package com.softwareprojectmanagement.backend.entities;

import java.time.LocalDate;
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
@Table(name = "ProjectMember")
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @ManyToOne
    @JoinColumn(name = "projectID", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private TeamMember teamMember;

    @Column(name = "enrollmentDate" , nullable = false)
    private LocalDate enrollmentDate;

    @Column(name = "projectRole" , nullable = false)
    private String projectRole;
    
}
