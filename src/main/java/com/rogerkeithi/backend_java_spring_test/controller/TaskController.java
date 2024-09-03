package com.rogerkeithi.backend_java_spring_test.controller;

import com.rogerkeithi.backend_java_spring_test.DTO.TaskDTO.CreateTaskDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.TaskDTO.TaskDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.TaskDTO.UpdateTaskDTO;
import com.rogerkeithi.backend_java_spring_test.services.interfaces.ITaskService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final ITaskService taskService;

    @Autowired
    public TaskController(ITaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(
            summary = "Get user tasks",
            description = "Get all tasks from the user authenticated"
    )
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getUserTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort
    ) {
        List<TaskDTO> tasks = taskService.getSelfUserTasks(status, sort);
        return ResponseEntity.ok(tasks);
    }

    @Operation(
            summary = "Create task",
            description = "Create a new task"
    )
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody CreateTaskDTO task) {
        TaskDTO createdTask = taskService.createTask(task);
        return ResponseEntity.ok(createdTask);
    }

    @Operation(
            summary = "Delete task",
            description = "Delete a task using her id as a parameter"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Update task",
            description = "Update a task using her id as a parameter"
    )
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @RequestBody UpdateTaskDTO task) {
        TaskDTO updateTask = taskService.updateTask(id, task);
        return ResponseEntity.ok(updateTask);
    }
}
