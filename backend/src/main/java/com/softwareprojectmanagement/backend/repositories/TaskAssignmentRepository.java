package com.softwareprojectmanagement.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.softwareprojectmanagement.backend.entities.Project;
import com.softwareprojectmanagement.backend.entities.TaskAssignment;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {
    
    List<TaskAssignment> findByProject(Project project);
    
}
