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
public class TeamMember extends User {
    private Boolean availability; 

    @OneToMany(mappedBy = "teamMember")
    private List<TeamMemberSkill> teamMemberSkills;
    
}
