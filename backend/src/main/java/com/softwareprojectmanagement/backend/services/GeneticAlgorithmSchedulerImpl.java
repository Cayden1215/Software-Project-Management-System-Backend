package com.softwareprojectmanagement.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softwareprojectmanagement.backend.entities.Project;
import com.softwareprojectmanagement.backend.entities.Task;
import com.softwareprojectmanagement.backend.entities.TeamMember;

@Service
public class GeneticAlgorithmSchedulerImpl implements GeneticAlgorithmScheduler{

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    @Override
    public void scheduleProject(Long projectID) {
        Project project = projectService.getProjectEntityById(projectID);

        List<Task> tasks = taskService.listTaskEntitiesByProjectId(projectID);

        List<TeamMember> teamMembers = projectService.getProjectTeamMembers(project);

        initializePopulation(tasks, teamMembers);

        //List<TeamMember> teamMembers = projectService.
    }

    private void initializePopulation(List<Task> tasks, List<TeamMember> teamMembers) {
        for (Task task : tasks) {
            
            for (TeamMember teamMember : teamMembers) {
                // Create a schedule assigning the task to the team member
            }
            
        }
    }

    private void evaluateFitness() {
        // Evaluate the fitness of each schedule
    }

    private void selectParents() {
        // Select parents for crossover
    }

    private void crossover() {
        // Perform crossover to create offspring
    }

    private void mutate() {
        // Perform mutation on offspring
    }


}
