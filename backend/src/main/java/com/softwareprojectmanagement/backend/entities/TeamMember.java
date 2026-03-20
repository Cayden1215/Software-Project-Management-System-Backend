package com.softwareprojectmanagement.backend.entities;

import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TeamMember")
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userID;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password" , nullable = false)
    private String password;

    @Column(name = "email" , unique = true)
    private String email;

    @Column(columnDefinition = "boolean default true")
    private Boolean availability; 

    @OneToMany(mappedBy = "teamMember")
    private List<ProjectMember> projectMembers;

    @OneToMany(mappedBy = "teamMember")
    private List<TeamMemberSkill> teamMemberSkills;



    
}
