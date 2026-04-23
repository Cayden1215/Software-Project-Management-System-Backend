package com.softwareprojectmanagement.backend.services;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softwareprojectmanagement.backend.entities.Project;
import com.softwareprojectmanagement.backend.entities.ProjectMember;
import com.softwareprojectmanagement.backend.entities.Skill;
import com.softwareprojectmanagement.backend.entities.Task;
import com.softwareprojectmanagement.backend.entities.TaskAssignment;
import com.softwareprojectmanagement.backend.repositories.ProjectMemberRepository;
import com.softwareprojectmanagement.backend.repositories.ProjectRepository;
import com.softwareprojectmanagement.backend.repositories.TaskAssignmentRepository;
import com.softwareprojectmanagement.backend.repositories.TaskRepository;

@Service
public class GeneticAlgorithmSchedulerImpl implements GeneticAlgorithmScheduler {

    private static final int POPULATION_SIZE = 50;
    private static final int MAX_GENERATIONS = 100;
    private static final double MUTATION_RATE = 0.08; // 8%
    private static final double ELITISM_RATE = 0.1; // Keep top 10%

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private TaskAssignmentRepository taskAssignmentRepository;

    private Random random = new Random();

    // ==================== Inner Classes ====================

    /**
     * Represents a single scheduled task with its assignment details
     */
    private static class Gene {

        Task task;
        Set<ProjectMember> assignedMembers; // Multiple members per task
        int startTime; // Relative hours/days from project start

        Gene(Task task, Set<ProjectMember> assignedMembers, int startTime) {
            this.task = task;
            this.assignedMembers = new HashSet<>(assignedMembers);
            this.startTime = startTime;
        }

        Gene copy() {
            return new Gene(this.task, new HashSet<>(this.assignedMembers), this.startTime);
        }

        @Override
        public String toString() {
            String members = assignedMembers.stream()
                .map(m -> m.getTeamMember().getName())
                .collect(Collectors.joining(", "));
            return String.format("Gene[Task:%s, Members:[%s], StartTime:%d]", 
                task.getTaskName(), members, startTime);
        }
    }

    /**
     * Represents a complete project schedule (candidate solution)
     */
    private static class Chromosome {
        List<Gene> genes; // Ordered sequence of tasks
        int makeSpan; // Total project duration
        double fitnessScore;

        Chromosome(List<Gene> genes, int makeSpan, double fitnessScore) {
            this.genes = new ArrayList<>(genes);
            this.makeSpan = makeSpan;
            this.fitnessScore = fitnessScore;
        }

        Chromosome copy() {
            List<Gene> copiedGenes = this.genes.stream()
                    .map(Gene::copy)
                    .collect(Collectors.toList());
            return new Chromosome(copiedGenes, this.makeSpan, this.fitnessScore);
        }

        @Override
        public String toString() {
            return String.format("Chromosome[MakeSpan:%d, Fitness:%.6f, Tasks:%d]", 
                makeSpan, fitnessScore, genes.size());
        }
    }

    // ==================== Main GA Entry Point ====================

