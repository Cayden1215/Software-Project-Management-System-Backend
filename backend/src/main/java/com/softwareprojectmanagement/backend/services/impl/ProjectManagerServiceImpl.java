package com.softwareprojectmanagement.backend.services.impl;

import org.springframework.stereotype.Service;

import com.softwareprojectmanagement.backend.dto.ProjectManagerDto;
import com.softwareprojectmanagement.backend.entities.ProjectManager;
import com.softwareprojectmanagement.backend.mappers.ProjectManagerMapper;
import com.softwareprojectmanagement.backend.repositories.ProjectManagerRepository;
import com.softwareprojectmanagement.backend.services.ProjectManagerService;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class ProjectManagerServiceImpl implements ProjectManagerService {

    private ProjectManagerRepository projectManagerRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public ProjectManagerDto createProjectManager(ProjectManagerDto projectManagerDto) {

        ProjectManager projectManager = ProjectManagerMapper.mapToProjectManager(projectManagerDto);
        // Encode password before saving
        projectManager.setPassword(passwordEncoder.encode(projectManager.getPassword()));

        ProjectManager savedProjectManager = projectManagerRepository.save(projectManager);

        return ProjectManagerMapper.mapToProjectManagerDto(savedProjectManager);
    }

    @Override
    public ProjectManagerDto updateProjectManager(Long id, ProjectManagerDto projectManagerDto) {
        ProjectManager projectManager = projectManagerRepository.findById(id).orElseThrow(() -> new RuntimeException("Project Manager not found"));
        projectManager.setUsername(projectManagerDto.getUsername());
        projectManager.setEmail(projectManagerDto.getEmail());
        // Only encode password if it's being updated
        if (projectManagerDto.getPassword() != null && !projectManagerDto.getPassword().isEmpty()) {
            projectManager.setPassword(passwordEncoder.encode(projectManagerDto.getPassword()));
        }
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
