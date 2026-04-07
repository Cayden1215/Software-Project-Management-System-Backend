package com.softwareprojectmanagement.backend.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softwareprojectmanagement.backend.dto.ProjectMemberSkillDto;
import com.softwareprojectmanagement.backend.services.ProjectMemberSkillService;

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
@RequestMapping("api/v1/project/{projectId}/project-member-skills")
public class ProjectMemberSkillController {

    private ProjectMemberSkillService projectMemberSkillService;

    @PostMapping
    public ResponseEntity<ProjectMemberSkillDto> addSkillsToProjectMember(
            @PathVariable("projectId") Long projectId,
            @RequestBody ProjectMemberSkillDto projectMemberSkillDto) {
        ProjectMemberSkillDto result = projectMemberSkillService
                .addSkillsToProjectMember(projectId, projectMemberSkillDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{projectMemberID}")
    public ResponseEntity<ProjectMemberSkillDto> updateProjectMemberSkills(
            @PathVariable("projectId") Long projectId,
            @PathVariable("projectMemberID") Long projectMemberID,
            @RequestBody ProjectMemberSkillDto projectMemberSkillDto) {
        ProjectMemberSkillDto result = projectMemberSkillService
                .updateProjectMemberSkills(projectId, projectMemberID, projectMemberSkillDto);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/{projectMemberID}")
    public ResponseEntity<ProjectMemberSkillDto> getProjectMemberSkills(@PathVariable Long projectMemberID) {
        ProjectMemberSkillDto result = projectMemberSkillService.getProjectMemberSkills(projectMemberID);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @DeleteMapping("/{projectMemberID}/skill/{skillID}")
    public ResponseEntity<Void> removeSkillFromProjectMember(@PathVariable Long projectMemberID,
            @PathVariable Long skillID) {
        projectMemberSkillService.removeSkillFromProjectMember(projectMemberID, skillID);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{projectMemberID}/all-skills")
    public ResponseEntity<Void> removeAllSkillsFromProjectMember(@PathVariable Long projectMemberID) {
        projectMemberSkillService.removeAllSkillsFromProjectMember(projectMemberID);
        return ResponseEntity.noContent().build();
    }
}
