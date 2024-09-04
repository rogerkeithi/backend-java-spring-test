package com.rogerkeithi.backend_java_spring_test.services;

import com.rogerkeithi.backend_java_spring_test.DTO.TaskDTO.CreateTaskDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.TaskDTO.TaskDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.TaskDTO.UpdateTaskDTO;
import com.rogerkeithi.backend_java_spring_test.model.Task;
import com.rogerkeithi.backend_java_spring_test.model.User;
import com.rogerkeithi.backend_java_spring_test.repositories.TaskRepository;
import com.rogerkeithi.backend_java_spring_test.repositories.UserRepository;
import com.rogerkeithi.backend_java_spring_test.utils.SecurityUtil;
import com.rogerkeithi.backend_java_spring_test.utils.ValidationUtil;
import com.rogerkeithi.backend_java_spring_test.utils.enums.TaskStatus;
import com.rogerkeithi.backend_java_spring_test.utils.enums.UserNivel;
import com.rogerkeithi.backend_java_spring_test.utils.exceptions.BadRequestException;
import com.rogerkeithi.backend_java_spring_test.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private ValidationUtil validationUtil;

    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSelfUserTasks_Success() {
        String username = "testUser";
        String sort = "dueDate";
        String status = "CONCLUIDA";
        User mockedUser = new User(1L, "testuser1", UserNivel.ADMIN, "123");
        LocalDateTime mockedDate = LocalDateTime.now();

        when(securityUtil.getCurrentUsername()).thenReturn(username);

        List<Task> tasks = List.of(
                new Task(1L, "Task 1", "Description 1", mockedDate, mockedDate, TaskStatus.CONCLUIDA,  mockedUser),
                new Task(2L, "Task 2", "Description 2", mockedDate, mockedDate, TaskStatus.PENDENTE, mockedUser),
                new Task(3L, "Task 3", "Description 3", mockedDate, mockedDate, TaskStatus.EM_ANDAMENTO, mockedUser)
        );
        when(taskRepository.findAllByUsername(username)).thenReturn(tasks);

        doNothing().when(validationUtil).requireValidEnum(TaskStatus.class, status, "Invalid status value");

        List<TaskDTO> expected = tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.valueOf(status))
                .sorted(Comparator.comparing(Task::getDueDate))
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

        List<TaskDTO> result = taskService.getSelfUserTasks(status, sort);

        assertEquals(expected, result);
    }

    @Test
    void testGetSelfUserTasks_shouldThrowBadRequestException_InvalidStatus() {
        String username = "testUser";
        String invalidStatus = "STATUS_INVALIDO";
        String sort = "dueDate";

        when(securityUtil.getCurrentUsername()).thenReturn(username);

        doThrow(new BadRequestException("Invalid status value"))
                .when(validationUtil)
                .requireValidEnum(TaskStatus.class, invalidStatus, "Invalid status value");

        BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
            taskService.getSelfUserTasks(invalidStatus, sort);
        });

        assertEquals("Invalid status value", thrown.getMessage());
    }

    @Test
    void testGetSelfUserTasks_shouldThrowBadRequestException_InvalidSortType() {
        String username = "testUser";
        String status = "CONCLUIDA";
        String sort = "invalidSortType";

        when(securityUtil.getCurrentUsername()).thenReturn(username);

        doNothing().when(validationUtil).requireValidEnum(TaskStatus.class, status, "Invalid status value");

        BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
            taskService.getSelfUserTasks(status, sort);
        });

        assertEquals("Invalid sort type", thrown.getMessage());
    }

    @Test
    void testGetAllUserTasks_Success() {
        Long userId = 1L;
        LocalDateTime mockedDate = LocalDateTime.now();
        User user = new User();
        user.setNivel(UserNivel.USER);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        List<Task> tasks = List.of(
                new Task(1L, "Task 1", "Description 1", mockedDate, mockedDate, TaskStatus.CONCLUIDA,  user),
                new Task(2L, "Task 2", "Description 2", mockedDate, mockedDate, TaskStatus.PENDENTE, user)
        );
        when(taskRepository.findByUser_Id(userId)).thenReturn(tasks);
        List<TaskDTO> expected = tasks.stream()
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

        List<TaskDTO> result = taskService.getAllUserTasks(userId);
        assertEquals(expected, result);
    }

    @Test
    void testGetAllUserTasks_shouldThrowNotFoundException_UserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            taskService.getAllUserTasks(userId);
        });

        assertEquals("User not found", thrown.getMessage());
    }

    @Test
    void testDeleteTask_Success() {
        Long taskId = 1L;
        Task task = new Task();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        taskService.deleteTask(taskId);

        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    void testDeleteTask_shouldThrowNotFoundException_TaskNotFound(){
        Long taskId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            taskService.deleteTask(taskId);
        });

        assertEquals("Task not found", thrown.getMessage());
        verify(taskRepository, never()).deleteById(taskId);
    }

    @Test
    void testCreateTask_Success() {
        CreateTaskDTO createTaskDTO = new CreateTaskDTO();
        createTaskDTO.setTitle("New Task");
        createTaskDTO.setDescription("Task Description");
        createTaskDTO.setStatus("CONCLUIDA");
        createTaskDTO.setUser_id(1L);
        createTaskDTO.setDueDate(LocalDateTime.of(2024, 1, 1, 12, 0));

        User user = new User();
        user.setNivel(UserNivel.USER);
        user.setId(1L);

        doNothing().when(validationUtil).requireStringNonEmpty(createTaskDTO.getTitle(), "Title is required");
        doNothing().when(validationUtil).requireStringNonEmpty(createTaskDTO.getDescription(), "Description is required");
        doNothing().when(validationUtil).requireValidEnum(TaskStatus.class, createTaskDTO.getStatus(), "Invalid status value");

        when(userRepository.findById(createTaskDTO.getUser_id())).thenReturn(Optional.of(user));

        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTitle(createTaskDTO.getTitle());
        savedTask.setDescription(createTaskDTO.getDescription());
        savedTask.setStatus(TaskStatus.CONCLUIDA);
        savedTask.setDueDate(createTaskDTO.getDueDate());
        savedTask.setCreatedAt(LocalDateTime.now());
        savedTask.set_user(user);

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        TaskDTO result = taskService.createTask(createTaskDTO);

        assertNotNull(result);
        assertEquals("New Task", result.getTitle());
        assertEquals("Task Description", result.getDescription());
        assertEquals(TaskStatus.CONCLUIDA, result.getStatus());
        assertEquals(createTaskDTO.getDueDate(), result.getDueDate());

        verify(validationUtil, times(1)).requireStringNonEmpty(createTaskDTO.getTitle(), "Title is required");
        verify(validationUtil, times(1)).requireStringNonEmpty(createTaskDTO.getDescription(), "Description is required");
        verify(validationUtil, times(1)).requireValidEnum(TaskStatus.class, createTaskDTO.getStatus(), "Invalid status value");
        verify(userRepository, times(1)).findById(createTaskDTO.getUser_id());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCreateTask_shouldThrowNotFoundException_UserNotFound() {
        CreateTaskDTO createTaskDTO = new CreateTaskDTO();
        createTaskDTO.setStatus("PENDENTE");
        createTaskDTO.setUser_id(1L);

        doNothing().when(validationUtil).requireStringNonEmpty(anyString(), anyString());
        doNothing().when(validationUtil).requireValidEnum(any(Class.class), anyString(), anyString());

        when(userRepository.findById(createTaskDTO.getUser_id())).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            taskService.createTask(createTaskDTO);
        });

        assertEquals("User not found", thrown.getMessage());

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testCreateTask_shouldThrowBadRequestException_TitleIsEmpty() {
        CreateTaskDTO createTaskDTO = new CreateTaskDTO();
        createTaskDTO.setStatus("PENDENTE");
        createTaskDTO.setTitle("");

        doThrow(new BadRequestException("Title is required"))
                .when(validationUtil).requireStringNonEmpty(createTaskDTO.getTitle(), "Title is required");

        BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
            taskService.createTask(createTaskDTO);
        });

        assertEquals("Title is required", thrown.getMessage());

        verify(userRepository, never()).findById(anyLong());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testCreateTask_shouldThrowBadRequestException_DescriptionIsEmpty() {
        CreateTaskDTO createTaskDTO = new CreateTaskDTO();
        createTaskDTO.setStatus("PENDENTE");
        createTaskDTO.setDescription("");

        doThrow(new BadRequestException("Description is required"))
                .when(validationUtil).requireStringNonEmpty(createTaskDTO.getDescription(), "Description is required");

        BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
            taskService.createTask(createTaskDTO);
        });

        assertEquals("Description is required", thrown.getMessage());

        verify(userRepository, never()).findById(anyLong());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testCreateTask_shouldThrowBadRequestException_InvalidStatus() {
        CreateTaskDTO createTaskDTO = new CreateTaskDTO();
        createTaskDTO.setStatus("INVALID_STATUS");

        doThrow(new BadRequestException("Invalid status value"))
                .when(validationUtil).requireValidEnum(TaskStatus.class, createTaskDTO.getStatus(), "Invalid status value");

        BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
            taskService.createTask(createTaskDTO);
        });

        assertEquals("Invalid status value", thrown.getMessage());

        verify(userRepository, never()).findById(anyLong());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testUpdateTask_Success() {
        Long taskId = 1L;
        UpdateTaskDTO updateTaskDTO = new UpdateTaskDTO();
        updateTaskDTO.setTitle("Updated Title");
        updateTaskDTO.setDescription("Updated Description");
        updateTaskDTO.setStatus("CONCLUIDA");
        updateTaskDTO.setUser_id(2L);

        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");
        existingTask.setStatus(TaskStatus.EM_ANDAMENTO);

        User existingUser = new User();
        existingUser.setNivel(UserNivel.USER);
        existingUser.setId(2L);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        doNothing().when(validationUtil).requireValidEnum(TaskStatus.class, updateTaskDTO.getStatus(), "Invalid status value");
        when(userRepository.findById(updateTaskDTO.getUser_id())).thenReturn(Optional.of(existingUser));
        when(taskRepository.save(existingTask)).thenReturn(existingTask);

        TaskDTO result = taskService.updateTask(taskId, updateTaskDTO);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(TaskStatus.CONCLUIDA, result.getStatus());

        verify(taskRepository, times(1)).findById(taskId);
        verify(validationUtil, times(1)).requireValidEnum(TaskStatus.class, updateTaskDTO.getStatus(), "Invalid status value");
        verify(userRepository, times(1)).findById(updateTaskDTO.getUser_id());
        verify(taskRepository, times(1)).save(existingTask);
    }

    @Test
    void testUpdateTask_shouldThrowNotFoundException_TaskNotFound() {
        Long taskId = 1L;
        UpdateTaskDTO updateTaskDTO = new UpdateTaskDTO();
        updateTaskDTO.setStatus("PENDENTE");

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            taskService.updateTask(taskId, updateTaskDTO);
        });

        assertEquals("Task not found", thrown.getMessage());

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testUpdateTask_shouldThrowNotFoundException_UserNotFound() {
        Long taskId = 1L;
        UpdateTaskDTO updateTaskDTO = new UpdateTaskDTO();
        updateTaskDTO.setUser_id(2L);
        updateTaskDTO.setStatus("PENDENTE");

        Task existingTask = new Task();
        existingTask.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(userRepository.findById(updateTaskDTO.getUser_id())).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            taskService.updateTask(taskId, updateTaskDTO);
        });

        assertEquals("User not found", thrown.getMessage());

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testUpdateTask_shouldThrowBadRequestException_InvalidStatus() {
        Long taskId = 1L;
        UpdateTaskDTO updateTaskDTO = new UpdateTaskDTO();
        updateTaskDTO.setStatus("INVALID_STATUS");

        Task existingTask = new Task();
        existingTask.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        doThrow(new BadRequestException("Invalid status value"))
                .when(validationUtil).requireValidEnum(TaskStatus.class, updateTaskDTO.getStatus(), "Invalid status value");

        BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
            taskService.updateTask(taskId, updateTaskDTO);
        });

        assertEquals("Invalid status value", thrown.getMessage());

        verify(taskRepository, never()).save(any(Task.class));
    }
}
