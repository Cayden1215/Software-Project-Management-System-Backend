package com.softwareprojectmanagement.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.softwareprojectmanagement.backend.entities.TeamMember;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    Optional<TeamMember> findByUsername(String username);

    Optional<TeamMember> findByEmail(String email);
}
