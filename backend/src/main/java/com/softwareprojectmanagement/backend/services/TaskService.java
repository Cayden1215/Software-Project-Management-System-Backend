package com.softwareprojectmanagement.backend.services;

import org.springframework.stereotype.Service;

import com.softwareprojectmanagement.backend.dto.TaskDto;

@Service
public interface TaskService {
    public TaskDto createTask(TaskDto taskDto);
    public TaskDto getTaskById(Long id);
    public TaskDto updateTask(TaskDto taskDto);
    public void deleteTask(Long id);
}
