package com.softwareprojectmanagement.backend.services.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softwareprojectmanagement.backend.dto.TaskDto;
import com.softwareprojectmanagement.backend.entities.Project;
import com.softwareprojectmanagement.backend.entities.Skill;
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
    public TaskDto createTask(Long projectId, TaskDto taskDto) {

        Project project = projectService.getProjectEntityById(projectId);

        Task task;

        if(taskDto.getSkillIDs()!=null){
            List<Skill> skills = taskDto.getSkillIDs().stream()
                .map(skillId -> {
                    Skill skill = project.getSkills().stream()
                            .filter(s -> s.getSkillID().equals(skillId))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Skill with ID " + skillId + " not found in project"));
                    return skill;
                })
                .collect(Collectors.toList());

                task = TaskMapper.mapToTask(taskDto, project, skills);
        }else{
            task = TaskMapper.mapToTask(taskDto, project, List.of());
        }

        // Validate and set dependencies
        if (taskDto.getDependencyIds() != null && !taskDto.getDependencyIds().isEmpty()) {
            validateAndSetDependencies(task, taskDto.getDependencyIds());
        } else {
            task.setDependencies(new HashSet<>());
        }

        Task savedTask = taskRepository.save(task);

        return TaskMapper.mapToTaskDto(savedTask);
    }

    @Override
    public TaskDto getTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        return TaskMapper.mapToTaskDto(task);
    }

    @Override
    public TaskDto updateTask(Long taskId, TaskDto taskDto){
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));

        Project project = task.getProject();

        task.setTaskName(taskDto.getTaskName());
        task.setEstimatedDuration(taskDto.getEstimatedDuration());
        task.setDescription(taskDto.getDescription());
        task.setTaskStatus(taskDto.getTaskStatus());
        task.setRequiredMemberNum(taskDto.getRequiredMemberNum());
        task.setStoryPoint(taskDto.getStoryPoint());

        if(taskDto.getSkillIDs()!=null){
            List<Skill> skills = taskDto.getSkillIDs().stream()
                .map(skillId -> {
                    Skill skill = project.getSkills().stream()
                            .filter(s -> s.getSkillID().equals(skillId))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Skill with ID " + skillId + " not found in project"));
                    return skill;
                })
                .collect(Collectors.toList());

            Set<Skill> skillsSet = new HashSet<>(skills);
            task.setSkills(skillsSet);
        }

        if(taskDto.getSprintID() != null){
            task.setSprint(project.getSprints().stream()
                .filter(s -> s.getSprintID().equals(taskDto.getSprintID()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sprint with ID " + taskDto.getSprintID() + " not found in project")));
        }

        // Validate and set dependencies if provided
        if(taskDto.getDependencyIds()!=null){
            validateAndSetDependencies(task, taskDto.getDependencyIds());
        }

        taskRepository.save(task);
        return TaskMapper.mapToTaskDto(task);
    }

    @Override
    public TaskDto updateTaskDependencies(Long taskId, List<Long> dependencyIds) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task with ID " + taskId + " not found"));

        // Validate and set new dependencies
        validateAndSetDependencies(task, dependencyIds);

        Task updatedTask = taskRepository.save(task);

        return TaskMapper.mapToTaskDto(updatedTask);
    }

    @Override
    public TaskDto removeDependency(Long taskId, Long dependencyId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task with ID " + taskId + " not found"));

        Task dependencyTask = taskRepository.findById(dependencyId)
            .orElseThrow(() -> new RuntimeException("Dependency task with ID " + dependencyId + " not found"));

        // Remove the dependency
        task.getDependencies().remove(dependencyTask);

        Task updatedTask = taskRepository.save(task);

        return TaskMapper.mapToTaskDto(updatedTask);
    }

    @Override
    public void deleteTask(Long id){
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        task.getDependentTasks().forEach(dependentTask -> dependentTask.getDependencies().remove(task));
        task.getDependencies().clear();
        taskRepository.delete(task);
    }

    @Override
    public List<TaskDto> listTasksByProjectId(Long projectId) {
        Project project = projectService.getProjectEntityById(projectId);
        List<Task> tasks = taskRepository.findByProject(project);
        return tasks.stream().map(TaskMapper::mapToTaskDto).toList();
    }
    
    @Override
    public List<Task> listTaskEntitiesByProjectId(Long projectId) {
        Project project = projectService.getProjectEntityById(projectId);
        return taskRepository.findByProject(project);
    }    


    /**
     * Validates and sets dependencies for a task, checking for:
     * - Self-dependencies
     * - Non-existent dependency IDs
     * - Circular dependencies
     */
    private void validateAndSetDependencies(Task task, List<Long> dependencyIds) {
        if (dependencyIds == null || dependencyIds.isEmpty()) {
            task.setDependencies(new HashSet<>());
            return;
        }

        // Check for self-dependency
        if (dependencyIds.contains(task.getTaskID())) {
            throw new RuntimeException("A task cannot depend on itself");
        }

        // Fetch all dependency tasks and validate they exist
        Set<Task> dependencies = dependencyIds.stream()
            .map(depId -> taskRepository.findById(depId)
                .orElseThrow(() -> new RuntimeException("Dependency task with ID " + depId + " not found")))
            .collect(Collectors.toSet());

        // Check for circular dependencies
        if (hasCircularDependency(task, dependencies)) {
            throw new RuntimeException("Circular dependency detected");
        }

        task.setDependencies(dependencies);
    }

    /**
     * Detects circular dependencies using depth-first search (DFS)
     * Returns true if adding dependencies to the task would create a circular dependency
     */
    private boolean hasCircularDependency(Task task, Set<Task> newDependencies) {
        // For each new dependency, check if it can reach back to this task
        for (Task dependency : newDependencies) {
            if (canReachTask(dependency, task, new HashSet<>())) {
                return true;
            }
        }
        return false;
    }

    /**
     * DFS to check if sourceTask can reach targetTask through existing dependencies
     */
    private boolean canReachTask(Task sourceTask, Task targetTask, Set<Long> visited) {
        if (sourceTask.getTaskID().equals(targetTask.getTaskID())) {
            return true;
        }

        if (visited.contains(sourceTask.getTaskID())) {
            return false;
        }

        visited.add(sourceTask.getTaskID());

        // Check all tasks that depend on sourceTask
        for (Task dependentTask : targetTask.getDependencies()) {
            if (canReachTask(dependentTask, targetTask, visited)) {
                return true;
            }
        }

        return false;
    }
    
}
