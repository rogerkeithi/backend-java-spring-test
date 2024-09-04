package com.rogerkeithi.backend_java_spring_test.services;

import com.rogerkeithi.backend_java_spring_test.DTO.TaskDTO.CreateTaskDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.TaskDTO.TaskDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.TaskDTO.UpdateTaskDTO;
import com.rogerkeithi.backend_java_spring_test.model.Task;
import com.rogerkeithi.backend_java_spring_test.model.User;
import com.rogerkeithi.backend_java_spring_test.repositories.TaskRepository;
import com.rogerkeithi.backend_java_spring_test.repositories.UserRepository;
import com.rogerkeithi.backend_java_spring_test.services.interfaces.ITaskService;
import com.rogerkeithi.backend_java_spring_test.utils.SecurityUtil;
import com.rogerkeithi.backend_java_spring_test.utils.ValidationUtil;
import com.rogerkeithi.backend_java_spring_test.utils.enums.TaskStatus;
import com.rogerkeithi.backend_java_spring_test.utils.exceptions.BadRequestException;
import com.rogerkeithi.backend_java_spring_test.utils.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TaskServiceImpl implements ITaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final ValidationUtil validationUtil;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, SecurityUtil securityUtil, UserRepository userRepository, ValidationUtil validationUtil) {
        this.taskRepository = taskRepository;
        this.securityUtil = securityUtil;
        this.userRepository = userRepository;
        this.validationUtil = validationUtil;
    }

    @Override
    public List<TaskDTO> getSelfUserTasks(String status, String sort) {
        String username = securityUtil.getCurrentUsername();
        List<Task> tasks = taskRepository.findAllByUsername(username);

        Stream<Task> taskStream = tasks.stream();

        if (status != null) {
            validationUtil.requireValidEnum(TaskStatus.class, status,"Invalid status value");
            taskStream = taskStream.filter(task -> task.getStatus() == TaskStatus.valueOf(status));
        }

        if (sort != null && !sort.isEmpty() && !"dueDate".equalsIgnoreCase(sort)) {
            throw new BadRequestException("Invalid sort type");
        }

        if ("dueDate".equalsIgnoreCase(sort)) {
            taskStream = taskStream.sorted(Comparator.comparing(Task::getDueDate));
        }

        return taskStream
                .map(task -> new TaskDTO(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getStatus(),
                        task.getDueDate(),
                        task.getCreatedAt(),
                        User.toDTO(task.get_user())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getAllUserTasks(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Task> tasks = taskRepository.findByUser_Id(userId);
        Stream<Task> taskStream = tasks.stream();
        return taskStream
                .map(task -> new TaskDTO(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getStatus(),
                        task.getDueDate(),
                        task.getCreatedAt(),
                        User.toDTO(task.get_user())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTask(Long id) {
        Optional<Task> taskFound = taskRepository.findById(id);

        if (taskFound.isEmpty()) {
            throw new NotFoundException("Task not found");
        }

        taskRepository.deleteById(id);
    }

    @Override
    public TaskDTO createTask(CreateTaskDTO createTaskDTO){
        String taskStatus = createTaskDTO.getStatus().toUpperCase();
        LocalDateTime currentDateTime = LocalDateTime.now();

        validationUtil.requireStringNonEmpty(createTaskDTO.getTitle(), "Title is required");
        validationUtil.requireStringNonEmpty(createTaskDTO.getDescription(), "Description is required");
        validationUtil.requireValidEnum(TaskStatus.class, createTaskDTO.getStatus(),"Invalid status value");

        User userFound = userRepository.findById(createTaskDTO.getUser_id())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Task task = new Task();
        task.setTitle(createTaskDTO.getTitle());
        task.setDescription(createTaskDTO.getDescription());
        task.setDueDate(createTaskDTO.getDueDate());
        task.setCreatedAt(currentDateTime);
        task.setStatus(TaskStatus.valueOf(taskStatus));
        task.set_user(userFound);

        taskRepository.save(task);

        return Task.toDTO(task);
    }

    @Override
    public TaskDTO updateTask(Long id, UpdateTaskDTO updateTaskDTO) {
        String taskStatus = updateTaskDTO.getStatus().toUpperCase();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found"));

        if (updateTaskDTO.getTitle() != null && !updateTaskDTO.getTitle().trim().isEmpty()) {
            task.setTitle(updateTaskDTO.getTitle());
        }
        if (updateTaskDTO.getDescription() != null && !updateTaskDTO.getDescription().trim().isEmpty()) {
            task.setDescription(updateTaskDTO.getDescription());
        }
        if(updateTaskDTO.getDueDate() != null) {
            task.setDueDate(updateTaskDTO.getDueDate());
        }
        if(updateTaskDTO.getStatus() != null && !updateTaskDTO.getStatus().trim().isEmpty()) {
            validationUtil.requireValidEnum(TaskStatus.class, taskStatus,"Invalid status value");
            task.setStatus(TaskStatus.valueOf(taskStatus));
        }
        if(updateTaskDTO.getUser_id() != null && updateTaskDTO.getUser_id() > 0) {
            User userFound = userRepository.findById(updateTaskDTO.getUser_id())
                    .orElseThrow(() -> new NotFoundException("User not found"));
            task.set_user(userFound);
        }

        taskRepository.save(task);

        return Task.toDTO(task);
    }
}