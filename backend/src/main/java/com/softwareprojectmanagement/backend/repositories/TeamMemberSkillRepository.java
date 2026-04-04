package com.softwareprojectmanagement.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.softwareprojectmanagement.backend.entities.TeamMemberSkill;
import java.util.List;

public interface TeamMemberSkillRepository extends JpaRepository<TeamMemberSkill, Long> {
    
    public List<TeamMemberSkill> findByTeamMemberUserID(Long userID);
    
    public List<TeamMemberSkill> findBySkillIDSkillID(Long skillID);
    
    public boolean existsByTeamMemberUserIDAndSkillID(Long userID, Long skillID);
}
