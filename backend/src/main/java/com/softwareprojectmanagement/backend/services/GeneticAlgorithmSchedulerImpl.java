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
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

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
        ProjectMember assignedMember;
        int startTime; // Relative hours/days from project start

        Gene(Task task, ProjectMember assignedMember, int startTime) {
            this.task = task;
            this.assignedMember = assignedMember;
            this.startTime = startTime;
        }

        Gene copy() {
            return new Gene(this.task, this.assignedMember, this.startTime);
        }

        @Override
        public String toString() {
            return String.format("Gene[Task:%s, Member:%s, StartTime:%d]", 
                task.getTaskName(), assignedMember.getTeamMember().getName(), startTime);
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

            // Apply mutation
            for (Chromosome chromosome : offspring) {
                if (random.nextDouble() < MUTATION_RATE) {
                    mutate(chromosome, allTasks, projectTeamMembers);
                }
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
     */
    private List<Chromosome> initializePopulation(List<Task> allTasks, List<ProjectMember> projectMembers) {
        List<Chromosome> population = new ArrayList<>();

        for (int i = 0; i < POPULATION_SIZE; i++) {
            // Create a random topological order of tasks (respecting dependencies)
            List<Task> taskSequence = generateRandomTopologicalOrder(allTasks);
            
            List<Gene> genes = new ArrayList<>();
            for (Task task : taskSequence) {
                // Find a compatible team member with required skills
                ProjectMember compatibleMember = findCompatibleMember(task, projectMembers);
                if (compatibleMember != null) {
                    genes.add(new Gene(task, compatibleMember, 0)); // Temporary startTime, will be calculated
                }
            }

            if (!genes.isEmpty()) {
                Chromosome chromosome = new Chromosome(genes, 0, 0);
                population.add(chromosome);
            }
        }

        // Ensure we have at least POPULATION_SIZE chromosomes
        while (population.size() < POPULATION_SIZE) {
            List<Task> taskSequence = generateRandomTopologicalOrder(allTasks);
            List<Gene> genes = new ArrayList<>();
            for (Task task : taskSequence) {
                ProjectMember compatibleMember = findCompatibleMember(task, projectMembers);
                if (compatibleMember != null) {
                    genes.add(new Gene(task, compatibleMember, 0));
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
     * Find a compatible ProjectMember with all required skills for a task
     */
    private ProjectMember findCompatibleMember(Task task, List<ProjectMember> projectMembers) {
        List<ProjectMember> compatible = projectMembers.stream()
            .filter(member -> hasAllRequiredSkills(member, task.getSkills()))
            .collect(Collectors.toList());

        if (compatible.isEmpty()) {
            return null;
        }

        return compatible.get(random.nextInt(compatible.size()));
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
     * 2. Resource availability must be tracked
     * 3. MakeSpan = maximum completion time of all tasks
     * 4. Fitness Score = 1.0 / makeSpan
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
            memberAvailability.put(gene.assignedMember, 0);
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

            // Constraint 2: Resource availability - find earliest slot where member is free
            ProjectMember member = gene.assignedMember;
            int memberFreeTime = memberAvailability.getOrDefault(member, 0);
            int startTime = Math.max(earliestStart, memberFreeTime);

            // Assign the calculated start time
            gene.startTime = startTime;

            // Update member availability
            int endTime = startTime + task.getEstimatedDuration();
            memberAvailability.put(member, endTime);

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
     * Helper method for Order Crossover
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
                Gene newGene = new Gene(parentGene.task, parentGene.assignedMember, 0);
                
                // Resource Crossover: 50% chance to inherit from secondary parent assignment
                if (random.nextDouble() < 0.5) {
                    newGene.assignedMember = parentGene.assignedMember;
                } else {
                    // Otherwise, find another compatible member (or keep secondary)
                    ProjectMember compatible = findCompatibleMember(parentGene.task, projectMembers);
                    if (compatible != null) {
                        newGene.assignedMember = compatible;
                    }
                }
                
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
     * 2. Reassignment Mutation: Reassign a task to a different compatible member
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
            // Reassignment Mutation: Reassign a task to a different compatible member
            int taskIndex = random.nextInt(chromosome.genes.size());
            Gene gene = chromosome.genes.get(taskIndex);
            
            List<ProjectMember> compatibleMembers = projectMembers.stream()
                .filter(member -> hasAllRequiredSkills(member, gene.task.getSkills())
                        && !member.equals(gene.assignedMember))
                .collect(Collectors.toList());
            
            if (!compatibleMembers.isEmpty()) {
                gene.assignedMember = compatibleMembers.get(random.nextInt(compatibleMembers.size()));
            }
        }
    }

    // ==================== Phase F: Persistence ====================

    /**
     * Save the optimal schedule to database
     * Convert relative integer startTime to actual LocalDate based on project start date
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
            assignment.setAssignedMember(gene.assignedMember);
            
            // Convert relative time (hours/days) to absolute LocalDate
            LocalDate scheduledStartDate = projectStartDate.plusDays(gene.startTime);
            LocalDate scheduledEndDate = scheduledStartDate.plusDays(gene.task.getEstimatedDuration());
            
            assignment.setScheduledStartDate(scheduledStartDate);
            assignment.setScheduledEndDate(scheduledEndDate);
            
            taskAssignmentRepository.save(assignment);
        }
    }

}
