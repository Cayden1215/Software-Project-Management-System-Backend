package com.softwareprojectmanagement.backend.entities;

import java.util.HashSet;
import java.util.List;
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

    @ManyToOne
    @JoinColumn(name = "projectID")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "sprintID")
    private Sprint sprint;

    @OneToOne(mappedBy = "task")
    private TaskAssignment taskAssignment;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "task_skills",
        joinColumns = @JoinColumn(name = "task_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills = new HashSet<>();

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "task_dependencies",
        joinColumns = @JoinColumn(name = "task_id"),
        inverseJoinColumns = @JoinColumn(name = "dependency_id")
    )
    private Set<Task> dependencies = new HashSet<>();

    @ManyToMany(mappedBy = "dependencies")
    private Set<Task> dependentTasks = new HashSet<>();


}
