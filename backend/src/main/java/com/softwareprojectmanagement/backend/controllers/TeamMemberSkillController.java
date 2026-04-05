/*package com.softwareprojectmanagement.backend.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softwareprojectmanagement.backend.dto.TeamMemberSkillDto;
import com.softwareprojectmanagement.backend.services.TeamMemberSkillService;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@AllArgsConstructor
@RestController
@RequestMapping("api/v1/team-member-skills")
public class TeamMemberSkillController {

    private TeamMemberSkillService teamMemberSkillService;

    @PostMapping
    public ResponseEntity<TeamMemberSkillDto> createTeamMemberSkill(
            @RequestBody TeamMemberSkillDto teamMemberSkillDto) {
        TeamMemberSkillDto savedTeamMemberSkillDto = teamMemberSkillService
                .createTeamMemberSkill(teamMemberSkillDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTeamMemberSkillDto);
    }

    @GetMapping("/{userID}")
    public ResponseEntity<TeamMemberSkillDto> getTeamMemberSkillById(@PathVariable Long userID) {
        TeamMemberSkillDto foundTeamMemberSkillDto = teamMemberSkillService.getTeamMemberSkillById(userID);
        return ResponseEntity.status(HttpStatus.OK).body(foundTeamMemberSkillDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeamMemberSkill(@PathVariable Long id) {
        teamMemberSkillService.deleteTeamMemberSkill(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/team-member/{userID}")
    public ResponseEntity<List<TeamMemberSkillDto>> getSkillsByTeamMember(@PathVariable Long userID) {
        List<TeamMemberSkillDto> skills = teamMemberSkillService.getSkillsByTeamMember(userID);
        return ResponseEntity.status(HttpStatus.OK).body(skills);
    }

    @GetMapping("/skill/{skillID}")
    public ResponseEntity<List<TeamMemberSkillDto>> getTeamMembersBySkill(@PathVariable Long skillID) {
        List<TeamMemberSkillDto> teamMembers = teamMemberSkillService.getTeamMembersBySkill(skillID);
        return ResponseEntity.status(HttpStatus.OK).body(teamMembers);
    }

    @GetMapping
    public ResponseEntity<List<TeamMemberSkillDto>> getAllTeamMemberSkills() {
        List<TeamMemberSkillDto> allTeamMemberSkills = teamMemberSkillService.getAllTeamMemberSkills();
        return ResponseEntity.status(HttpStatus.OK).body(allTeamMemberSkills);
    }
}

*/