    @Override
    public void scheduleProject(Long projectID) {
        Project project = projectRepository.findById(projectID).orElseThrow();

        List<Task> allTasks = taskRepository.findByProject(project);
        List<ProjectMember> projectMembers = projectMemberRepository.findByTeamMemberUserID(
            project.getProjectMembers().stream()
                .map(pm -> pm.getTeamMember().getUserID())
                .findFirst()
                .orElse(null)
        );

        if (allTasks.isEmpty() || projectMembers.isEmpty()) {
            throw new IllegalArgumentException("Project must have tasks and team members for scheduling");
        }

        // Get project members directly from project
        List<ProjectMember> projectTeamMembers = new ArrayList<>(project.getProjectMembers());

        // Phase 1: Initialize Population
        List<Chromosome> population = initializePopulation(allTasks, projectTeamMembers);

        // Phase 2-5: Evolution Loop
        for (int generation = 0; generation < MAX_GENERATIONS; generation++) {
            // Evaluate fitness for all chromosomes
            for (Chromosome chromosome : population) {
                evaluateFitness(chromosome, allTasks, project);
            }

            // Sort by fitness (descending)
            population.sort((a, b) -> Double.compare(b.fitnessScore, a.fitnessScore));

            // Selection + Elitism
            List<Chromosome> selectedParents = selectParents(population);

            // Crossover to create offspring
            List<Chromosome> offspring = new ArrayList<>();
            for (int i = 0; i < population.size() - (int)(POPULATION_SIZE * ELITISM_RATE); i += 2) {
                Chromosome parent1 = selectedParents.get(i % selectedParents.size());
                Chromosome parent2 = selectedParents.get((i + 1) % selectedParents.size());
                
                Chromosome[] children = crossover(parent1, parent2, allTasks, projectTeamMembers);
                offspring.add(children[0]);
                if (offspring.size() < POPULATION_SIZE - (int)(POPULATION_SIZE * ELITISM_RATE)) {
                    offspring.add(children[1]);
                }
            }

            // Restore topological order after crossover
            for (Chromosome chromosome : offspring) {
                sortChromosomeTopologically(chromosome);
            }

            // Apply mutation
            for (Chromosome chromosome : offspring) {
                if (random.nextDouble() < MUTATION_RATE) {
                    mutate(chromosome, allTasks, projectTeamMembers);
                }
            }

            // Restore topological order after mutation
            for (Chromosome chromosome : offspring) {
                sortChromosomeTopologically(chromosome);
            }

            // Merge elite solutions with offspring
            int eliteCount = (int)(POPULATION_SIZE * ELITISM_RATE);
            List<Chromosome> newPopulation = new ArrayList<>();
            for (int i = 0; i < eliteCount && i < population.size(); i++) {
                newPopulation.add(population.get(i).copy());
            }
            newPopulation.addAll(offspring);
            population = newPopulation;
        }

        // Final evaluation and selection of best solution
        for (Chromosome chromosome : population) {
            evaluateFitness(chromosome, allTasks, project);
        }
        population.sort((a, b) -> Double.compare(b.fitnessScore, a.fitnessScore));
        Chromosome bestSchedule = population.get(0);

        // Phase 6: Persist the optimal schedule
        persistSchedule(bestSchedule, project);
    }

    // ==================== Phase A: Initialization ====================

    /**
     * Generate initial population of random candidate schedules
     * Constraint: Ensure skill matching for task assignments
     * Each task is assigned to multiple members based on task.getRequiredMemberNum()
     */
    private List<Chromosome> initializePopulation(List<Task> allTasks, List<ProjectMember> projectMembers) {
        List<Chromosome> population = new ArrayList<>();

        for (int i = 0; i < POPULATION_SIZE; i++) {
            // Create a random topological order of tasks (respecting dependencies)
            List<Task> taskSequence = generateRandomTopologicalOrder(allTasks);
            
            List<Gene> genes = new ArrayList<>();
            for (Task task : taskSequence) {
                // Find compatible team members with required skills
                Set<ProjectMember> compatibleMembers = findCompatibleMembers(task, projectMembers);
                
                if (!compatibleMembers.isEmpty()) {
                    // Assign the required number of members, or all available if fewer than required
                    int requiredCount = task.getRequiredMemberNum() != null ? task.getRequiredMemberNum() : 1;
                    Set<ProjectMember> assignedMembers = selectRandomMembers(compatibleMembers, requiredCount);
                    
                    if (!assignedMembers.isEmpty()) {
                        genes.add(new Gene(task, assignedMembers, 0)); // Temporary startTime, will be calculated
                    }
                }
            }

            if (!genes.isEmpty()) {
                Chromosome chromosome = new Chromosome(genes, 0, 0);
                population.add(chromosome);
            }
        }












        //Suspect Duplicated


        // Ensure we have at least POPULATION_SIZE chromosomes
        while (population.size() < POPULATION_SIZE) {
            List<Task> taskSequence = generateRandomTopologicalOrder(allTasks);
            List<Gene> genes = new ArrayList<>();
            for (Task task : taskSequence) {
                Set<ProjectMember> compatibleMembers = findCompatibleMembers(task, projectMembers);
                if (!compatibleMembers.isEmpty()) {
                    int requiredCount = task.getRequiredMemberNum() != null ? task.getRequiredMemberNum() : 1;
                    Set<ProjectMember> assignedMembers = selectRandomMembers(compatibleMembers, requiredCount);
                    if (!assignedMembers.isEmpty()) {
                        genes.add(new Gene(task, assignedMembers, 0));
                    }
                }
            }
            if (!genes.isEmpty()) {
                Chromosome chromosome = new Chromosome(genes, 0, 0);
                population.add(chromosome);
            }
        }

        return population.subList(0, POPULATION_SIZE);
    }

