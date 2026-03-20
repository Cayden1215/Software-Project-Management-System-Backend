package com.softwareprojectmanagement.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softwareprojectmanagement.backend.dto.ProjectManagerDto;
import com.softwareprojectmanagement.backend.entities.ProjectManager;
import com.softwareprojectmanagement.backend.services.ProjectManagerService;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;


@AllArgsConstructor
@RestController
@RequestMapping("/api/projectmanagers")
public class ProjectManagerController {
    private ProjectManagerService projectManagerService;

    @PostMapping("/create")
    public ResponseEntity<ProjectManagerDto> createProjectManager(@RequestBody ProjectManagerDto projectManagerDto){
        ProjectManagerDto savedProjectManager = projectManagerService.createProjectManager(projectManagerDto);
        return new ResponseEntity<>(savedProjectManager,HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/get/{id}")
    public ResponseEntity<ProjectManagerDto> getProjectManager(@PathVariable Long id) {
        ProjectManagerDto retrievedProjectManager = projectManagerService.getProjectManager(id);
        return new ResponseEntity<>(retrievedProjectManager, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ProjectManagerDto> updateProjectManager(@PathVariable Long id, @RequestBody ProjectManagerDto projectManagerDto) {
        ProjectManagerDto updatedProjectManager = projectManagerService.updateProjectManager(id, projectManagerDto);
        return new ResponseEntity<>(updatedProjectManager, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProjectManager(@PathVariable Long id) {
        projectManagerService.deleteProjectManager(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

    
}
