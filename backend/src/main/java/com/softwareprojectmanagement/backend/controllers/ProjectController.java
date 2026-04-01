package com.softwareprojectmanagement.backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softwareprojectmanagement.backend.dto.ProjectDto;
import com.softwareprojectmanagement.backend.dto.ProjectMemberDto;
import com.softwareprojectmanagement.backend.services.ProjectService;

import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@AllArgsConstructor
@RestController
@RequestMapping("api/v1/projects")
public class ProjectController {

    private ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectDto projectDto) {

        ProjectDto savedProjectDto = projectService.createProject(projectDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedProjectDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable Long id) {
        ProjectDto foundProjectDto = projectService.getProjectById(id);
        return ResponseEntity.status(HttpStatus.OK).body(foundProjectDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable Long id, @RequestBody ProjectDto projectDto) {
        projectDto.setProjectID(id);
        ProjectDto updatedProjectDto = projectService.updateProject(projectDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedProjectDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProjectDto>> getAllProjects(@AuthenticationPrincipal UserDetails userDetails) {
        List<ProjectDto> projectDtos = projectService.getAllProjects(userDetails.getUsername());
        return ResponseEntity.ok(projectDtos);
    }

    @PostMapping("/{id}/enroll") //havent test
    public ResponseEntity<Void> enrollTeamMemberToProject(@PathVariable Long id, @RequestBody ProjectMemberDto projectMemberDto) {
        projectService.enrollTeamMemberToProject(id, projectMemberDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
