package com.softwareprojectmanagement.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softwareprojectmanagement.backend.dto.TaskAssignmentDto;
import com.softwareprojectmanagement.backend.services.TaskAssignmentService;

/**
 * REST Controller for managing task assignments.
 * Allows project managers to manually allocate resources and schedule tasks.
 */
@RestController
@RequestMapping("/api/v1/project/{projectId}/tasks/assignments")
public class TaskAssignmentController {

    @Autowired
    private TaskAssignmentService taskAssignmentService;

    /**
     * Create a new task assignment with allocated members and scheduling dates.
     * 
     * POST /api/v1/projects/{projectId}/tasks/{taskId}/assignments
     * 
     * @param projectId The ID of the project
     * @param taskId The ID of the task
     * @param taskAssignmentDto The assignment details including members and schedule
     * @return ResponseEntity containing the created TaskAssignmentDto
     */
    @PostMapping("/{taskId}")
    public ResponseEntity<TaskAssignmentDto> createTaskAssignment(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestBody TaskAssignmentDto taskAssignmentDto) {
        TaskAssignmentDto createdAssignment = taskAssignmentService.createTaskAssignment(projectId, taskId, taskAssignmentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAssignment);
    }

    /**
     * Get a specific task assignment by task ID.
     * 
     * GET /api/v1/project/{projectId}/tasks/{taskId}/assignments
     * 
     * @param projectId The ID of the project
     * @param taskId The ID of the task
     * @return ResponseEntity containing the TaskAssignmentDto
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskAssignmentDto> getTaskAssignment(
            @PathVariable Long projectId,
            @PathVariable Long taskId) {
        TaskAssignmentDto assignment = taskAssignmentService.getTaskAssignment(projectId, taskId);
        return ResponseEntity.ok(assignment);
    }

    /**
     * Get all task assignments for a project.
     * 
     * GET /api/v1/project/{projectId}/assignments
     * 
     * @param projectId The ID of the project
     * @return ResponseEntity containing list of TaskAssignmentDto
     */
    @GetMapping
    public ResponseEntity<List<TaskAssignmentDto>> getTaskAssignmentsByProject(
            @PathVariable Long projectId) {
        List<TaskAssignmentDto> assignments = taskAssignmentService.getTaskAssignmentsByProjectId(projectId);
        return ResponseEntity.ok(assignments);
    }

    /**
     * Update an existing task assignment (members and/or schedule).
     * 
     * PUT /api/v1/project/{projectId}/tasks/{taskId}/assignments
     * 
     * @param projectId The ID of the project
     * @param taskId The ID of the task
     * @param taskAssignmentDto The updated assignment details
     * @return ResponseEntity containing the updated TaskAssignmentDto
     */
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskAssignmentDto> updateTaskAssignment(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestBody TaskAssignmentDto taskAssignmentDto) {
        TaskAssignmentDto updatedAssignment = taskAssignmentService.updateTaskAssignment(projectId, taskId, taskAssignmentDto);
        return ResponseEntity.ok(updatedAssignment);
    }

    /**
     * Allocate members to a task assignment.
     * 
     * POST /api/v1/project/{projectId}/tasks/{taskId}/assignments/allocate
     * 
     * @param projectId The ID of the project
     * @param taskId The ID of the task
     * @param taskAssignmentDto Contains the assignedMemberIds list
     * @return ResponseEntity containing the updated TaskAssignmentDto
     */

    /* 
    @PostMapping("/allocate")
    public ResponseEntity<TaskAssignmentDto> allocateMembers(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestBody TaskAssignmentDto taskAssignmentDto) {
        TaskAssignmentDto updatedAssignment = taskAssignmentService.allocateMembers(projectId, taskId, taskAssignmentDto.getAssignedMemberIds());
        return ResponseEntity.ok(updatedAssignment);
    }*/

    /**
     * Schedule a task assignment with start and end dates.
     * 
     * POST /api/v1/project/{projectId}/tasks/{taskId}/assignments/schedule
     * 
     * @param projectId The ID of the project
     * @param taskId The ID of the task
     * @param taskAssignmentDto Contains scheduledStartDate and scheduledEndDate
     * @return ResponseEntity containing the updated TaskAssignmentDto
     */
    @PostMapping("/schedule")
    public ResponseEntity<TaskAssignmentDto> scheduleTaskAssignment(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestBody TaskAssignmentDto taskAssignmentDto) {
        TaskAssignmentDto updatedAssignment = taskAssignmentService.scheduleTaskAssignment(projectId, taskId, taskAssignmentDto);
        return ResponseEntity.ok(updatedAssignment);
    }

    /**
     * Remove a member from a task assignment.
     * 
     * DELETE /api/v1/project/{projectId}/tasks/{taskId}/assignments/members/{memberId}
     * 
     * @param projectId The ID of the project
     * @param taskId The ID of the task
     * @param memberId The ID of the member to remove
     * @return ResponseEntity containing the updated TaskAssignmentDto
     */

    /*
    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<TaskAssignmentDto> removeMemberFromAssignment(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @PathVariable Long memberId) {
        TaskAssignmentDto updatedAssignment = taskAssignmentService.removeMemberFromAssignment(projectId, taskId, memberId);
        return ResponseEntity.ok(updatedAssignment);
    }
    */

    /**
     * Delete a task assignment.
     * 
     * DELETE /api/v1/project/{projectId}/tasks/{taskId}/assignments
     * 
     * @param projectId The ID of the project
     * @param taskId The ID of the task
     * @return ResponseEntity with no content status
     */
    /* 
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTaskAssignment(
            @PathVariable Long projectId,
            @PathVariable Long taskId) {
        taskAssignmentService.deleteTaskAssignment(projectId, taskId);
        return ResponseEntity.noContent().build();
    }
        */
}
