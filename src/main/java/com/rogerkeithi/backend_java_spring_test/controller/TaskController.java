package com.rogerkeithi.backend_java_spring_test.controller;

import com.rogerkeithi.backend_java_spring_test.DTO.TaskDTO.CreateTaskDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.TaskDTO.TaskDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.TaskDTO.UpdateTaskDTO;
import com.rogerkeithi.backend_java_spring_test.services.interfaces.ITaskService;
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

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getUserTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort
    ) {
        List<TaskDTO> tasks = taskService.getSelfUserTasks(status, sort);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody CreateTaskDTO task) {
        TaskDTO createdTask = taskService.createTask(task);
        return ResponseEntity.ok(createdTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @RequestBody UpdateTaskDTO task) {
        TaskDTO updateTask = taskService.updateTask(id, task);
        return ResponseEntity.ok(updateTask);
    }
}
