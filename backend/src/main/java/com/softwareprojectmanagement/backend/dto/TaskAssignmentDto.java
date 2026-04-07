package com.softwareprojectmanagement.backend.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for TaskAssignment entity.
 * Contains scheduling information and multiple member assignments for a task.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskAssignmentDto {
    
    private Long assignmentID;
    
    private Long taskID;
    
    private String taskName;
    
    private Integer requiredMemberNum;
    
    private List<Long> assignedMemberIds = new ArrayList<>();
    
    private List<String> assignedMemberNames = new ArrayList<>();
    
    private LocalDate scheduledStartDate;
    
    private LocalDate scheduledEndDate;
    
    private Long projectID;

    /**
     * Validates that the number of assigned members matches the required member count.
     * 
     * @return true if valid, false if the count doesn't match
     */
    public boolean isValidMemberCount() {
        if (requiredMemberNum == null) {
            return true; // No validation if not set
        }
        return this.assignedMemberIds.size() == requiredMemberNum;
    }

    /**
     * Gets validation error message if invalid.
     * 
     * @return error message or null if valid
     */
    public String getValidationError() {
        if (requiredMemberNum == null) {
            return null;
        }
        int assigned = this.assignedMemberIds.size();
        if (assigned != requiredMemberNum) {
            return String.format("Task '%s' requires %d member(s), but %d member(s) were assigned.",
                taskName, requiredMemberNum, assigned);
        }
        return null;
    }
}
