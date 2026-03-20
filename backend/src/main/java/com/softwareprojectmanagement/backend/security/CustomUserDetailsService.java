package com.softwareprojectmanagement.backend.security;

import com.softwareprojectmanagement.backend.entities.ProjectManager;
import com.softwareprojectmanagement.backend.entities.TeamMember;
import com.softwareprojectmanagement.backend.repositories.ProjectManagerRepository;
import com.softwareprojectmanagement.backend.repositories.TeamMemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private ProjectManagerRepository projectManagerRepository;
    private TeamMemberRepository teamMemberRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try to find in ProjectManager
        Optional<ProjectManager> projectManager = projectManagerRepository.findByUsername(username);
        if (projectManager.isPresent()) {
            ProjectManager pm = projectManager.get();
            return new CustomUserPrincipal(
                    pm.getUserID(),
                    pm.getUsername(),
                    pm.getPassword(),
                    "MANAGER",
                    true
            );
        }

        // Try to find in TeamMember
        Optional<TeamMember> teamMember = teamMemberRepository.findByUsername(username);
        if (teamMember.isPresent()) {
            TeamMember tm = teamMember.get();
            return new CustomUserPrincipal(
                    tm.getUserID(),
                    tm.getUsername(),
                    tm.getPassword(),
                    "TEAM_MEMBER",
                    true
            );
        }

        throw new UsernameNotFoundException("User not found: " + username);
    }

    public CustomUserPrincipal loadUserById(Long userId, String userType) throws UsernameNotFoundException {
        if ("MANAGER".equals(userType)) {
            Optional<ProjectManager> projectManager = projectManagerRepository.findById(userId);
            if (projectManager.isPresent()) {
                ProjectManager pm = projectManager.get();
                return new CustomUserPrincipal(
                        pm.getUserID(),
                        pm.getUsername(),
                        pm.getPassword(),
                        "MANAGER",
                        true
                );
            }
        } else if ("TEAM_MEMBER".equals(userType)) {
            Optional<TeamMember> teamMember = teamMemberRepository.findById(userId);
            if (teamMember.isPresent()) {
                TeamMember tm = teamMember.get();
                return new CustomUserPrincipal(
                        tm.getUserID(),
                        tm.getUsername(),
                        tm.getPassword(),
                        "TEAM_MEMBER",
                        true
                );
            }
        }

        throw new UsernameNotFoundException("User not found with ID: " + userId + " and type: " + userType);
    }
}
