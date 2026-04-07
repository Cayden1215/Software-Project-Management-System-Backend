package com.softwareprojectmanagement.backend.services.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softwareprojectmanagement.backend.dto.TaskAssignmentDto;
import com.softwareprojectmanagement.backend.entities.Project;
import com.softwareprojectmanagement.backend.entities.ProjectMember;
import com.softwareprojectmanagement.backend.entities.Task;
import com.softwareprojectmanagement.backend.entities.TaskAssignment;
import com.softwareprojectmanagement.backend.mappers.TaskAssignmentMapper;
import com.softwareprojectmanagement.backend.repositories.ProjectMemberRepository;
import com.softwareprojectmanagement.backend.repositories.ProjectRepository;
import com.softwareprojectmanagement.backend.repositories.TaskAssignmentRepository;
import com.softwareprojectmanagement.backend.repositories.TaskRepository;
import com.softwareprojectmanagement.backend.services.TaskAssignmentService;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Implementation of TaskAssignmentService for managing resource allocation and scheduling.
 */
@Service
@AllArgsConstructor
@NoArgsConstructor
public class TaskAssignmentServiceImpl implements TaskAssignmentService {

    @Autowired
    private TaskAssignmentRepository taskAssignmentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Override
    public TaskAssignmentDto createTaskAssignment(Long projectId, Long taskId, TaskAssignmentDto taskAssignmentDto) {
        // Validate project exists
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        // Validate task exists and belongs to project
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        if (!task.getProject().getProjectID().equals(projectId)) {
            throw new RuntimeException("Task does not belong to the specified project");
        }

        // Create new task assignment
        TaskAssignment taskAssignment = new TaskAssignment();
        taskAssignment.setTask(task);
        taskAssignment.setProject(project);
        taskAssignment.setScheduledStartDate(taskAssignmentDto.getScheduledStartDate());
        taskAssignment.setScheduledEndDate(taskAssignmentDto.getScheduledEndDate());

        // Allocate members if provided
        Set<ProjectMember> assignedMembers = new HashSet<>();
        if (taskAssignmentDto.getAssignedMemberIds() != null && !taskAssignmentDto.getAssignedMemberIds().isEmpty()) {
            for (Long memberId : taskAssignmentDto.getAssignedMemberIds()) {
                ProjectMember member = projectMemberRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("Project member not found with ID: " + memberId));
                
                // Verify member belongs to project
                if (!member.getProject().getProjectID().equals(projectId)) {
                    throw new RuntimeException("Project member does not belong to the specified project");
                }
                
                assignedMembers.add(member);
            }
        }
        taskAssignment.setAssignedMembers(assignedMembers);

        // Validate member count if required
        String validationError = taskAssignment.getValidationError();
        if (validationError != null) {
            throw new RuntimeException(validationError);
        }

