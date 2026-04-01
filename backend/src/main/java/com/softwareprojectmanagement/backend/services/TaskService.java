package com.softwareprojectmanagement.backend.services;

import java.util.List;

import com.softwareprojectmanagement.backend.dto.TaskDto;


public interface TaskService {
    public TaskDto createTask(TaskDto taskDto);
    public TaskDto getTaskById(Long id);
    public TaskDto updateTask(TaskDto taskDto);
    public void deleteTask(Long id);
    public List<TaskDto> listTasksByProjectId(Long projectId);
}
