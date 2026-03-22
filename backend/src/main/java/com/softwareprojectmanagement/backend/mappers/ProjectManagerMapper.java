package com.softwareprojectmanagement.backend.mappers;

import com.softwareprojectmanagement.backend.dto.ProjectManagerDto;
import com.softwareprojectmanagement.backend.entities.ProjectManager;

public class ProjectManagerMapper {
    public static ProjectManagerDto mapToProjectManagerDto(ProjectManager projectmanager){
        return new ProjectManagerDto(
            projectmanager.getUserID(),
            projectmanager.getUsername(),
            projectmanager.getPassword(),
            projectmanager.getEmail()
        );
    }

    public static ProjectManager mapToProjectManager(ProjectManagerDto projectmanagerdto){
        ProjectManager projectmanager = new ProjectManager();
        projectmanager.setUserID(projectmanagerdto.getUserID());
        projectmanager.setUsername(projectmanagerdto.getUsername());
        projectmanager.setPassword(projectmanagerdto.getPassword());
        projectmanager.setEmail(projectmanagerdto.getEmail());
        
        return projectmanager;
    }
}
