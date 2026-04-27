package com.softwareprojectmanagement.backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softwareprojectmanagement.backend.dto.ProjectDto;
import com.softwareprojectmanagement.backend.dto.ProjectMemberDto;
import com.softwareprojectmanagement.backend.dto.TeamMemberDto;
import com.softwareprojectmanagement.backend.entities.Project;
import com.softwareprojectmanagement.backend.entities.TeamMember;
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
    public ResponseEntity<ProjectDto> createProject(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ProjectDto projectDto) {

        ProjectDto savedProjectDto = projectService.createProject(userDetails.getUsername(), projectDto);

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

    @PostMapping("/{id}/enroll")
    public ResponseEntity<Void> enrollTeamMemberToProject(@PathVariable Long id, @RequestBody ProjectMemberDto projectMemberDto) {
        projectService.enrollTeamMemberToProject(id, projectMemberDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{projectId}/enrolled")
    public ResponseEntity<List<ProjectMemberDto>> getProjectTeamMembers(@PathVariable Long projectId) {
        Project project = projectService.getProjectEntityById(projectId);
        List<ProjectMemberDto> teamMemberDtos = projectService.getProjectTeamMembersDto(project);
        return ResponseEntity.ok(teamMemberDtos);
    }

    @DeleteMapping("/{projectId}/enrolled/{teamMemberId}")
    public ResponseEntity<Void> removeTeamMemberFromProject(@PathVariable Long projectId, @PathVariable Long teamMemberId) {
        projectService.removeTeamMemberFromProject(projectId, teamMemberId);
        return ResponseEntity.noContent().build();
    }
}
