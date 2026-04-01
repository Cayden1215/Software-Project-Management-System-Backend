package com.softwareprojectmanagement.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.softwareprojectmanagement.backend.dto.SprintDto;

@Service
public interface SprintService {

    public SprintDto createSprint(SprintDto sprintDto, Long projectId);

    public SprintDto getSprintById(Long id);

    public SprintDto updateSprint(Long id, SprintDto sprintDto);

    public void deleteSprint(Long id);

    public List<SprintDto> getSprintsByProjectId(Long projectId);

    public List<SprintDto> getAllSprints();
}
