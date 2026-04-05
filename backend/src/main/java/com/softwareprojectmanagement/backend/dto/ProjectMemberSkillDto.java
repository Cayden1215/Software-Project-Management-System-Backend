package com.softwareprojectmanagement.backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMemberSkillDto {
    private Long projectMemberID;
    private List<Long> skillIDs;
    private List<SkillDto> skills;
}