    /**
     * Generate a random topological order of tasks respecting dependencies
     */
    private List<Task> generateRandomTopologicalOrder(List<Task> tasks) {
        List<Task> result = new ArrayList<>();
        Set<Task> visited = new HashSet<>();
        Set<Task> tempVisited = new HashSet<>();

        // Create a copy of tasks for shuffling
        List<Task> remainingTasks = new ArrayList<>(tasks);
        Collections.shuffle(remainingTasks);

        for (Task task : remainingTasks) {
            topologicalSortUtil(task, visited, tempVisited, result, tasks);
        }

        return result;
    }

    /**
     * Utility for DFS-based topological sorting
     */
    private void topologicalSortUtil(Task task, Set<Task> visited, Set<Task> tempVisited, 
                                     List<Task> result, List<Task> allTasks) {
        if (visited.contains(task)) {
            return;
        }

        tempVisited.add(task);

        // Visit all dependencies first
        for (Task dependency : task.getDependencies()) {
            if (!visited.contains(dependency)) {
                topologicalSortUtil(dependency, visited, tempVisited, result, allTasks);
            }
        }

        tempVisited.remove(task);
        visited.add(task);
        result.add(task);
    }

    /**
     * Re-sort a chromosome's genes to maintain topological order
     * This is necessary after crossover and mutation operations that may violate task dependencies
     */
    private void sortChromosomeTopologically(Chromosome chromosome) {
        Map<Task, Gene> taskToGene = new HashMap<>();
        for (Gene gene : chromosome.genes) {
            taskToGene.put(gene.task, gene);
        }

        List<Gene> sortedGenes = new ArrayList<>();
        Set<Task> visited = new HashSet<>();
        Set<Task> tempVisited = new HashSet<>();

        // Perform topological sort on genes
        for (Gene gene : chromosome.genes) {
            topologicalSortGeneUtil(gene.task, visited, tempVisited, sortedGenes, taskToGene);
        }

        chromosome.genes = sortedGenes;
    }

    /**
     * Utility for DFS-based topological sorting of genes within a chromosome
     */
    private void topologicalSortGeneUtil(Task task, Set<Task> visited, Set<Task> tempVisited,
                                         List<Gene> result, Map<Task, Gene> taskToGene) {
        if (visited.contains(task)) {
            return;
        }

        tempVisited.add(task);

        // Visit all dependencies first
        for (Task dependency : task.getDependencies()) {
            if (taskToGene.containsKey(dependency) && !visited.contains(dependency)) {
                topologicalSortGeneUtil(dependency, visited, tempVisited, result, taskToGene);
            }
        }

        tempVisited.remove(task);
        visited.add(task);
        result.add(taskToGene.get(task));
    }

    /**
     * Find all compatible ProjectMembers with required skills for a task
     */
    private Set<ProjectMember> findCompatibleMembers(Task task, List<ProjectMember> projectMembers) {
        return projectMembers.stream()
            .filter(member -> hasAllRequiredSkills(member, task.getSkills()))
            .collect(Collectors.toSet());
    }

    /**
     * Select a random subset of members from compatible members.
     * Selects up to 'count' members randomly from the provided set.
     */
    private Set<ProjectMember> selectRandomMembers(Set<ProjectMember> compatibleMembers, int count) {
        Set<ProjectMember> selected = new HashSet<>();
        List<ProjectMember> list = new ArrayList<>(compatibleMembers);
        Collections.shuffle(list, random);
        
        int selectCount = Math.min(count, list.size());
        for (int i = 0; i < selectCount; i++) {
            selected.add(list.get(i));
        }
        
        return selected;
    }

    /**
     * Check if a project member possesses all required skills
     */
    private boolean hasAllRequiredSkills(ProjectMember member, Set<Skill> requiredSkills) {
        Set<Skill> memberSkills = member.getSkills();
        return memberSkills.containsAll(requiredSkills);
    }

    // ==================== Phase B: Fitness Evaluation ====================

