package com.softwareprojectmanagement.backend.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TaskAssignment")
public class TaskAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignmentID;

    @Column(nullable = false)
    private LocalDate scheduledStartDate;

    @Column
    private LocalDate scheduledEndDate;

    @OneToOne
    @JoinColumn(name = "taskID", nullable = false)
    private Task task;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "task_assignment_members",
        joinColumns = @JoinColumn(name = "assignment_id"),
        inverseJoinColumns = @JoinColumn(name = "project_member_id")
    )
    private Set<ProjectMember> assignedMembers = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "projectID", nullable = false)
    private Project project;

    /**
     * Validates that the number of assigned members matches the required member count in the task.
     * 
     * @return true if the assignment is valid, false otherwise
     */
    public boolean isValidMemberCount() {
        if (task == null || task.getRequiredMemberNum() == null) {
            return true; // No validation if task or requiredMemberNum is not set
        }
        return this.assignedMembers.size() == task.getRequiredMemberNum();
    }

    /**
     * Gets the validation error message if the member count is invalid.
     * 
     * @return error message or null if valid
     */
    public String getValidationError() {
        if (task == null || task.getRequiredMemberNum() == null) {
            return null;
        }
        int required = task.getRequiredMemberNum();
        int assigned = this.assignedMembers.size();
        if (assigned != required) {
            return String.format("Task '%s' requires %d member(s), but %d member(s) were assigned.",
                task.getTaskName(), required, assigned);
        }
        return null;
    }
}
