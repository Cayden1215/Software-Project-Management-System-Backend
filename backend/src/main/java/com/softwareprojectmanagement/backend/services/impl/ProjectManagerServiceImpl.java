package com.softwareprojectmanagement.backend.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softwareprojectmanagement.backend.dto.ProjectManagerDto;
import com.softwareprojectmanagement.backend.entities.ProjectManager;
import com.softwareprojectmanagement.backend.mappers.ProjectManagerMapper;
import com.softwareprojectmanagement.backend.repositories.ProjectManagerRepository;
import com.softwareprojectmanagement.backend.services.ProjectManagerService;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class ProjectManagerServiceImpl implements ProjectManagerService {

    @Autowired
    private ProjectManagerRepository projectManagerRepository;

    @Override
    public ProjectManagerDto createProjectManager(ProjectManagerDto projectManagerDto) {

        ProjectManager projectManager = ProjectManagerMapper.mapToProjectManager(projectManagerDto);

        ProjectManager savedProjectManager = projectManagerRepository.save(projectManager);

        return ProjectManagerMapper.mapToProjectManagerDto(savedProjectManager);
    }

    @Override
    public ProjectManagerDto updateProjectManager(Long id, ProjectManagerDto projectManagerDto) {
        ProjectManager projectManager = projectManagerRepository.findById(id).orElseThrow(() -> new RuntimeException("Project Manager not found"));

        projectManager.setName(projectManagerDto.getName());
        projectManager.setEmail(projectManagerDto.getEmail());
        projectManager.setPassword(projectManagerDto.getPassword());
        
        ProjectManager updatedProjectManager = projectManagerRepository.save(projectManager);

        return ProjectManagerMapper.mapToProjectManagerDto(updatedProjectManager);
    }

    @Override
    public void deleteProjectManager(Long userID) {
        projectManagerRepository.deleteById(userID);
    }

    @Override
    public ProjectManagerDto getProjectManager(Long id) {
        ProjectManager projectManager = projectManagerRepository.findById(id).orElseThrow(() -> new RuntimeException("Project Manager not found"));

        return ProjectManagerMapper.mapToProjectManagerDto(projectManager);
    }
}
