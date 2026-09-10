package com.example.taskmanager.controller;

import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskEntity;
import com.example.taskmanager.repository.TaskRepository;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private static final Logger logger = LogManager.getLogger(TaskController.class);

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping
    public List<TaskEntity> listTasks() {

        logger.info("Fetching all tasks");

        List<TaskEntity> tasks = taskRepository.findAll();

        logger.info("Fetched {} tasks", tasks.size());

        return tasks;
    }

    @GetMapping("/{id}")
    public TaskEntity getTask(@PathVariable Long id) {

        logger.info("Fetching task with id: {}", id);

        return taskRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Task not found with id: {}", id);
                    return new TaskNotFoundException(id);
                });
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskEntity createTask(@Valid @RequestBody Task task) {

        logger.info("Creating task with title: {}", task.getTitle());

        TaskEntity taskEntity =
                new TaskEntity(task.getTitle(), task.getDescription());

        TaskEntity savedTask = taskRepository.save(taskEntity);

        logger.info("Task created successfully with id: {}", savedTask.getId());

        return savedTask;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {

        logger.info("Deleting task with id: {}", id);

        if (!taskRepository.existsById(id)) {
            logger.warn("Cannot delete task. Task not found with id: {}", id);
            throw new TaskNotFoundException(id);
        }

        taskRepository.deleteById(id);

        logger.info("Task deleted successfully with id: {}", id);
    }
}