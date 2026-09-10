package com.example.taskmanager.exception;

public class TaskNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1048735304722717332L;

    public TaskNotFoundException(Long id) {
        super("Task not found with id: " + id);
    }
}
