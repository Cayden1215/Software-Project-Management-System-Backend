package com.softwareprojectmanagement.backend.mappers;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.softwareprojectmanagement.backend.dto.TaskDto;
import com.softwareprojectmanagement.backend.entities.Project;
import com.softwareprojectmanagement.backend.entities.Skill;
import com.softwareprojectmanagement.backend.entities.Sprint;
import com.softwareprojectmanagement.backend.entities.Task;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskMapper {

    public static TaskDto mapToTaskDto(Task task){
        TaskDto taskDto = new TaskDto();

        taskDto.setTaskID(task.getTaskID());
        taskDto.setTaskName(task.getTaskName());
        taskDto.setEstimatedDuration(task.getEstimatedDuration());
        taskDto.setDescription(task.getDescription());
        taskDto.setTaskStatus(task.getTaskStatus());
        taskDto.setRequiredMemberNum(task.getRequiredMemberNum());
        taskDto.setStoryPoint(task.getStoryPoint());
        taskDto.setProjectID(task.getProject().getProjectID());
        taskDto.setSprintID(task.getSprint() != null ? task.getSprint().getSprintID() : null);
        taskDto.setSkillIDs(task.getSkills().stream().map(Skill::getSkillID).toList());
        taskDto.setDependencyIds(task.getDependencies().stream().map(Task::getTaskID).toList());

        return taskDto;
    }

    public static Task mapToTask(TaskDto taskDto, Project project, Sprint sprint){//unused

        Task task = new Task();

        task.setTaskName(taskDto.getTaskName());
        task.setEstimatedDuration(taskDto.getEstimatedDuration());
        task.setDescription(taskDto.getDescription());
        task.setTaskStatus(taskDto.getTaskStatus());
        task.setRequiredMemberNum(taskDto.getRequiredMemberNum());
        task.setStoryPoint(taskDto.getStoryPoint());
        task.setProject(project);
        task.setSprint(sprint);
        task.setDependencies(new HashSet<>());

        return task;
    }

    public static Task mapToTask(TaskDto taskDto, Project project, List<Skill> skills){

        Task task = new Task();

        task.setTaskName(taskDto.getTaskName());
        task.setEstimatedDuration(taskDto.getEstimatedDuration());
        task.setDescription(taskDto.getDescription());
        task.setTaskStatus(taskDto.getTaskStatus());
        task.setRequiredMemberNum(taskDto.getRequiredMemberNum());
        task.setStoryPoint(taskDto.getStoryPoint());
        task.setProject(project);
        task.setSkills(new HashSet<Skill>(skills));
        task.setDependencies(new HashSet<>());


        return task;
    }
}
