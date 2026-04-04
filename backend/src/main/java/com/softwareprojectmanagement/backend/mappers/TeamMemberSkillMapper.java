package com.softwareprojectmanagement.backend.mappers;

import com.softwareprojectmanagement.backend.dto.TeamMemberSkillDto;
import com.softwareprojectmanagement.backend.entities.Skill;
import com.softwareprojectmanagement.backend.entities.TeamMember;
import com.softwareprojectmanagement.backend.entities.TeamMemberSkill;

public class TeamMemberSkillMapper {

    public static TeamMemberSkillDto mapToTeamMemberSkillDto(TeamMemberSkill teamMemberSkill) {
        return new TeamMemberSkillDto(
            teamMemberSkill.getTmsID(),
            teamMemberSkill.getSkillID().getSkillID(),
            teamMemberSkill.getTeamMember().getUserID()
        );
    }

    public static TeamMemberSkill mapToTeamMemberSkill(TeamMemberSkillDto teamMemberSkillDto, Skill skill, TeamMember teamMember) {
        TeamMemberSkill teamMemberSkill = new TeamMemberSkill();
        teamMemberSkill.setSkillID(skill);
        teamMemberSkill.setTeamMember(teamMember);
        return teamMemberSkill;
    }
}
