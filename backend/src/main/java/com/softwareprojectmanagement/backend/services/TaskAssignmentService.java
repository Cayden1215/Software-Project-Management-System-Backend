package com.softwareprojectmanagement.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.softwareprojectmanagement.backend.dto.TaskAssignmentDto;

/**
 * Service interface for managing task assignments.
 * Handles resource allocation and scheduling of tasks by project managers.
 */
@Service
public interface TaskAssignmentService {

    /**
     * Create a new task assignment with allocated members and scheduling dates.
     * 
     * @param projectId The ID of the project
     * @param taskId The ID of the task
     * @param taskAssignmentDto The assignment details including members and schedule
     * @return The created TaskAssignmentDto
     */
    TaskAssignmentDto createTaskAssignment(Long projectId, Long taskId, TaskAssignmentDto taskAssignmentDto);

    /**
     * Get a task assignment by its task ID.
     * 
     * @param projectId The project ID
     * @param taskId The task ID
     * @return The TaskAssignmentDto
     */
    TaskAssignmentDto getTaskAssignment(Long projectId, Long taskId);

    /**
     * Get all task assignments for a specific project.
     * 
     * @param projectId The project ID
     * @return List of TaskAssignmentDto
     */
    List<TaskAssignmentDto> getTaskAssignmentsByProjectId(Long projectId);

    /**
     * Get the task assignment for a specific task.
     * 
     * @param taskId The task ID
     * @return The TaskAssignmentDto or null if not found
     */
    TaskAssignmentDto getTaskAssignmentByTaskId(Long taskId);

    /**
     * Update an existing task assignment (members and/or schedule).
     * 
     * @param projectId The project ID
     * @param taskId The task ID
     * @param taskAssignmentDto The updated assignment details
     * @return The updated TaskAssignmentDto
     */
    TaskAssignmentDto updateTaskAssignment(Long projectId, Long taskId, TaskAssignmentDto taskAssignmentDto);

    /**
     * Delete a task assignment.
     * 
     * @param projectId The project ID
     * @param taskId The task ID
     */
    void deleteTaskAssignment(Long projectId, Long taskId);

    /**
     * Allocate members to an existing task assignment.
     * 
     * @param projectId The project ID
     * @param taskId The task ID
     * @param memberIds List of project member IDs to allocate
     * @return The updated TaskAssignmentDto
     */
    TaskAssignmentDto allocateMembers(Long projectId, Long taskId, List<Long> memberIds);

    /**
     * Schedule a task assignment with start and end dates.
     * 
     * @param projectId The project ID
     * @param taskId The task ID
     * @param taskAssignmentDto Contains the scheduled start and end dates
     * @return The updated TaskAssignmentDto
     */
    TaskAssignmentDto scheduleTaskAssignment(Long projectId, Long taskId, TaskAssignmentDto taskAssignmentDto);

    /**
     * Remove a member from a task assignment.
     * 
     * @param projectId The project ID
     * @param taskId The task ID
     * @param memberId The project member ID to remove
     * @return The updated TaskAssignmentDto
     */
    TaskAssignmentDto removeMemberFromAssignment(Long projectId, Long taskId, Long memberId);
}
