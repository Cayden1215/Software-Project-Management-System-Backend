package com.softwareprojectmanagement.backend.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.softwareprojectmanagement.backend.dto.AuthenticationRequest;
import com.softwareprojectmanagement.backend.dto.AuthenticationResponse;
import com.softwareprojectmanagement.backend.repositories.UserRepository;
import com.softwareprojectmanagement.backend.security.JwtService;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userRepository, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // 1. Authenticate the user. If credentials are bad, this throws an exception.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. If we get here, the user is authenticated. Fetch them from the database.
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Generate the token based on the user's details and role
        var jwtToken = jwtService.generateToken(user);
        
        // 4. Return the token wrapped in our DTO
        return new AuthenticationResponse(jwtToken);
    }
}