        TaskAssignment savedAssignment = taskAssignmentRepository.save(taskAssignment);
        return TaskAssignmentMapper.mapToTaskAssignmentDto(savedAssignment);
    }

    @Override
    public TaskAssignmentDto getTaskAssignmentById(Long assignmentId) {
        TaskAssignment taskAssignment = taskAssignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new RuntimeException("Task assignment not found with ID: " + assignmentId));
        return TaskAssignmentMapper.mapToTaskAssignmentDto(taskAssignment);
    }

    @Override
    public List<TaskAssignmentDto> getTaskAssignmentsByProjectId(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        List<TaskAssignment> assignments = taskAssignmentRepository.findByProject(project);
        return assignments.stream()
            .map(TaskAssignmentMapper::mapToTaskAssignmentDto)
            .collect(Collectors.toList());
    }

    @Override
    public TaskAssignmentDto getTaskAssignmentByTaskId(Long taskId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        return taskRepository.findById(taskId)
            .map(t -> t.getTaskAssignment())
            .filter(assignment -> assignment != null)
            .map(TaskAssignmentMapper::mapToTaskAssignmentDto)
            .orElse(null);
    }

    @Override
    public TaskAssignmentDto updateTaskAssignment(Long assignmentId, TaskAssignmentDto taskAssignmentDto) {
        TaskAssignment taskAssignment = taskAssignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new RuntimeException("Task assignment not found with ID: " + assignmentId));

        // Update scheduling dates
        if (taskAssignmentDto.getScheduledStartDate() != null) {
            taskAssignment.setScheduledStartDate(taskAssignmentDto.getScheduledStartDate());
        }
        if (taskAssignmentDto.getScheduledEndDate() != null) {
            taskAssignment.setScheduledEndDate(taskAssignmentDto.getScheduledEndDate());
        }

        // Update members if provided
        if (taskAssignmentDto.getAssignedMemberIds() != null) {
            Set<ProjectMember> assignedMembers = new HashSet<>();
            for (Long memberId : taskAssignmentDto.getAssignedMemberIds()) {
                ProjectMember member = projectMemberRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("Project member not found with ID: " + memberId));
                
                // Verify member belongs to project
                if (!member.getProject().getProjectID().equals(taskAssignment.getProject().getProjectID())) {
                    throw new RuntimeException("Project member does not belong to the project");
                }
                
                assignedMembers.add(member);
            }
            taskAssignment.setAssignedMembers(assignedMembers);
        }

        // Validate member count
        String validationError = taskAssignment.getValidationError();
        if (validationError != null) {
            throw new RuntimeException(validationError);
        }

        TaskAssignment updatedAssignment = taskAssignmentRepository.save(taskAssignment);
        return TaskAssignmentMapper.mapToTaskAssignmentDto(updatedAssignment);
    }

    @Override
    public void deleteTaskAssignment(Long assignmentId) {
        if (!taskAssignmentRepository.existsById(assignmentId)) {
            throw new RuntimeException("Task assignment not found with ID: " + assignmentId);
        }
        taskAssignmentRepository.deleteById(assignmentId);
    }

    @Override
    public TaskAssignmentDto allocateMembers(Long assignmentId, List<Long> memberIds) {
        TaskAssignment taskAssignment = taskAssignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new RuntimeException("Task assignment not found with ID: " + assignmentId));

        Set<ProjectMember> assignedMembers = new HashSet<>();
        for (Long memberId : memberIds) {
            ProjectMember member = projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Project member not found with ID: " + memberId));
            
            // Verify member belongs to project
            if (!member.getProject().getProjectID().equals(taskAssignment.getProject().getProjectID())) {
                throw new RuntimeException("Project member does not belong to the project");
            }
            
            assignedMembers.add(member);
        }
        taskAssignment.setAssignedMembers(assignedMembers);

        // Validate member count
        String validationError = taskAssignment.getValidationError();
        if (validationError != null) {
            throw new RuntimeException(validationError);
        }

        TaskAssignment updatedAssignment = taskAssignmentRepository.save(taskAssignment);
        return TaskAssignmentMapper.mapToTaskAssignmentDto(updatedAssignment);
    }

    @Override
    public TaskAssignmentDto scheduleTaskAssignment(Long assignmentId, TaskAssignmentDto taskAssignmentDto) {
        TaskAssignment taskAssignment = taskAssignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new RuntimeException("Task assignment not found with ID: " + assignmentId));

        if (taskAssignmentDto.getScheduledStartDate() == null) {
            throw new RuntimeException("Scheduled start date is required");
        }

        taskAssignment.setScheduledStartDate(taskAssignmentDto.getScheduledStartDate());
        if (taskAssignmentDto.getScheduledEndDate() != null) {
            taskAssignment.setScheduledEndDate(taskAssignmentDto.getScheduledEndDate());
        }

        TaskAssignment updatedAssignment = taskAssignmentRepository.save(taskAssignment);
        return TaskAssignmentMapper.mapToTaskAssignmentDto(updatedAssignment);
    }

    @Override
    public TaskAssignmentDto removeMemberFromAssignment(Long assignmentId, Long memberId) {
        TaskAssignment taskAssignment = taskAssignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new RuntimeException("Task assignment not found with ID: " + assignmentId));

        ProjectMember member = projectMemberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("Project member not found with ID: " + memberId));

        taskAssignment.getAssignedMembers().remove(member);

        // Validate member count
        String validationError = taskAssignment.getValidationError();
        if (validationError != null) {
            throw new RuntimeException(validationError);
        }

        TaskAssignment updatedAssignment = taskAssignmentRepository.save(taskAssignment);
        return TaskAssignmentMapper.mapToTaskAssignmentDto(updatedAssignment);
    }
}
