package com.softwareprojectmanagement.backend.services;

import org.springframework.stereotype.Service;

import com.softwareprojectmanagement.backend.dto.TeamMemberDto;

@Service
public interface TeamMemberService {
    TeamMemberDto createTeamMember(TeamMemberDto teamMemberDto);
    TeamMemberDto updateTeamMember(Long id, TeamMemberDto teamMemberDto);
    void deleteTeamMember(Long id);
    TeamMemberDto getTeamMember(Long id);
}
