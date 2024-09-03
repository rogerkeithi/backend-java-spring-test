package com.rogerkeithi.backend_java_spring_test.services.interfaces;

import com.rogerkeithi.backend_java_spring_test.DTO.TaskDTO.CreateTaskDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.TaskDTO.TaskDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.TaskDTO.UpdateTaskDTO;
import com.rogerkeithi.backend_java_spring_test.utils.enums.TaskStatus;

import java.util.List;

public interface ITaskService {
    List<TaskDTO> getSelfUserTasks(TaskStatus status, String sort);
    List<TaskDTO> getAllUserTasks(Long userId);
    void deleteTask(Long id);
    TaskDTO createTask(CreateTaskDTO createTaskDTO);
    TaskDTO updateTask(Long id, UpdateTaskDTO updateTaskDTO);
}
