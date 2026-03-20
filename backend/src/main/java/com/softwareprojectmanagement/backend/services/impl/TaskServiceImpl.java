package com.softwareprojectmanagement.backend.services.impl;

import com.softwareprojectmanagement.backend.dto.TaskDto;
import com.softwareprojectmanagement.backend.entities.Project;
import com.softwareprojectmanagement.backend.entities.Task;
import com.softwareprojectmanagement.backend.mappers.TaskMapper;
import com.softwareprojectmanagement.backend.repositories.ProjectRepository;
import com.softwareprojectmanagement.backend.repositories.TaskRepository;
import com.softwareprojectmanagement.backend.services.TaskService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaskServiceImpl implements TaskService {

    private TaskRepository taskRepository;
    private ProjectRepository projectRepository;

    @Override
    public TaskDto createTask(TaskDto taskDto) {

        Project project = projectRepository.findById(taskDto.getProjectID()).orElseThrow(() -> new RuntimeException("Project not found"));

        Task savedTask = TaskMapper.mapToTask(taskDto, project);

        taskRepository.save(savedTask);

        return TaskMapper.mapToTaskDto(savedTask);
    }

    @Override
    public TaskDto getTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        return TaskMapper.mapToTaskDto(task);
    }

    @Override
    public TaskDto updateTask(TaskDto taskDto){
        Task task = taskRepository.findById(taskDto.getTaskID()).orElseThrow(() -> new RuntimeException("Task not found"));
        Project project = projectRepository.findById(taskDto.getProjectID()).orElseThrow(() -> new RuntimeException("Project not found"));

        task.setTaskName(taskDto.getTaskName());
        task.setEstimatedDuration(taskDto.getEstimatedDuration());
        task.setDescription(taskDto.getDescription());
        task.setTaskStatus(taskDto.getTaskStatus());
        task.setRequiredMemberNum(taskDto.getRequiredMemberNum());
        task.setStoryPoint(taskDto.getStoryPoint());
        task.setProject(project);

        taskRepository.save(task);
        return TaskMapper.mapToTaskDto(task);
    }

    @Override
    public void deleteTask(Long id){
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepository.delete(task);
    }
}
