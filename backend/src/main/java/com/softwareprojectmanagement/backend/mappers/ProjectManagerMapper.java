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
        return new ProjectManager(
            projectmanagerdto.getUserID(),
            projectmanagerdto.getUsername(),
            projectmanagerdto.getPassword(),
            projectmanagerdto.getEmail(),
            null);
    }
}
