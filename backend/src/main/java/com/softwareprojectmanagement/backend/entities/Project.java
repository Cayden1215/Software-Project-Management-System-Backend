package com.softwareprojectmanagement.backend.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "Project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectID;

    @Column(name = "projectName", nullable = false)
    private String projectName;

    @Column(name = "projectDescription" , nullable = false)
    private String projectDescription;

    @Column(name = "startDate")
    private LocalDate startDate;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Column(name = "projectStatus", columnDefinition = "VARCHAR(255) DEFAULT 'Not Started'")
    private String projectStatus;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private ProjectManager projectManager;

    @OneToMany(mappedBy = "project")
    private Set<Task> tasks = new HashSet<>();

    @OneToMany(mappedBy = "project")
    private Set<ProjectMember> projectMembers = new HashSet<>();

    @OneToMany(mappedBy = "project")
    private Set<Sprint> sprints = new HashSet<>();

    @OneToMany(mappedBy = "project")
    private Set<Skill> skills = new HashSet<>();

    @OneToMany(mappedBy = "project")
    private Set<TaskAssignment> taskAssignments = new HashSet<>();

}
