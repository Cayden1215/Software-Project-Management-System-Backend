/*package com.softwareprojectmanagement.backend.services.impl;

import com.softwareprojectmanagement.backend.dto.TeamMemberDto;
import com.softwareprojectmanagement.backend.entities.TeamMember;
import com.softwareprojectmanagement.backend.mappers.TeamMemberMapper;
import com.softwareprojectmanagement.backend.repositories.TeamMemberRepository;
import com.softwareprojectmanagement.backend.services.TeamMemberService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TeamMemberServiceImpl implements TeamMemberService {

    private TeamMemberRepository teamMemberRepository;

    @Override
    public TeamMemberDto createTeamMember(TeamMemberDto teamMemberDto) {
        TeamMember teamMember = TeamMemberMapper.mapToTeamMember(teamMemberDto);
        teamMember = teamMemberRepository.save(teamMember);

        return TeamMemberMapper.mapToTeamMemberDto(teamMember);
    }

    @Override
    public TeamMemberDto updateTeamMember(Long id,TeamMemberDto teamMemberDto) {
        TeamMember teamMember = teamMemberRepository.findById(id).orElseThrow(() -> new RuntimeException("Team Member not found"));
        teamMember = TeamMemberMapper.mapToTeamMember(teamMemberDto);
        teamMember = teamMemberRepository.save(teamMember);

        return TeamMemberMapper.mapToTeamMemberDto(teamMember);
    }

    @Override
    public void deleteTeamMember(Long id) {
        TeamMember teamMember = teamMemberRepository.findById(id).orElseThrow(() -> new RuntimeException("Team Member not found"));
        teamMemberRepository.delete(teamMember);
    }

    @Override
    public TeamMemberDto getTeamMember(Long id) {
        TeamMember teamMember = teamMemberRepository.findById(id).orElseThrow(() -> new RuntimeException("Team Member not found"));
        return TeamMemberMapper.mapToTeamMemberDto(teamMember);
    }
        

}

*/