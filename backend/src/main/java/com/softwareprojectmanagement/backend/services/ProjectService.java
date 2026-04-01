package com.softwareprojectmanagement.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.softwareprojectmanagement.backend.dto.ProjectDto;
import com.softwareprojectmanagement.backend.dto.ProjectMemberDto;
import com.softwareprojectmanagement.backend.entities.Project;

@Service
public interface ProjectService {

    public ProjectDto createProject(ProjectDto projectDto);

    public ProjectDto getProjectById(Long id);

    public Project getProjectEntityById(Long id);

    public ProjectDto updateProject(ProjectDto projectDto);

    public void deleteProject(Long id);

    public List<ProjectDto> getAllProjects(String pmEmail);

    public List<ProjectDto> getAllEnrolledProjects(Long tmID);

    public void enrollTeamMemberToProject(Long projectId,ProjectMemberDto projectMemberDto);

}