    /**
     * Evaluate fitness of a candidate schedule
     * Constraints:
     * 1. Task dependencies must be respected
     * 2. Resource availability must be tracked for each member
     * 3. Multiple members can be assigned to a task
     * 4. MakeSpan = maximum completion time of all tasks
     * 5. Fitness Score = 1.0 / makeSpan
     */
    private void evaluateFitness(Chromosome chromosome, List<Task> allTasks, Project project) {
        // Map tasks to genes for easy lookup
        Map<Task, Gene> taskToGene = new HashMap<>();
        for (Gene gene : chromosome.genes) {
            taskToGene.put(gene.task, gene);
        }

        // Track resource availability for each ProjectMember
        Map<ProjectMember, Integer> memberAvailability = new HashMap<>();
        for (Gene gene : chromosome.genes) {
            for (ProjectMember member : gene.assignedMembers) {
                memberAvailability.putIfAbsent(member, 0);
            }
        }

        // Calculate start and end times for each task
        int maxEndTime = 0;
        for (Gene gene : chromosome.genes) {
            Task task = gene.task;
            int earliestStart = 0;

            // Constraint 1: Task dependencies - cannot start before all predecessors finish
            for (Task dependency : task.getDependencies()) {
                if (taskToGene.containsKey(dependency)) {
                    Gene depGene = taskToGene.get(dependency);
                    int depEndTime = depGene.startTime + dependency.getEstimatedDuration();
                    earliestStart = Math.max(earliestStart, depEndTime);
                }
            }

            // Constraint 2: Resource availability - find earliest slot where ALL assigned members are free
            int latestMemberFreeTime = 0;
            for (ProjectMember member : gene.assignedMembers) {
                int memberFreeTime = memberAvailability.getOrDefault(member, 0);
                latestMemberFreeTime = Math.max(latestMemberFreeTime, memberFreeTime);
            }
            
            int startTime = Math.max(earliestStart, latestMemberFreeTime);

            // Assign the calculated start time
            gene.startTime = startTime;

            // Update availability for all assigned members
            int endTime = startTime + task.getEstimatedDuration();
            for (ProjectMember member : gene.assignedMembers) {
                memberAvailability.put(member, endTime);
            }

            // Track maximum end time (makeSpan)
            maxEndTime = Math.max(maxEndTime, endTime);
        }

        // Set chromosome properties
        chromosome.makeSpan = maxEndTime;
        chromosome.fitnessScore = (maxEndTime > 0) ? 1.0 / maxEndTime : 0.0;
    }

    // ==================== Phase C: Selection ====================

    /**
     * Select parents using Tournament Selection (better than simple proportional selection)
     * Top 50% of population becomes parents
     */
    private List<Chromosome> selectParents(List<Chromosome> population) {
        int parentCount = (int)(population.size() * 0.5); // Top 50%
        return population.subList(0, Math.min(parentCount, population.size()));
    }

    // ==================== Phase D: Crossover ====================

    /**
     * Generate child schedules from two parent schedules
     * 1. Order Crossover (Task Sequence): Preserve task sequences
     * 2. Resource Crossover (Team Assignment): 50% probability for each parent's assignment
     */
    private Chromosome[] crossover(Chromosome parent1, Chromosome parent2, 
                                   List<Task> allTasks, List<ProjectMember> projectMembers) {
        // Order Crossover - maintain task sequence
        int crossoverPoint1 = random.nextInt(parent1.genes.size());
        int crossoverPoint2 = random.nextInt(parent1.genes.size());
        if (crossoverPoint1 > crossoverPoint2) {
            int temp = crossoverPoint1;
            crossoverPoint1 = crossoverPoint2;
            crossoverPoint2 = temp;
        }

        // Child 1: Take segment from Parent1, fill gaps from Parent2
        List<Gene> child1Genes = createCrossoverChild(parent1.genes, parent2.genes, 
                                                      crossoverPoint1, crossoverPoint2, 
                                                      projectMembers);

        // Child 2: Take segment from Parent2, fill gaps from Parent1
        List<Gene> child2Genes = createCrossoverChild(parent2.genes, parent1.genes, 
                                                      crossoverPoint1, crossoverPoint2, 
                                                      projectMembers);

        Chromosome child1 = new Chromosome(child1Genes, 0, 0);
        Chromosome child2 = new Chromosome(child2Genes, 0, 0);

        return new Chromosome[]{child1, child2};
    }

