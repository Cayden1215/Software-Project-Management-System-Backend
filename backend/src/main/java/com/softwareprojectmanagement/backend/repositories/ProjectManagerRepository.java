package com.softwareprojectmanagement.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.softwareprojectmanagement.backend.entities.ProjectManager;

public interface ProjectManagerRepository extends JpaRepository<ProjectManager, Long>{

}
