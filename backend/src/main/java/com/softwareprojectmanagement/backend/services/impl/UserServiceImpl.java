package com.softwareprojectmanagement.backend.services.impl;

import com.softwareprojectmanagement.backend.services.UserService;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.softwareprojectmanagement.backend.dto.UserDto;
import com.softwareprojectmanagement.backend.entities.ProjectManager;
import com.softwareprojectmanagement.backend.entities.TeamMember;
import com.softwareprojectmanagement.backend.entities.User;
import com.softwareprojectmanagement.backend.repositories.ProjectManagerRepository;
import com.softwareprojectmanagement.backend.repositories.TeamMemberRepository;
import com.softwareprojectmanagement.backend.repositories.UserRepository;

@AllArgsConstructor
@NoArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectManagerRepository projectManagerRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User createUser(UserDto userDto) {

        if(userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        String role = userDto.getRole();

        if (role.equals("PROJECT_MANAGER")) {
            ProjectManager projectManager = new ProjectManager();

            projectManager.setUsername(userDto.getUsername());
            projectManager.setPassword(passwordEncoder.encode(userDto.getPassword()));
            projectManager.setEmail(userDto.getEmail());
            projectManager.setRole(com.softwareprojectmanagement.backend.entities.Role.PROJECT_MANAGER);

            
            try {
                User savedProjectManager = projectManagerRepository.save(projectManager);
                
                return savedProjectManager;
            }
                catch (Exception e) {
                    throw new RuntimeException("Error creating project manager: " + e.getMessage());
                }

        
        }

        if(role.equals("TEAM_MEMBER")) {
            TeamMember teamMember = new TeamMember();

            teamMember.setUserID(userDto.getUserID());
            teamMember.setUsername(userDto.getUsername());
            teamMember.setPassword(passwordEncoder.encode(userDto.getPassword()));
            teamMember.setEmail(userDto.getEmail());
            teamMember.setAvailability(true);
            teamMember.setRole(com.softwareprojectmanagement.backend.entities.Role.TEAM_MEMBER);

            try {
                User savedTeamMember = teamMemberRepository.save(teamMember);
                return savedTeamMember;
            } catch (Exception e) {
                throw new RuntimeException("Error creating team member: " + e.getMessage());
            }
        }

        return null;
    }

    @Override
    public ResponseEntity<String> deleteUser(Long id) {

        try {
            userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }

}