    /**
     * Helper method for Order Crossover that handles multiple member assignments
     */
    private List<Gene> createCrossoverChild(List<Gene> primaryParent, List<Gene> secondaryParent,
                                           int point1, int point2, List<ProjectMember> projectMembers) {
        List<Gene> child = new ArrayList<>();
        Set<Task> usedTasks = new HashSet<>();

        // Take segment from primary parent
        for (int i = point1; i < point2 && i < primaryParent.size(); i++) {
            Gene gene = primaryParent.get(i).copy();
            child.add(gene);
            usedTasks.add(gene.task);
        }

        // Fill remaining from secondary parent (maintaining order)
        for (Gene parentGene : secondaryParent) {
            if (!usedTasks.contains(parentGene.task)) {
                Set<ProjectMember> assignedMembers;
                
                // Resource Crossover: 50% chance to inherit from secondary parent assignment
                if (random.nextDouble() < 0.5) {
                    assignedMembers = new HashSet<>(parentGene.assignedMembers);
                } else {
                    // Otherwise, find another compatible member set
                    Set<ProjectMember> compatible = findCompatibleMembers(parentGene.task, projectMembers);
                    int requiredCount = parentGene.task.getRequiredMemberNum() != null ? 
                        parentGene.task.getRequiredMemberNum() : 1;
                    assignedMembers = selectRandomMembers(compatible, requiredCount);
                    
                    // If we can't find enough compatible members, use parent's assignment
                    if (assignedMembers.isEmpty()) {
                        assignedMembers = new HashSet<>(parentGene.assignedMembers);
                    }
                }
                
                Gene newGene = new Gene(parentGene.task, assignedMembers, 0);
                child.add(newGene);
                usedTasks.add(parentGene.task);
            }
        }

        return child;
    }

    // ==================== Phase E: Mutation ====================

    /**
     * Apply random variations to escape local optima
     * 1. Swap Mutation: Swap two random task positions
     * 2. Reassignment Mutation: Reassign a task to different compatible members
     */
    private void mutate(Chromosome chromosome, List<Task> allTasks, List<ProjectMember> projectMembers) {
        if (chromosome.genes.isEmpty()) {
            return;
        }

        // Mutation type selection
        double mutationType = random.nextDouble();

        if (mutationType < 0.5 && chromosome.genes.size() > 1) {
            // Swap Mutation: Randomly swap two task positions
            int pos1 = random.nextInt(chromosome.genes.size());
            int pos2 = random.nextInt(chromosome.genes.size());
            
            Gene temp = chromosome.genes.get(pos1);
            chromosome.genes.set(pos1, chromosome.genes.get(pos2));
            chromosome.genes.set(pos2, temp);
        } else {
            // Reassignment Mutation: Reassign members for a task to different compatible members
            int taskIndex = random.nextInt(chromosome.genes.size());
            Gene gene = chromosome.genes.get(taskIndex);
            
            Set<ProjectMember> compatibleMembers = findCompatibleMembers(gene.task, projectMembers);
            
            // Filter out currently assigned members to get alternatives
            Set<ProjectMember> alternativeMembers = compatibleMembers.stream()
                .filter(member -> !gene.assignedMembers.contains(member))
                .collect(Collectors.toSet());
            
            if (!alternativeMembers.isEmpty()) {
                int requiredCount = gene.task.getRequiredMemberNum() != null ? 
                    gene.task.getRequiredMemberNum() : 1;
                Set<ProjectMember> newMembers = selectRandomMembers(alternativeMembers, requiredCount);
                
                if (!newMembers.isEmpty()) {
                    gene.assignedMembers = newMembers;
                }
            }
        }
    }

    // ==================== Phase F: Persistence ====================

    /**
     * Save the optimal schedule to database
     * Convert relative integer startTime to actual LocalDate based on project start date
     * Each task assignment now includes multiple assigned members
     */
    private void persistSchedule(Chromosome bestSchedule, Project project) {
        LocalDate projectStartDate = project.getStartDate();
        if (projectStartDate == null) {
            projectStartDate = LocalDate.now();
        }

        // Clear any existing task assignments for this project
        List<TaskAssignment> existingAssignments = taskAssignmentRepository.findByProject(project);
        taskAssignmentRepository.deleteAll(existingAssignments);

        // Create and persist new task assignments
        for (Gene gene : bestSchedule.genes) {
            TaskAssignment assignment = new TaskAssignment();
            assignment.setProject(project);
            assignment.setTask(gene.task);
            assignment.setAssignedMembers(new HashSet<>(gene.assignedMembers));
            
            // Convert relative time (hours/days) to absolute LocalDate
            LocalDate scheduledStartDate = projectStartDate.plusDays(gene.startTime);
            LocalDate scheduledEndDate = scheduledStartDate.plusDays(gene.task.getEstimatedDuration());
            
            assignment.setScheduledStartDate(scheduledStartDate);
            assignment.setScheduledEndDate(scheduledEndDate);
            
            // Validate member count before persistence
            if (!assignment.isValidMemberCount()) {
                throw new IllegalArgumentException(assignment.getValidationError());
            }
            
            taskAssignmentRepository.save(assignment);
        }
    }

}
