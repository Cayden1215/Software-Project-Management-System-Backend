package com.softwareprojectmanagement.backend.mappers;

import com.softwareprojectmanagement.backend.dto.SprintDto;
import com.softwareprojectmanagement.backend.entities.Project;
import com.softwareprojectmanagement.backend.entities.Sprint;

public class SprintMapper {
    public static SprintDto mapToSprintDto(Sprint sprint) {
        if (sprint == null) {
            return null;
        }
        SprintDto sprintDto = new SprintDto();
        sprintDto.setSprintID(sprint.getSprintID());
        sprintDto.setSprintName(sprint.getSprintName());
        sprintDto.setStartDate(sprint.getStartDate());
        sprintDto.setEndDate(sprint.getEndDate());
        sprintDto.setSprintGoal(sprint.getSprintGoal());
        sprintDto.setSprintStatus(sprint.getSprintStatus());
        sprintDto.setProject(sprint.getProject().getProjectID());

        return sprintDto;
    }

    public static Sprint mapToSprint(SprintDto sprintDto, Project project) {
        if (sprintDto == null) {
            return null;
        }
        Sprint sprint = new Sprint();
        sprint.setSprintID(sprintDto.getSprintID());
        sprint.setSprintName(sprintDto.getSprintName());
        sprint.setStartDate(sprintDto.getStartDate());
        sprint.setEndDate(sprintDto.getEndDate());
        sprint.setSprintGoal(sprintDto.getSprintGoal());
        sprint.setSprintStatus(sprintDto.getSprintStatus());
        sprint.setProject(project);
        return sprint;
    }
}
