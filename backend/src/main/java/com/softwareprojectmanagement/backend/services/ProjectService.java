package com.softwareprojectmanagement.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.softwareprojectmanagement.backend.dto.ProjectDto;

@Service
public interface ProjectService {

    public ProjectDto createProject(ProjectDto projectDto);

    public ProjectDto getProjectById(Long id);

    public ProjectDto updateProject(ProjectDto projectDto);

    public void deleteProject(Long id);

    public List<ProjectDto> getAllProjects(Long pmID);

    public List<ProjectDto> getAllEnrolledProjects(Long tmID);

    public void enrollTeamMemberToProject(Long projectId, Long teamMemberId, String projectRole);

}
