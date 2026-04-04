package com.softwareprojectmanagement.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.softwareprojectmanagement.backend.entities.ProjectMember;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long>{

    public java.util.List<ProjectMember> findByTeamMemberUserID(Long teamMemberID);

    Optional<ProjectMember> findByProjectProjectIDAndTeamMemberUserID(Long projectId, Long teamMemberId);
    
}
