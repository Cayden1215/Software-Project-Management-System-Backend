package com.softwareprojectmanagement.backend.dto;

import java.time.LocalDate;
import java.util.List;

import com.softwareprojectmanagement.backend.entities.Task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {
    private Long projectID;
    private String projectName;
    private String projectDescription;
    private LocalDate startDate;
    private LocalDate deadline;
    private String projectStatus;
    private Long projectManagerID;
    private List<TaskDto> tasks;
}
