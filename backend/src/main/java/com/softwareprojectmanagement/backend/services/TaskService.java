package com.softwareprojectmanagement.backend.services;

import java.util.List;

import com.softwareprojectmanagement.backend.dto.TaskDto;
import com.softwareprojectmanagement.backend.entities.Task;


public interface TaskService {
    public TaskDto createTask(Long projectId, TaskDto taskDto);
    public TaskDto getTaskById(Long id);
    public TaskDto updateTask(Long taskId, TaskDto taskDto);
    public void deleteTask(Long id);
    public List<TaskDto> listTasksByProjectId(Long projectId);
    public List<Task> listTaskEntitiesByProjectId(Long projectId);
    public TaskDto updateTaskDependencies(Long taskId, List<Long> dependencyIds);
    public TaskDto removeDependency(Long taskId, Long dependencyId);
}
