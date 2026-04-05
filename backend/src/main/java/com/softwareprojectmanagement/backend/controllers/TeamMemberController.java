/*package com.softwareprojectmanagement.backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softwareprojectmanagement.backend.dto.TeamMemberDto;
import com.softwareprojectmanagement.backend.services.TeamMemberService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("api/teammembers")
public class TeamMemberController {

    private TeamMemberService teamMemberService;
    
    @PostMapping("/create")
    public ResponseEntity<TeamMemberDto> createTeamMember(@RequestBody TeamMemberDto teamMemberDto){
        TeamMemberDto savedTeamMember = teamMemberService.createTeamMember(teamMemberDto);
        return new ResponseEntity<>(savedTeamMember,HttpStatus.CREATED);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<TeamMemberDto> getTeamMember(@PathVariable Long id) {
        TeamMemberDto retrievedTeamMember = teamMemberService.getTeamMember(id);
        return new ResponseEntity<>(retrievedTeamMember, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TeamMemberDto> updateTeamMember(@PathVariable Long id,@RequestBody TeamMemberDto teamMemberDto) {
        TeamMemberDto updatedTeamMember = teamMemberService.updateTeamMember(id, teamMemberDto);
        return new ResponseEntity<>(updatedTeamMember, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTeamMember(@PathVariable Long id) {
        teamMemberService.deleteTeamMember(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

*/
