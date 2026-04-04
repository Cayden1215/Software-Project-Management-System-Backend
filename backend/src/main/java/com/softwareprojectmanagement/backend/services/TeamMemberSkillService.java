package com.softwareprojectmanagement.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.softwareprojectmanagement.backend.dto.TeamMemberSkillDto;

@Service
public interface TeamMemberSkillService {

    public TeamMemberSkillDto createTeamMemberSkill(TeamMemberSkillDto teamMemberSkillDto);

    public TeamMemberSkillDto getTeamMemberSkillById(Long id);

    public void deleteTeamMemberSkill(Long id);

    public List<TeamMemberSkillDto> getSkillsByTeamMember(Long userID);

    public List<TeamMemberSkillDto> getTeamMembersBySkill(Long skillID);

    public List<TeamMemberSkillDto> getAllTeamMemberSkills();
}
