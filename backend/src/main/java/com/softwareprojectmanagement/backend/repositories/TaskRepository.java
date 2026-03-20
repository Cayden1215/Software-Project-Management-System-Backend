package com.softwareprojectmanagement.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.softwareprojectmanagement.backend.entities.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

}
