package com.softwareprojectmanagement.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectManagerDto {
    private Long userID;

    private String username;

    private String password;
    
    private String email;
}
