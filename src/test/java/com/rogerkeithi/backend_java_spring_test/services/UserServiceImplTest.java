package com.rogerkeithi.backend_java_spring_test.services;

import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.UserCreateDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.UserUpdateDTO;
import com.rogerkeithi.backend_java_spring_test.model.User;
import com.rogerkeithi.backend_java_spring_test.repositories.UserRepository;
import com.rogerkeithi.backend_java_spring_test.utils.exceptions.BadRequestException;
import com.rogerkeithi.backend_java_spring_test.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllUsers_Success() {
        List<User> users = List.of(new User(), new User());

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void createUser_Success() {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setUsername("testuser");
        dto.setNivel("admin");

        when(userRepository.findByUsername("testuser")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

        User createdUser = userService.createUser(dto);

        assertNotNull(createdUser);
        assertEquals("testuser", createdUser.getUsername());
        assertEquals("admin", createdUser.getNivel());
    }

    @Test
    void createUser_shouldThrowBadRequestException_whenUsernameIsNull() {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setUsername(null);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.createUser(userCreateDTO);
        });

        assertEquals("Username is required", exception.getMessage());
    }

    @Test
    void createUser_shouldThrowBadRequestException_whenNivelIsNull() {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setUsername("testuser");
        userCreateDTO.setNivel(null);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.createUser(userCreateDTO);
        });

        assertEquals("Nivel is required", exception.getMessage());
    }

    @Test
    void createUser_shouldThrowBadRequestException_UsernameAlreadyExists() {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setUsername("testuser");
        userCreateDTO.setNivel("admin");

        User existingUser = new User();
        existingUser.setUsername("testuser");

        when(userRepository.findByUsername(userCreateDTO.getUsername())).thenReturn(existingUser);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.createUser(userCreateDTO));

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void updateUser_Success() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("olduser");
        existingUser.setNivel("user");

        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setUsername("newuser");
        updateDTO.setNivel("admin");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

        User updatedUser = userService.updateUser(1L, updateDTO);

        assertNotNull(updatedUser);
        assertEquals("newuser", updatedUser.getUsername());
        assertEquals("admin", updatedUser.getNivel());
    }

    @Test
    void updateUser_shouldThrowNotFoundException_UserNotFound() {
        Long userId = 1L;
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.updateUser(userId, userUpdateDTO));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void deleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_shouldThrowNotFoundException_UserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).deleteById(anyLong());
    }

}