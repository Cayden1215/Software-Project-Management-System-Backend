package com.softwareprojectmanagement.backend.mappers;

import com.softwareprojectmanagement.backend.entities.Role;
import com.softwareprojectmanagement.backend.entities.User;
import com.softwareprojectmanagement.backend.dto.ProjectManagerDto;
import com.softwareprojectmanagement.backend.dto.UserDto;
import com.softwareprojectmanagement.backend.entities.ProjectManager;

public class ProjectManagerMapper {
    public static ProjectManagerDto mapToProjectManagerDto(User projectmanager){
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

    public static ProjectManager mapToProjectManager(UserDto userDto){
        ProjectManager projectmanager = new ProjectManager();
        projectmanager.setUserID(userDto.getUserID());
        projectmanager.setUsername(userDto.getUsername());
        projectmanager.setPassword(userDto.getPassword());
        projectmanager.setEmail(userDto.getEmail());
        projectmanager.setRole(Role.valueOf(userDto.getRole()));
        
        return projectmanager;
    }
}
