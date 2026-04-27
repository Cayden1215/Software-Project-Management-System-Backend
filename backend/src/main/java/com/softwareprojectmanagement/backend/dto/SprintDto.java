package com.softwareprojectmanagement.backend.dto;

import java.time.LocalDate;

import com.softwareprojectmanagement.backend.entities.Project;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SprintDto {
    
    private Long sprintID;
    
    private String sprintName;
   
    private LocalDate startDate;

    private LocalDate endDate;

    private String sprintGoal;
    
    private String sprintStatus;

    private Long project;
}
