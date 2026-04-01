package com.softwareprojectmanagement.backend.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMemberDto {
    private Long ID;

    private Long projectID;
    
    private Long teamMemberID;

    private LocalDate enrollmentDate;

    private String projectRole;
}
