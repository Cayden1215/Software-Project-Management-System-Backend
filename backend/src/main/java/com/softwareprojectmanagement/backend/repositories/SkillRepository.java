package com.softwareprojectmanagement.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.softwareprojectmanagement.backend.entities.Skill;
import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    
    public List<Skill> findByProjectProjectID(Long projectID);
    
    public boolean existsBySkillNameAndProjectProjectID(String skillName, Long projectID);

    public boolean existsBySkillIDAndProject(Long skillId, Long projectID);
}
