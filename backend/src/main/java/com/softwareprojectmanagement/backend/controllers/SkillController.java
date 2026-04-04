package com.softwareprojectmanagement.backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softwareprojectmanagement.backend.dto.SkillDto;
import com.softwareprojectmanagement.backend.services.SkillService;

import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@AllArgsConstructor
@RestController
@RequestMapping("api/v1/project/{projectId}/skills")
public class SkillController {

    private SkillService skillService;

    @PostMapping
    public ResponseEntity<SkillDto> createSkill(@PathVariable Long projectId, @RequestBody SkillDto skillDto) {
        SkillDto savedSkillDto = skillService.createSkill(projectId,skillDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSkillDto);
    }

    @GetMapping
    public ResponseEntity<List<SkillDto>> getProjectSkills(@PathVariable Long projectId) {
        List<SkillDto> skillDtos = skillService.getAllSkillsByProject(projectId);
        return ResponseEntity.status(HttpStatus.OK).body(skillDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillDto> getSkillById(@PathVariable Long id) {
        SkillDto foundSkillDto = skillService.getSkillById(id);
        return ResponseEntity.status(HttpStatus.OK).body(foundSkillDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }
}
