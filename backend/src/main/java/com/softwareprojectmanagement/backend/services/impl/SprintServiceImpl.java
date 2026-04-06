package com.softwareprojectmanagement.backend.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softwareprojectmanagement.backend.dto.SprintDto;
import com.softwareprojectmanagement.backend.entities.Project;
import com.softwareprojectmanagement.backend.entities.Sprint;
import com.softwareprojectmanagement.backend.mappers.SprintMapper;
import com.softwareprojectmanagement.backend.repositories.SprintRepository;
import com.softwareprojectmanagement.backend.services.ProjectService;
import com.softwareprojectmanagement.backend.services.SprintService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class SprintServiceImpl implements SprintService {

    @Autowired
    private SprintRepository sprintRepository;

    @Autowired
    private ProjectService projectService;

    @Override
    public SprintDto createSprint(SprintDto sprintDto, Long projectId) {
        Project project = projectService.getProjectEntityById(projectId);

        Sprint sprint = SprintMapper.mapToSprint(sprintDto, project);
        sprint = sprintRepository.save(sprint);
        return SprintMapper.mapToSprintDto(sprint);
    }

    @Override
    public SprintDto getSprintById(Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sprint not found"));
        return SprintMapper.mapToSprintDto(sprint);
    }

    @Override
    public SprintDto updateSprint(Long id, SprintDto sprintDto) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sprint not found"));

        sprint.setSprintName(sprintDto.getSprintName());
        sprint.setStartDate(sprintDto.getStartDate());
        sprint.setEndDate(sprintDto.getEndDate());
        sprint.setSprintGoal(sprintDto.getSprintGoal());
        sprint.setSprintStatus(sprintDto.getSprintStatus());

        sprint = sprintRepository.save(sprint);
        return SprintMapper.mapToSprintDto(sprint);
    }

    @Override
    public void deleteSprint(Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sprint not found"));
        sprintRepository.delete(sprint);
    }

    @Override
    public List<SprintDto> getSprintsByProjectId(Long projectId) {
        Project project = projectService.getProjectEntityById(projectId);

        List<Sprint> sprints = new ArrayList<>(project.getSprints());
        return sprints.stream().map(SprintMapper::mapToSprintDto).toList();
    }

    @Override
    public List<SprintDto> getAllSprints() {
        List<Sprint> sprints = sprintRepository.findAll();
        return sprints.stream().map(SprintMapper::mapToSprintDto).toList();
    }
}
