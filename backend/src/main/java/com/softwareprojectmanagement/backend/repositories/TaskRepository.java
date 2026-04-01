package com.softwareprojectmanagement.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.softwareprojectmanagement.backend.entities.Project;
import com.softwareprojectmanagement.backend.entities.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProject(Project project);
}
