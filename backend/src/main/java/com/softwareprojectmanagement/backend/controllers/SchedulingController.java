package com.softwareprojectmanagement.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softwareprojectmanagement.backend.dto.TaskAssignmentDto;
import com.softwareprojectmanagement.backend.services.GeneticAlgorithmScheduler;
import com.softwareprojectmanagement.backend.services.ProjectService;

import lombok.AllArgsConstructor;

import java.util.List;

/**
 * REST Controller for managing project scheduling operations.
 * Provides endpoints to trigger genetic algorithm-based scheduling and retrieve task assignments.
 * 
 * Endpoints are secured to PROJECT_MANAGER role only.
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/scheduling")
public class SchedulingController {

    private GeneticAlgorithmScheduler geneticAlgorithmScheduler;
    private ProjectService projectService;

    /**
     * Activates the genetic algorithm scheduler for a specific project.
     * 
     * @param projectId the ID of the project to schedule
     * @return ResponseEntity with success message once scheduling completes
     * @throws RuntimeException if the project ID is not found (handled by GlobalExceptionHandler)
     */
    @PostMapping("/project/{projectId}/run")
    public ResponseEntity<String> activateScheduler(@PathVariable Long projectId) {
        // Verify project exists before scheduling
        projectService.getProjectEntityById(projectId);

        
        // Execute the genetic algorithm scheduling
        geneticAlgorithmScheduler.scheduleProject(projectId);
        
        return ResponseEntity.status(HttpStatus.OK)
                .body("Scheduling process for project " + projectId + " completed successfully.");
    }

    /**
     * Retrieves all task assignments for a specific project.
     * 
     * @param projectId the ID of the project
     * @return ResponseEntity containing a list of TaskAssignmentDto objects
     * @throws RuntimeException if the project ID is not found (handled by GlobalExceptionHandler)
     */
    @GetMapping("/project/{projectId}/assignments")
    public ResponseEntity<List<TaskAssignmentDto>> getProjectAssignments(@PathVariable Long projectId) {
        // Verify project exists
        projectService.getProjectEntityById(projectId);
        
        // Retrieve all task assignments for the project
        List<TaskAssignmentDto> assignments = projectService.getProjectTaskAssignments(projectId);
        
        return ResponseEntity.status(HttpStatus.OK).body(assignments);
    }
}
