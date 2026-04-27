package com.softwareprojectmanagement.backend.mappers;

import com.softwareprojectmanagement.backend.dto.ProjectDto;
import com.softwareprojectmanagement.backend.entities.ProjectManager;
import com.softwareprojectmanagement.backend.entities.Project;

public class ProjectMapper {

    public static ProjectDto mapToProjectDto(Project project){
        return new ProjectDto(
            project.getProjectID(),
            project.getProjectName(),
            project.getProjectDescription(),
            project.getStartDate(),
            project.getDeadline(),
            project.getProjectStatus(),
            project.getProjectManager().getUserID(),
            project.getTasks().stream().map(TaskMapper::mapToTaskDto).toList()
        );
    }

    public static Project mapToProject(ProjectDto projectdto, ProjectManager projectManager){

        Project project = new Project();

        project.setProjectName(projectdto.getProjectName());
        project.setProjectDescription(projectdto.getProjectDescription());
        project.setStartDate(projectdto.getStartDate());
        project.setDeadline(projectdto.getDeadline());
        project.setProjectStatus(projectdto.getProjectStatus());
        project.setProjectManager(projectManager);

        return project;
    }
}
