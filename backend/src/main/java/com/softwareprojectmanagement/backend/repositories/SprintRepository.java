package com.softwareprojectmanagement.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.softwareprojectmanagement.backend.entities.Sprint;


public interface SprintRepository extends JpaRepository<Sprint, Long>{

}
