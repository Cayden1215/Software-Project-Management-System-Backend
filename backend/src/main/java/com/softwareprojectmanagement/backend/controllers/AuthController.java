package com.softwareprojectmanagement.backend.controllers;

import com.softwareprojectmanagement.backend.dto.AuthResponse;
import com.softwareprojectmanagement.backend.dto.LoginRequest;
import com.softwareprojectmanagement.backend.dto.ProjectManagerDto;
import com.softwareprojectmanagement.backend.dto.ProjectMemberDto;
import com.softwareprojectmanagement.backend.entities.ProjectManager;
import com.softwareprojectmanagement.backend.entities.TeamMember;
import com.softwareprojectmanagement.backend.repositories.ProjectManagerRepository;
import com.softwareprojectmanagement.backend.repositories.TeamMemberRepository;
import com.softwareprojectmanagement.backend.security.CustomUserPrincipal;
import com.softwareprojectmanagement.backend.security.JwtProvider;
import com.softwareprojectmanagement.backend.services.ProjectManagerService;
import com.softwareprojectmanagement.backend.services.TeamMemberService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {

    private AuthenticationManager authenticationManager;
    private JwtProvider jwtProvider;
    private ProjectManagerRepository projectManagerRepository;
    private TeamMemberRepository teamMemberRepository;
    private PasswordEncoder passwordEncoder;
    private ProjectManagerService projectManagerService;
    private TeamMemberService teamMemberService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            CustomUserPrincipal userPrincipal = (CustomUserPrincipal) authentication.getPrincipal();
            String token = jwtProvider.generateToken(authentication);

            AuthResponse authResponse = new AuthResponse();
            authResponse.setToken(token);
            authResponse.setId(userPrincipal.getId());
            authResponse.setUsername(userPrincipal.getUsername());
            authResponse.setUserType(userPrincipal.getUserType());

            if ("MANAGER".equals(userPrincipal.getUserType())) {
                Optional<ProjectManager> pm = projectManagerRepository.findById(userPrincipal.getId());
                if (pm.isPresent()) {
                    authResponse.setEmail(pm.get().getEmail());
                }
            } else if ("TEAM_MEMBER".equals(userPrincipal.getUserType())) {
                Optional<TeamMember> tm = teamMemberRepository.findById(userPrincipal.getId());
                if (tm.isPresent()) {
                    authResponse.setEmail(tm.get().getEmail());
                }
            }

            authResponse.setMessage("User authenticated successfully");
            return new ResponseEntity<>(authResponse, HttpStatus.OK);

        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", loginRequest.getUsername());
            AuthResponse authResponse = new AuthResponse();
            authResponse.setMessage("Invalid username or password");
            return new ResponseEntity<>(authResponse, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/login/manager")
    public ResponseEntity<?> loginProjectManager(@RequestBody LoginRequest loginRequest) {
        Optional<ProjectManager> manager = projectManagerRepository.findByUsername(loginRequest.getUsername());

        if (manager.isEmpty()) {
            AuthResponse authResponse = new AuthResponse();
            authResponse.setMessage("Project Manager not found");
            return new ResponseEntity<>(authResponse, HttpStatus.UNAUTHORIZED);
        }

        ProjectManager pm = manager.get();
        if (!passwordEncoder.matches(loginRequest.getPassword(), pm.getPassword())) {
            AuthResponse authResponse = new AuthResponse();
            authResponse.setMessage("Invalid password");
            return new ResponseEntity<>(authResponse, HttpStatus.UNAUTHORIZED);
        }

        String token = jwtProvider.generateTokenFromId(pm.getUserID(), pm.getUsername(), "MANAGER");

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(token);
        authResponse.setId(pm.getUserID());
        authResponse.setUsername(pm.getUsername());
        authResponse.setEmail(pm.getEmail());
        authResponse.setUserType("MANAGER");
        authResponse.setMessage("Project Manager logged in successfully");

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @PostMapping("/login/team-member")
    public ResponseEntity<?> loginTeamMember(@RequestBody LoginRequest loginRequest) {
        Optional<TeamMember> member = teamMemberRepository.findByUsername(loginRequest.getUsername());

        if (member.isEmpty()) {
            AuthResponse authResponse = new AuthResponse();
            authResponse.setMessage("Team Member not found");
            return new ResponseEntity<>(authResponse, HttpStatus.UNAUTHORIZED);
        }

        TeamMember tm = member.get();
        if (!passwordEncoder.matches(loginRequest.getPassword(), tm.getPassword())) {
            AuthResponse authResponse = new AuthResponse();
            authResponse.setMessage("Invalid password");
            return new ResponseEntity<>(authResponse, HttpStatus.UNAUTHORIZED);
        }

        String token = jwtProvider.generateTokenFromId(tm.getUserID(), tm.getUsername(), "TEAM_MEMBER");

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(token);
        authResponse.setId(tm.getUserID());
        authResponse.setUsername(tm.getUsername());
        authResponse.setEmail(tm.getEmail());
        authResponse.setUserType("TEAM_MEMBER");
        authResponse.setMessage("Team Member logged in successfully");

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }
}
