package com.softwareprojectmanagement.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.softwareprojectmanagement.backend.dto.SkillDto;

@Service
public interface SkillService {

    public SkillDto createSkill(Long projectId, SkillDto skillDto);

    public SkillDto getSkillById(Long id);

    public void deleteSkill(Long id);

    public List<SkillDto> getAllSkillsByProject(Long projectID);

    public List<SkillDto> getAllSkills();
}
