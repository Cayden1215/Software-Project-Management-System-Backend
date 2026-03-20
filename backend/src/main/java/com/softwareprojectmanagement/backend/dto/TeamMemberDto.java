package com.softwareprojectmanagement.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TeamMemberDto {
    
    private Long userID;

    private String username;

    private String password;

    private String email;
    
    private Boolean availability;
}
