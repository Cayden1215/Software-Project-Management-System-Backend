package com.softwareprojectmanagement.backend.mappers;

import com.softwareprojectmanagement.backend.dto.TaskAssignmentDto;
import com.softwareprojectmanagement.backend.entities.TaskAssignment;
import lombok.Getter;
import lombok.Setter;

/**
 * Mapper for TaskAssignment entity to/from TaskAssignmentDto
 * Handles conversion between entities and DTOs for TaskAssignment with multiple member support
 */
@Setter
@Getter
public class TaskAssignmentMapper {

    /**
     * Convert TaskAssignment entity to TaskAssignmentDto
     * Converts the Set<ProjectMember> to lists of member IDs and names
     * 
     * @param taskAssignment the entity to convert
     * @return the corresponding DTO
     */
    public static TaskAssignmentDto mapToTaskAssignmentDto(TaskAssignment taskAssignment) {
        if (taskAssignment == null) {
            return null;
        }

        TaskAssignmentDto dto = new TaskAssignmentDto();
        dto.setAssignmentID(taskAssignment.getAssignmentID());
        dto.setTaskID(taskAssignment.getTask().getTaskID());
        dto.setTaskName(taskAssignment.getTask().getTaskName());
        dto.setRequiredMemberNum(taskAssignment.getTask().getRequiredMemberNum());
        
        // Convert Set<ProjectMember> to lists of IDs and names
        if (taskAssignment.getAssignedMembers() != null && !taskAssignment.getAssignedMembers().isEmpty()) {
            dto.setAssignedMemberIds(
                taskAssignment.getAssignedMembers().stream()
                    .map(member -> member.getTeamMember().getUserID())
                    .toList()
            );
            dto.setAssignedMemberNames(
                taskAssignment.getAssignedMembers().stream()
                    .map(member -> member.getTeamMember().getName())
                    .toList()
            );
        }
        
        dto.setScheduledStartDate(taskAssignment.getScheduledStartDate());
        dto.setScheduledEndDate(taskAssignment.getScheduledEndDate());
        dto.setProjectID(taskAssignment.getProject().getProjectID());
        
        return dto;
    }
}
