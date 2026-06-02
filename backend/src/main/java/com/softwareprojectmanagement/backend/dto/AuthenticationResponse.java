package com.softwareprojectmanagement.backend.dto;

public class AuthenticationResponse {
    private String token;
    private String role;

    public AuthenticationResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }

    // Getter
    public String getToken() { return token; }

    public String getRole() { return role; }
}
