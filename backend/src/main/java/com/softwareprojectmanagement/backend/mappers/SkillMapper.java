package com.softwareprojectmanagement.backend.mappers;

import com.softwareprojectmanagement.backend.dto.SkillDto;
import com.softwareprojectmanagement.backend.entities.Project;
import com.softwareprojectmanagement.backend.entities.Skill;

public class SkillMapper {

    public static SkillDto mapToSkillDto(Skill skill) {
        return new SkillDto(
            skill.getSkillID(),
            skill.getSkillName(),
            skill.getProject().getProjectID()
        );
    }

    public static Skill mapToSkill(SkillDto skillDto, Project project) {
        Skill skill = new Skill();
        skill.setSkillName(skillDto.getSkillName());
        skill.setProject(project);
        return skill;
    }
}
