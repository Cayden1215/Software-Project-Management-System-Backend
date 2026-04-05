package com.softwareprojectmanagement.backend.dto;

import java.time.LocalDate;
import java.util.List;

import com.softwareprojectmanagement.backend.entities.Skill;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMemberDto {

    private Long projectMemberID;

    private Long projectID;
    
    private Long teamMemberID;

    private String teamMemberUsername;

    private String teamMemberEmail;

    private LocalDate enrollmentDate;

    private String projectRole;

    private List<Skill> skills;
}
