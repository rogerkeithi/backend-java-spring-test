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
    void createUser_ThrowsBadRequestException_UsernameAlreadyExists() {
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
    void updateUser_ThrowsNotFoundException_UserNotFound() {
        Long userId = 1L;
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.updateUser(userId, userUpdateDTO));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_ThrowsNotFoundException_UserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).deleteById(anyLong());
    }

}