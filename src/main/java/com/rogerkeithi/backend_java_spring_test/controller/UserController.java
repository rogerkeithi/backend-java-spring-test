package com.rogerkeithi.backend_java_spring_test.controller;

import com.rogerkeithi.backend_java_spring_test.DTO.TaskDTO.TaskDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.CreateUserDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.UserDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.UpdateUserDTO;
import com.rogerkeithi.backend_java_spring_test.services.TaskServiceImpl;
import com.rogerkeithi.backend_java_spring_test.services.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserServiceImpl userService;
    private final TaskServiceImpl taskService;

    @Autowired
    public UserController(UserServiceImpl userService, TaskServiceImpl taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }

    @Operation(
            summary = "Get all users",
            description = "Get all users with their data."
    )
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Create user",
            description = "Create a new user <br><br>" +
                    "nivel can only have 2 values: <br> <b>ADMIN or USER </b>"
    )
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserDTO user) {
        UserDTO createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @Operation(
            summary = "Update user",
            description = "Update user informations using her id as a parameter <br><br>" +
                    "nivel can only have 2 values: <br> <b>ADMIN or USER </b>"
    )
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO user) {
        UserDTO updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(
            summary = "Delete user",
            description = "Delete user using her id as a parameter"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get user tasks",
            description = "Get all tasks related to an user based on user id"
    )
    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<TaskDTO>> getUserTasks(@PathVariable Long id) {
        List<TaskDTO> tasks = taskService.getAllUserTasks(id);
        return ResponseEntity.ok(tasks);
    }

}