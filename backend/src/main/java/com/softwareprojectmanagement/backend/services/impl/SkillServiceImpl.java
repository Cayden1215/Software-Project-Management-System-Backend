package com.softwareprojectmanagement.backend.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softwareprojectmanagement.backend.dto.SkillDto;
import com.softwareprojectmanagement.backend.entities.Project;
import com.softwareprojectmanagement.backend.entities.Skill;
import com.softwareprojectmanagement.backend.mappers.SkillMapper;
import com.softwareprojectmanagement.backend.repositories.SkillRepository;
import com.softwareprojectmanagement.backend.services.ProjectService;
import com.softwareprojectmanagement.backend.services.SkillService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class SkillServiceImpl implements SkillService {

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private ProjectService projectService;

    @Override
    public SkillDto createSkill(SkillDto skillDto) {
        if (skillDto.getProjectID() == null) {
            throw new RuntimeException("Project ID cannot be null");
        }

        if (skillRepository.existsBySkillNameAndProjectProjectID(skillDto.getSkillName(), skillDto.getProjectID())) {
            throw new RuntimeException("Skill with name '" + skillDto.getSkillName() + "' already exists in this project");
        }

        Project project = projectService.getProjectEntityById(skillDto.getProjectID());

        Skill skill = SkillMapper.mapToSkill(skillDto, project);
        Skill savedSkill = skillRepository.save(skill);

        return SkillMapper.mapToSkillDto(savedSkill);
    }

    @Override
    public SkillDto getSkillById(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));
        return SkillMapper.mapToSkillDto(skill);
    }

    @Override
    public void deleteSkill(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));
        skillRepository.delete(skill);
    }

    @Override
    public List<SkillDto> getAllSkillsByProject(Long projectID) {

        projectService.getProjectEntityById(projectID);

        List<Skill> skills = skillRepository.findByProjectProjectID(projectID);
        return skills.stream().map(SkillMapper::mapToSkillDto).toList();
    }

    @Override
    public List<SkillDto> getAllSkills() {
        List<Skill> skills = skillRepository.findAll();
        return skills.stream().map(SkillMapper::mapToSkillDto).toList();
    }
}
