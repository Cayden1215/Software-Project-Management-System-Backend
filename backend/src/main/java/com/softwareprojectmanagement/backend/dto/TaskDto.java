package com.softwareprojectmanagement.backend.dto;

import java.util.List;

import com.softwareprojectmanagement.backend.entities.Skill;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaskDto {
    private Long taskID;
    private String taskName;
    private int estimatedDuration;
    private String description;
    private String taskStatus;
    private int requiredMemberNum;
    private int storyPoint;
    private Long projectID;
    private Long sprintID;
    private List<Long> skillIDs;
    private List<Long> dependencyIds;
}
