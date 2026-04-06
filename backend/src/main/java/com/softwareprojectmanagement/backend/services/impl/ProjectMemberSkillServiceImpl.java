package com.softwareprojectmanagement.backend.services.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softwareprojectmanagement.backend.dto.ProjectMemberSkillDto;
import com.softwareprojectmanagement.backend.dto.SkillDto;
import com.softwareprojectmanagement.backend.entities.ProjectMember;
import com.softwareprojectmanagement.backend.entities.Skill;
import com.softwareprojectmanagement.backend.mappers.SkillMapper;
import com.softwareprojectmanagement.backend.repositories.ProjectMemberRepository;
import com.softwareprojectmanagement.backend.repositories.SkillRepository;
import com.softwareprojectmanagement.backend.services.ProjectMemberSkillService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ProjectMemberSkillServiceImpl implements ProjectMemberSkillService {

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Override
    public ProjectMemberSkillDto addSkillsToProjectMember(Long projectId, ProjectMemberSkillDto projectMemberSkillDto) {
        if (projectMemberSkillDto.getProjectMemberID() == null || projectMemberSkillDto.getSkillIDs() == null) {
            throw new RuntimeException("ProjectMember ID and Skill IDs cannot be null");
        }

        if (projectMemberSkillDto.getSkillIDs().isEmpty()) {
            throw new RuntimeException("Skill IDs list cannot be empty");
        }

        ProjectMember projectMember = projectMemberRepository.findById(projectMemberSkillDto.getProjectMemberID())
                .orElseThrow(() -> new RuntimeException("Project member not found"));


        List<Skill> skillsToAdd = new ArrayList<>();
        for (Long skillID : projectMemberSkillDto.getSkillIDs()) {
            Skill skill = skillRepository.findById(skillID)
                    .orElseThrow(() -> new RuntimeException("Skill with ID " + skillID + " not found"));
            if(!skill.getProject().getProjectID().equals(projectId)) {
                throw new RuntimeException("Skill with ID " + skillID + " does not belong to the project");
            }
            skillsToAdd.add(skill);
        }

        // Add new skills to existing skills
        if (projectMember.getSkills() == null) {
            projectMember.setSkills(new HashSet<>());
        }

        for (Skill skill : skillsToAdd) {
            if (!projectMember.getSkills().contains(skill)) {
                projectMember.getSkills().add(skill);
            }
        }

        ProjectMember updatedProjectMember = projectMemberRepository.save(projectMember);
        return mapToProjectMemberSkillDto(updatedProjectMember);
    }

    @Override
    public ProjectMemberSkillDto updateProjectMemberSkills(Long projectId, ProjectMemberSkillDto projectMemberSkillDto) {
        if (projectMemberSkillDto.getProjectMemberID() == null || projectMemberSkillDto.getSkillIDs() == null) {
            throw new RuntimeException("ProjectMember ID and Skill IDs cannot be null");
        }

        if (projectMemberSkillDto.getSkillIDs().isEmpty()) {
            throw new RuntimeException("Skill IDs list cannot be empty");
        }

        ProjectMember projectMember = projectMemberRepository.findById(projectMemberSkillDto.getProjectMemberID())
                .orElseThrow(() -> new RuntimeException("Project member not found"));

        List<Skill> newSkills = new ArrayList<>();
        for (Long skillID : projectMemberSkillDto.getSkillIDs()) {
            Skill skill = skillRepository.findById(skillID)
                    .orElseThrow(() -> new RuntimeException("Skill with ID " + skillID + " not found"));
            if(!skill.getProject().getProjectID().equals(projectId)) {
                throw new RuntimeException("Skill with ID " + skillID + " does not belong to the project");
            }
            newSkills.add(skill);
        }

        // Replace existing skills with new skills
        projectMember.setSkills(new HashSet<>(newSkills));

        ProjectMember updatedProjectMember = projectMemberRepository.save(projectMember);
        return mapToProjectMemberSkillDto(updatedProjectMember);
    }

    @Override
    public ProjectMemberSkillDto getProjectMemberSkills(Long projectMemberID) {
        ProjectMember projectMember = projectMemberRepository.findById(projectMemberID)
                .orElseThrow(() -> new RuntimeException("Project member not found"));

        return mapToProjectMemberSkillDto(projectMember);
    }

    @Override
    public void removeSkillFromProjectMember(Long projectMemberID, Long skillID) {
        ProjectMember projectMember = projectMemberRepository.findById(projectMemberID)
                .orElseThrow(() -> new RuntimeException("Project member not found"));

        Skill skill = skillRepository.findById(skillID)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        if (projectMember.getSkills() != null && projectMember.getSkills().contains(skill)) {
            projectMember.getSkills().remove(skill);
            projectMemberRepository.save(projectMember);
        }
    }

    @Override
    public void removeAllSkillsFromProjectMember(Long projectMemberID) {
        ProjectMember projectMember = projectMemberRepository.findById(projectMemberID)
                .orElseThrow(() -> new RuntimeException("Project member not found"));

        if (projectMember.getSkills() != null) {
            projectMember.setSkills(new HashSet<>());
            projectMemberRepository.save(projectMember);
        }
    }

    private ProjectMemberSkillDto mapToProjectMemberSkillDto(ProjectMember projectMember) {
        ProjectMemberSkillDto dto = new ProjectMemberSkillDto();
        dto.setProjectMemberID(projectMember.getID());

        if (projectMember.getSkills() != null && !projectMember.getSkills().isEmpty()) {
            dto.setSkillIDs(projectMember.getSkills().stream()
                    .map(Skill::getSkillID)
                    .collect(Collectors.toList()));

            dto.setSkills(projectMember.getSkills().stream()
                    .map(SkillMapper::mapToSkillDto)
                    .collect(Collectors.toList()));
        } else {
            dto.setSkillIDs(new ArrayList<>());
            dto.setSkills(new ArrayList<>());
        }

        return dto;
    }
}
