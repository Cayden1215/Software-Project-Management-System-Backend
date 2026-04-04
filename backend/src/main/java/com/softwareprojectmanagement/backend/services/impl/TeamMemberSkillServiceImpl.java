package com.softwareprojectmanagement.backend.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softwareprojectmanagement.backend.dto.TeamMemberSkillDto;
import com.softwareprojectmanagement.backend.entities.Skill;
import com.softwareprojectmanagement.backend.entities.TeamMember;
import com.softwareprojectmanagement.backend.entities.TeamMemberSkill;
import com.softwareprojectmanagement.backend.mappers.TeamMemberSkillMapper;
import com.softwareprojectmanagement.backend.repositories.SkillRepository;
import com.softwareprojectmanagement.backend.repositories.TeamMemberRepository;
import com.softwareprojectmanagement.backend.repositories.TeamMemberSkillRepository;
import com.softwareprojectmanagement.backend.services.TeamMemberSkillService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class TeamMemberSkillServiceImpl implements TeamMemberSkillService {

    @Autowired
    private TeamMemberSkillRepository teamMemberSkillRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Override
    public TeamMemberSkillDto createTeamMemberSkill(TeamMemberSkillDto teamMemberSkillDto) {
        if (teamMemberSkillDto.getSkillID() == null || teamMemberSkillDto.getUserID() == null) {
            throw new RuntimeException("Skill ID and User ID cannot be null");
        }

        if (teamMemberSkillRepository.existsByTeamMemberUserIDAndSkillID(
                teamMemberSkillDto.getUserID(), teamMemberSkillDto.getSkillID())) {
            throw new RuntimeException("Team member already has this skill assigned");
        }

        Skill skill = skillRepository.findById(teamMemberSkillDto.getSkillID())
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        TeamMember teamMember = teamMemberRepository.findById(teamMemberSkillDto.getUserID())
                .orElseThrow(() -> new RuntimeException("Team member not found"));

        TeamMemberSkill teamMemberSkill = TeamMemberSkillMapper.mapToTeamMemberSkill(
                teamMemberSkillDto, skill, teamMember);
        TeamMemberSkill savedTeamMemberSkill = teamMemberSkillRepository.save(teamMemberSkill);

        return TeamMemberSkillMapper.mapToTeamMemberSkillDto(savedTeamMemberSkill);
    }

    @Override
    public TeamMemberSkillDto getTeamMemberSkillById(Long id) {
        TeamMemberSkill teamMemberSkill = teamMemberSkillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team member skill not found"));
        return TeamMemberSkillMapper.mapToTeamMemberSkillDto(teamMemberSkill);
    }

    @Override
    public void deleteTeamMemberSkill(Long id) {
        TeamMemberSkill teamMemberSkill = teamMemberSkillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team member skill not found"));
        teamMemberSkillRepository.delete(teamMemberSkill);
    }

    @Override
    public List<TeamMemberSkillDto> getSkillsByTeamMember(Long userID) {
        teamMemberRepository.findById(userID)
                .orElseThrow(() -> new RuntimeException("Team member not found"));

        List<TeamMemberSkill> teamMemberSkills = teamMemberSkillRepository.findByTeamMemberUserID(userID);
        return teamMemberSkills.stream().map(TeamMemberSkillMapper::mapToTeamMemberSkillDto).toList();
    }

    @Override
    public List<TeamMemberSkillDto> getTeamMembersBySkill(Long skillID) {
        skillRepository.findById(skillID)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        List<TeamMemberSkill> teamMemberSkills = teamMemberSkillRepository.findBySkillIDSkillID(skillID);
        return teamMemberSkills.stream().map(TeamMemberSkillMapper::mapToTeamMemberSkillDto).toList();
    }

    @Override
    public List<TeamMemberSkillDto> getAllTeamMemberSkills() {
        List<TeamMemberSkill> teamMemberSkills = teamMemberSkillRepository.findAll();
        return teamMemberSkills.stream().map(TeamMemberSkillMapper::mapToTeamMemberSkillDto).toList();
    }
}
