package com.softwareprojectmanagement.backend.mappers;

import com.softwareprojectmanagement.backend.dto.TeamMemberDto;
import com.softwareprojectmanagement.backend.dto.UserDto;
import com.softwareprojectmanagement.backend.entities.TeamMember;

public class TeamMemberMapper {
    public static TeamMemberDto mapToTeamMemberDto(TeamMember teamMember){
        return new TeamMemberDto(
            teamMember.getUserID(),
            teamMember.getUsername(),
            teamMember.getEmail(),
            teamMember.getAvailability(),
            teamMember.getRole().name()
        );
    }

    public static TeamMember mapToTeamMember(TeamMemberDto teamMemberDto){
        TeamMember teamMember = new TeamMember();
        teamMember.setName(teamMemberDto.getUsername());
        //teamMember.setPassword(teamMemberDto.getPassword());
        teamMember.setEmail(teamMemberDto.getEmail());
        teamMember.setAvailability(teamMemberDto.getAvailability());
        //teamMember.setRole(TeamMember.Role.valueOf(teamMemberDto.getRole()));
        return teamMember;
    }

    public static TeamMember mapToTeamMember(UserDto teamMemberDto){
        TeamMember teamMember = new TeamMember();
        teamMember.setName(teamMemberDto.getName());
        teamMember.setPassword(teamMemberDto.getPassword());
        teamMember.setEmail(teamMemberDto.getEmail());
        teamMember.setAvailability(true);

        return teamMember;
    }
}
