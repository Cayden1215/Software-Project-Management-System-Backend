package com.softwareprojectmanagement.backend.dto;

import java.time.LocalDate;

public class ProjectMemberDto {
    private Long ID;

    private Long projectID;
    
    private Long teamMemberID;

    private LocalDate enrollmentDate;

    private String projectRole;
}
