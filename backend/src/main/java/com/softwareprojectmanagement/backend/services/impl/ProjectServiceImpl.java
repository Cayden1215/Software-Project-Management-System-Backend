package com.softwareprojectmanagement.backend.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softwareprojectmanagement.backend.dto.ProjectDto;
import com.softwareprojectmanagement.backend.dto.ProjectMemberDto;
import com.softwareprojectmanagement.backend.dto.TeamMemberDto;
import com.softwareprojectmanagement.backend.entities.Project;
import com.softwareprojectmanagement.backend.entities.ProjectManager;
import com.softwareprojectmanagement.backend.entities.ProjectMember;
import com.softwareprojectmanagement.backend.entities.TeamMember;
import com.softwareprojectmanagement.backend.mappers.ProjectMapper;
import com.softwareprojectmanagement.backend.repositories.ProjectManagerRepository;
import com.softwareprojectmanagement.backend.repositories.ProjectMemberRepository;
import com.softwareprojectmanagement.backend.repositories.ProjectRepository;
import com.softwareprojectmanagement.backend.repositories.TeamMemberRepository;
import com.softwareprojectmanagement.backend.services.ProjectService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ProjectServiceImpl implements ProjectService{

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectManagerRepository projectManagerRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;
    
    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Override
    public ProjectDto createProject(String pmEmail, ProjectDto projectDto) {
        ProjectManager projectManager = projectManagerRepository.findByEmail(pmEmail).orElseThrow(() -> new RuntimeException("Project Manager not found"));
        Project savedProject = ProjectMapper.mapToProject(projectDto, projectManager);
        savedProject = projectRepository.save(savedProject);
        return ProjectMapper.mapToProjectDto(savedProject);
    }

    @Override
    public ProjectDto getProjectById(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));
        return ProjectMapper.mapToProjectDto(project);
    }

    @Override
    public Project getProjectEntityById(Long id) {
        return projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));
    }

    @Override
    public List<ProjectDto> getAllProjects(String pmEmail){
        ProjectManager pm = projectManagerRepository.findByEmail(pmEmail).orElseThrow(() -> new RuntimeException("Project Manager not found"));
        Long pmID = pm.getUserID();
        List<Project> projects = projectRepository.findByProjectManagerUserID(pmID);
        return projects.stream().map(ProjectMapper::mapToProjectDto).toList();
    }

    @Override
    public ProjectDto updateProject(ProjectDto projectDto){
        Project project = projectRepository.findById(projectDto.getProjectID()).orElseThrow(() -> new RuntimeException("Project not found"));
        ProjectManager projectManager = projectManagerRepository.findById(projectDto.getProjectManagerID()).orElseThrow(() -> new RuntimeException("Project Manager not found"));

        project.setProjectName(projectDto.getProjectName());
        project.setProjectDescription(projectDto.getProjectDescription());
        project.setStartDate(projectDto.getStartDate());
        project.setDeadline(projectDto.getDeadline());
        project.setProjectStatus(projectDto.getProjectStatus());
        project.setProjectManager(projectManager);

        projectRepository.save(project);
        return ProjectMapper.mapToProjectDto(project);
    }

    @Override
    public void deleteProject(Long id){
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));
        projectRepository.delete(project);
    }

    @Override
    public void enrollTeamMemberToProject(Long projectId,ProjectMemberDto projectMemberDto){

        TeamMember teamMember = teamMemberRepository.findByEmail(projectMemberDto.getTeamMemberEmail()).orElseThrow(() -> new RuntimeException("Team Member not found"));
        
        String projectRole = projectMemberDto.getProjectRole();

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));

        if (projectMemberRepository.findByProjectProjectIDAndTeamMemberUserID(projectId, teamMember.getUserID()).isPresent()) {
            throw new RuntimeException("Team Member is already enrolled in the project");
        }

        ProjectMember projectMember = new ProjectMember(
            null,
            project,
            teamMember,
            java.time.LocalDate.now(),
            projectRole,
            null
        );

        projectMemberRepository.save(projectMember);
    }

    @Override
    public List<ProjectDto> getAllEnrolledProjects(Long tmID){
        List<ProjectMember> projectMembers = projectMemberRepository.findByTeamMemberUserID(tmID);

        return projectMembers.stream().map(pm -> ProjectMapper.mapToProjectDto(pm.getProject())).toList();
    }

    @Override
    public List<ProjectMemberDto> getProjectTeamMembersDto(Project project){
        List<ProjectMember> projectMembers = new ArrayList<>(project.getProjectMembers());

        List<ProjectMemberDto> projectMemberDtos = projectMembers.stream()
            .map(pm -> new ProjectMemberDto(
                pm.getID(),
                pm.getProject().getProjectID(),
                pm.getTeamMember().getUserID(),
                pm.getTeamMember().getName(),
                pm.getTeamMember().getEmail(),
                pm.getEnrollmentDate(),
                pm.getProjectRole(),
                new ArrayList<>(pm.getSkills())
            ))
            .toList();

        return projectMemberDtos;
    }

    @Override
    public List<TeamMember> getProjectTeamMembers(Project project){
        List<ProjectMember> projectMembers = new ArrayList<>(project.getProjectMembers());
        List<TeamMember> teamMembers = projectMembers.stream()
            .map(ProjectMember::getTeamMember)
            .distinct()
            .toList();

        return teamMembers;
    }
}
