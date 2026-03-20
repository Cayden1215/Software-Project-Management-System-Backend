package com.softwareprojectmanagement.backend.services;

import org.springframework.stereotype.Service;

import com.softwareprojectmanagement.backend.dto.ProjectManagerDto;

@Service
public interface ProjectManagerService {
    ProjectManagerDto createProjectManager(ProjectManagerDto projectManagerDto);
    ProjectManagerDto updateProjectManager(Long id, ProjectManagerDto projectManagerDto);
    void deleteProjectManager(Long userID);
    ProjectManagerDto getProjectManager(Long id);
}
