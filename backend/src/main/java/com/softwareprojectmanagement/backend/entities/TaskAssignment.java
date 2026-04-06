package com.softwareprojectmanagement.backend.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

    @OneToOne
    @JoinColumn(name = "assignmentID")
    private ProjectMember assignedMember;

    @ManyToOne
    @JoinColumn(name = "projectID", nullable = false)
    private Project project;
    
}
