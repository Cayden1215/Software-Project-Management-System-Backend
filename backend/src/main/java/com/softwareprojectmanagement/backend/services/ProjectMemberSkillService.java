package com.softwareprojectmanagement.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.softwareprojectmanagement.backend.dto.ProjectMemberSkillDto;

@Service
public interface ProjectMemberSkillService {

    public ProjectMemberSkillDto addSkillsToProjectMember(Long projectId, ProjectMemberSkillDto projectMemberSkillDto);

    public ProjectMemberSkillDto updateProjectMemberSkills(Long projectId, ProjectMemberSkillDto projectMemberSkillDto);

    public ProjectMemberSkillDto getProjectMemberSkills(Long projectMemberID);

    public void removeSkillFromProjectMember(Long projectMemberID, Long skillID);

    public void removeAllSkillsFromProjectMember(Long projectMemberID);
}
