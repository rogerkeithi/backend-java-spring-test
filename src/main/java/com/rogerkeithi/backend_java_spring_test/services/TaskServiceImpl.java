package com.rogerkeithi.backend_java_spring_test.services;

import com.rogerkeithi.backend_java_spring_test.DTO.TaskDTO.TaskDTO;
import com.rogerkeithi.backend_java_spring_test.model.Task;
import com.rogerkeithi.backend_java_spring_test.model.User;
import com.rogerkeithi.backend_java_spring_test.repositories.TaskRepository;
import com.rogerkeithi.backend_java_spring_test.services.interfaces.ITaskService;
import com.rogerkeithi.backend_java_spring_test.utils.EnumUtil;
import com.rogerkeithi.backend_java_spring_test.utils.SecurityUtil;
import com.rogerkeithi.backend_java_spring_test.utils.enums.TaskStatus;
import com.rogerkeithi.backend_java_spring_test.utils.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TaskServiceImpl implements ITaskService {
    private final TaskRepository taskRepository;
    private final SecurityUtil securityUtil;
    private final EnumUtil enumUtil;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, SecurityUtil securityUtil, EnumUtil enumUtil) {
        this.taskRepository = taskRepository;
        this.securityUtil = securityUtil;
        this.enumUtil = enumUtil;
    }

    @Override
    public List<TaskDTO> getUserTasks(TaskStatus status, String sort) {
        String username = securityUtil.getCurrentUsername();
        List<Task> tasks = taskRepository.findAllByUsername(username);

        Stream<Task> taskStream = tasks.stream();

        if (status != null) {
            if (!enumUtil.isValidEnum(TaskStatus.class, status.name())) {
                throw new BadRequestException("Invalid status value");
            }
            taskStream = taskStream.filter(task -> task.getStatus() == status);
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
}