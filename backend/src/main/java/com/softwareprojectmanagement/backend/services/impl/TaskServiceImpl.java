package com.softwareprojectmanagement.backend.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softwareprojectmanagement.backend.dto.TaskDto;
import com.softwareprojectmanagement.backend.entities.Project;
import com.softwareprojectmanagement.backend.entities.Task;
import com.softwareprojectmanagement.backend.mappers.TaskMapper;
import com.softwareprojectmanagement.backend.repositories.ProjectRepository;
import com.softwareprojectmanagement.backend.repositories.TaskRepository;
import com.softwareprojectmanagement.backend.services.ProjectService;
import com.softwareprojectmanagement.backend.services.TaskService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    @Override
    public TaskDto createTask(TaskDto taskDto) {

        Project project = projectService.getProjectEntityById(taskDto.getProjectID());

        Task task = TaskMapper.mapToTask(taskDto, project);

        Task savedTask = taskRepository.save(task);

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

        task.setTaskName(taskDto.getTaskName());
        task.setEstimatedDuration(taskDto.getEstimatedDuration());
        task.setDescription(taskDto.getDescription());
        task.setTaskStatus(taskDto.getTaskStatus());
        task.setRequiredMemberNum(taskDto.getRequiredMemberNum());
        task.setStoryPoint(taskDto.getStoryPoint());

        taskRepository.save(task);
        return TaskMapper.mapToTaskDto(task);
    }

    @Override
    public void deleteTask(Long id){
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepository.delete(task);
    }

    @Override
    public List<TaskDto> listTasksByProjectId(Long projectId) {
        Project project = projectService.getProjectEntityById(projectId);
        List<Task> tasks = taskRepository.findByProject(project);
        return tasks.stream().map(TaskMapper::mapToTaskDto).toList();
    }    
}
