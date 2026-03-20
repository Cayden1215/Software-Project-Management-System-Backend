package com.softwareprojectmanagement.backend.mappers;

import com.softwareprojectmanagement.backend.dto.TaskDto;
import com.softwareprojectmanagement.backend.entities.Project;
import com.softwareprojectmanagement.backend.entities.Sprint;
import com.softwareprojectmanagement.backend.entities.Task;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskMapper {

    public static TaskDto mapToTaskDto(Task task){
        return new TaskDto(
            task.getTaskID(),
            task.getTaskName(),
            task.getEstimatedDuration(),
            task.getDescription(),
            task.getTaskStatus(),
            task.getRequiredMemberNum(),
            task.getStoryPoint(),
            task.getProject().getProjectID(),
            task.getSprint().getSprintID()
        );
    }

    public static Task mapToTask(TaskDto taskDto, Project project, Sprint sprint){

        Task task = new Task();

        task.setTaskName(taskDto.getTaskName());
        task.setEstimatedDuration(taskDto.getEstimatedDuration());
        task.setDescription(taskDto.getDescription());
        task.setTaskStatus(taskDto.getTaskStatus());
        task.setRequiredMemberNum(taskDto.getRequiredMemberNum());
        task.setStoryPoint(taskDto.getStoryPoint());
        task.setProject(project);
        task.setSprint(sprint);

        return task;
    }

    public static Task mapToTask(TaskDto taskDto, Project project){

        Task task = new Task();

        task.setTaskName(taskDto.getTaskName());
        task.setEstimatedDuration(taskDto.getEstimatedDuration());
        task.setDescription(taskDto.getDescription());
        task.setTaskStatus(taskDto.getTaskStatus());
        task.setRequiredMemberNum(taskDto.getRequiredMemberNum());
        task.setStoryPoint(taskDto.getStoryPoint());
        task.setProject(project);

        return task;
    }
}
