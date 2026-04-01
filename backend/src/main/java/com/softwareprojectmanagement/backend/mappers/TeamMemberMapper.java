package com.softwareprojectmanagement.backend.mappers;

import com.softwareprojectmanagement.backend.dto.TeamMemberDto;
import com.softwareprojectmanagement.backend.dto.UserDto;
import com.softwareprojectmanagement.backend.entities.TeamMember;
import com.softwareprojectmanagement.backend.entities.User;

public class TeamMemberMapper {
    public static TeamMemberDto mapToTeamMemberDto(TeamMember teamMember){
        return new TeamMemberDto(
            teamMember.getUserID(),
            teamMember.getUsername(),
            teamMember.getPassword(),
            teamMember.getEmail(),
            teamMember.getAvailability()
        );
    }

    public static TeamMember mapToTeamMember(TeamMemberDto teamMemberDto){
        TeamMember teamMember = new TeamMember();
        teamMember.setUsername(teamMemberDto.getUsername());
        teamMember.setPassword(teamMemberDto.getPassword());
        teamMember.setEmail(teamMemberDto.getEmail());
        teamMember.setAvailability(teamMemberDto.getAvailability());
        return teamMember;
    }

    public static TeamMember mapToTeamMember(UserDto teamMemberDto){
        TeamMember teamMember = new TeamMember();
        teamMember.setUsername(teamMemberDto.getUsername());
        teamMember.setPassword(teamMemberDto.getPassword());
        teamMember.setEmail(teamMemberDto.getEmail());
        teamMember.setAvailability(true);

        return teamMember;
    }
}
