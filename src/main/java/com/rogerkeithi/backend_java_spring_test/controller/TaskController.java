package com.rogerkeithi.backend_java_spring_test.controller;

import com.rogerkeithi.backend_java_spring_test.DTO.TaskDTO.TaskDTO;
import com.rogerkeithi.backend_java_spring_test.model.Task;
import com.rogerkeithi.backend_java_spring_test.services.interfaces.ITaskService;
import com.rogerkeithi.backend_java_spring_test.utils.enums.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) String sort
    ) {
        List<TaskDTO> tasks = taskService.getUserTasks(status, sort);
        return ResponseEntity.ok(tasks);
    }
}
