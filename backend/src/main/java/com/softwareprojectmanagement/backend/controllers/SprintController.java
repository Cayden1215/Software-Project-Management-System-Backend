package com.softwareprojectmanagement.backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softwareprojectmanagement.backend.dto.SprintDto;
import com.softwareprojectmanagement.backend.services.SprintService;

import lombok.AllArgsConstructor;

import java.util.List;

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
@RequestMapping("api/v1/sprints")
public class SprintController {

    private SprintService sprintService;

    @PostMapping("/{projectId}")
    public ResponseEntity<SprintDto> createSprint(
            @PathVariable Long projectId,
            @RequestBody SprintDto sprintDto) {
        SprintDto savedSprintDto = sprintService.createSprint(sprintDto, projectId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSprintDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SprintDto> getSprintById(@PathVariable Long id) {
        SprintDto foundSprintDto = sprintService.getSprintById(id);
        return ResponseEntity.status(HttpStatus.OK).body(foundSprintDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SprintDto> updateSprint(
            @PathVariable Long id,
            @RequestBody SprintDto sprintDto) {
        SprintDto updatedSprintDto = sprintService.updateSprint(id, sprintDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedSprintDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSprint(@PathVariable Long id) {
        sprintService.deleteSprint(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<SprintDto>> getSprintsByProjectId(@PathVariable Long projectId) {
        List<SprintDto> sprintDtos = sprintService.getSprintsByProjectId(projectId);
        return ResponseEntity.status(HttpStatus.OK).body(sprintDtos);
    }

    @GetMapping
    public ResponseEntity<List<SprintDto>> getAllSprints() {
        List<SprintDto> sprintDtos = sprintService.getAllSprints();
        return ResponseEntity.status(HttpStatus.OK).body(sprintDtos);
    }
}
