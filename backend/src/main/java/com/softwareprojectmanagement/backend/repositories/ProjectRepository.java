package com.softwareprojectmanagement.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.softwareprojectmanagement.backend.entities.Project;
import java.util.List;
import com.softwareprojectmanagement.backend.entities.ProjectMember;


public interface ProjectRepository extends JpaRepository<Project, Long> {

    public java.util.List<Project> findByProjectManagerUserID(Long userID);

}
