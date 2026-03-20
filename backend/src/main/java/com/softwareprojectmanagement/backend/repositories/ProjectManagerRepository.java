package com.softwareprojectmanagement.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.softwareprojectmanagement.backend.entities.ProjectManager;
import java.util.Optional;

public interface ProjectManagerRepository extends JpaRepository<ProjectManager, Long>{
    Optional<ProjectManager> findByUsername(String username);
}
