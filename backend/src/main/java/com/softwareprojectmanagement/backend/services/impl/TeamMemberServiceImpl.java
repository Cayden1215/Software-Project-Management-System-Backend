package com.softwareprojectmanagement.backend.services.impl;

import com.softwareprojectmanagement.backend.dto.TeamMemberDto;
import com.softwareprojectmanagement.backend.entities.TeamMember;
import com.softwareprojectmanagement.backend.mappers.TeamMemberMapper;
import com.softwareprojectmanagement.backend.repositories.TeamMemberRepository;
import com.softwareprojectmanagement.backend.services.TeamMemberService;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TeamMemberServiceImpl implements TeamMemberService {

    private TeamMemberRepository teamMemberRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public TeamMemberDto createTeamMember(TeamMemberDto teamMemberDto) {
        TeamMember teamMember = TeamMemberMapper.mapToTeamMember(teamMemberDto);
        // Encode password before saving
        teamMember.setPassword(passwordEncoder.encode(teamMember.getPassword()));
        teamMember = teamMemberRepository.save(teamMember);

        return TeamMemberMapper.mapToTeamMemberDto(teamMember);
    }

    @Override
    public TeamMemberDto updateTeamMember(Long id,TeamMemberDto teamMemberDto) {
        TeamMember teamMember = teamMemberRepository.findById(id).orElseThrow(() -> new RuntimeException("Team Member not found"));
        teamMember.setUsername(teamMemberDto.getUsername());
        teamMember.setEmail(teamMemberDto.getEmail());
        // Only encode password if it's being updated
        if (teamMemberDto.getPassword() != null && !teamMemberDto.getPassword().isEmpty()) {
            teamMember.setPassword(passwordEncoder.encode(teamMemberDto.getPassword()));
        }
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
