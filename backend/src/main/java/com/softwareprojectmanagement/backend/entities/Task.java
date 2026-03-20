package com.softwareprojectmanagement.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskID;

    @Column(length = 255, nullable = false)
    private String taskName;

    private int estimatedDuration;

    @Column(length = 255)
    private String description;

    @Column(length = 10)
    private String taskStatus;

    @Column(length = 30)
    private Integer requiredMemberNum;

    @Column(length = 30)
    private Integer storyPoint;

    @OneToOne
    @JoinColumn(name = "projectID")
    private Project project;

    @OneToOne
    @JoinColumn(name = "sprintID")
    private Sprint sprint;

    @OneToOne(mappedBy = "task")
    private TaskAssignment taskAssignment;
}
