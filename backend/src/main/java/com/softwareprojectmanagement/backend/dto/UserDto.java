package com.softwareprojectmanagement.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long userID;

    private String name;

    private String password;
    
    private String email;

    private String role;
}
