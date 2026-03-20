package com.softwareprojectmanagement.backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softwareprojectmanagement.backend.dto.ProjectDto;
import com.softwareprojectmanagement.backend.services.ProjectService;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@AllArgsConstructor
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private ProjectService projectService;

    @PostMapping("/create")
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectDto projectDto) {

        ProjectDto savedProjectDto = projectService.createProject(projectDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedProjectDto);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable Long id) {
        ProjectDto foundProjectDto = projectService.getProjectById(id);
        return ResponseEntity.status(HttpStatus.OK).body(foundProjectDto);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable Long id, @RequestBody ProjectDto projectDto) {
        projectDto.setProjectID(id);
        ProjectDto updatedProjectDto = projectService.updateProject(projectDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedProjectDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/")
    public ResponseEntity<java.util.List<ProjectDto>> getAllProjects(@RequestParam Long pmID) {
        java.util.List<ProjectDto> projectDtos = projectService.getAllProjects(pmID);
        return ResponseEntity.ok(projectDtos);
    }
}
