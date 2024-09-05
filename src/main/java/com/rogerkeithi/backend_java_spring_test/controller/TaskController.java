package com.rogerkeithi.backend_java_spring_test.controller;

import com.rogerkeithi.backend_java_spring_test.DTO.TaskDTO.CreateTaskDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.TaskDTO.TaskDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.TaskDTO.UpdateTaskDTO;
import com.rogerkeithi.backend_java_spring_test.services.TaskServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://127.0.0.1:3000", maxAge = 3600)
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskServiceImpl taskService;

    @Autowired
    public TaskController(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }

    @Operation(
            summary = "Get user authenticated tasks",
            description = "Get all tasks from the user authenticated <br><br>" +
                    " status can only have 3 types: <br> <b>PENDENTE, EM_ANDAMENTO and CONCLUIDA </b> " +
                    "<br><br> sort can only have <b>dueDate</b> as a value"
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
            description = "Create a new task <br><br> status can only have 3 types:" +
                    "<br><b>PENDENTE, EM_ANDAMENTO and CONCLUIDA </b>"
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
            description = "Update a task using her id as a parameter  <br><br>" +
                    "status can only have 3 types: <br> <b>PENDENTE, EM_ANDAMENTO and CONCLUIDA </b>"
    )
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @RequestBody UpdateTaskDTO task) {
        TaskDTO updateTask = taskService.updateTask(id, task);
        return ResponseEntity.ok(updateTask);
    }
}
