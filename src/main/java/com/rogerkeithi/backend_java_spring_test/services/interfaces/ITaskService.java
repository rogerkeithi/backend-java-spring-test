package com.rogerkeithi.backend_java_spring_test.services.interfaces;

import com.rogerkeithi.backend_java_spring_test.DTO.TaskDTO.TaskDTO;
import com.rogerkeithi.backend_java_spring_test.utils.enums.TaskStatus;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ITaskService {
    List<TaskDTO> getUserTasks(TaskStatus status, String sort);
    /*Task createTask(CreateTaskDTO createTaskDTO);
    Task updateTask(Long id, UpdateTaskDTO updateTaskDTO);
    void deleteTask(Long id);
    List<Task> getAllUserTasks(Long userId);*/
}
