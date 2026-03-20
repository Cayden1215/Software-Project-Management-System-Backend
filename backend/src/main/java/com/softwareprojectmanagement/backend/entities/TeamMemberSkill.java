package com.softwareprojectmanagement.backend.entities;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TeamMemberSkill")
public class TeamMemberSkill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tmsID;

    @ManyToOne
    @JoinColumn(name = "skillID")
    private Skill skillID;

    @ManyToOne
    @JoinColumn(name = "userID")
    private TeamMember teamMember;




}
