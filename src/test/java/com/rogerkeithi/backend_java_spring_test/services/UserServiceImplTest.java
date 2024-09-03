package com.rogerkeithi.backend_java_spring_test.services;

import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.UserCreateDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.UserGetAllDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.UserUpdateDTO;
import com.rogerkeithi.backend_java_spring_test.model.User;
import com.rogerkeithi.backend_java_spring_test.repositories.UserRepository;
import com.rogerkeithi.backend_java_spring_test.utils.PasswordEncryptionUtil;
import com.rogerkeithi.backend_java_spring_test.utils.exceptions.BadRequestException;
import com.rogerkeithi.backend_java_spring_test.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncryptionUtil passwordEncryptionUtil;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllUsers_Success() {
        User user1 = new User(1L, "testuser1", "admin", "123");
        User user2 = new User(2L, "testuser2", "user", "456");
        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        List<UserGetAllDTO> expectedDtos = users.stream()
                .map(user -> new UserGetAllDTO(user.getId(), user.getUsername(), user.getNivel()))
                .collect(Collectors.toList());

        List<UserGetAllDTO> actualDtos = userService.getAllUsers();

        assertEquals(expectedDtos, actualDtos);
    }

    @Test
    public void createUser_Success() {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setUsername("testuser");
        userCreateDTO.setNivel("admin");
        userCreateDTO.setPassword("decryptedPassword");
        String encryptedPassword = "encryptedPassword";

        when(passwordEncryptionUtil.encryptPassword(userCreateDTO.getPassword())).thenReturn(encryptedPassword);
        when(userRepository.findByUsername("testuser")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

        User createdUser = userService.createUser(userCreateDTO);

        assertNotNull(createdUser);
        assertEquals("testuser", createdUser.getUsername());
        assertEquals("admin", createdUser.getNivel());
        assertEquals(encryptedPassword, createdUser.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowBadRequestException_whenUsernameIsNullOrEmpty() {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setUsername(null);
        userCreateDTO.setNivel("admin");
        userCreateDTO.setPassword("password");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.createUser(userCreateDTO));
        assertEquals("Username is required", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowBadRequestException_whenNivelIsNullOrEmpty() {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setUsername("testuser");
        userCreateDTO.setNivel(null);
        userCreateDTO.setPassword("password");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.createUser(userCreateDTO));
        assertEquals("Nivel is required", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowBadRequestException_whenPasswordIsNullOrEmpty() {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setUsername("testuser");
        userCreateDTO.setNivel("admin");
        userCreateDTO.setPassword(null);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.createUser(userCreateDTO));
        assertEquals("Password is required", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowBadRequestException_whenUsernameAlreadyExists() {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setUsername("testuser");
        userCreateDTO.setNivel("admin");
        userCreateDTO.setPassword("password");

        User existingUser = new User();
        existingUser.setUsername("testuser");

        when(userRepository.findByUsername(userCreateDTO.getUsername())).thenReturn(existingUser);
        when(passwordEncryptionUtil.encryptPassword("password")).thenReturn("encryptedPassword");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.createUser(userCreateDTO));
        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void updateUser_Success() {
        Long userId = 1L;
        User existingUser = new User(userId, "old_username", "old_nivel", "old_password");
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setUsername("new_username");
        userUpdateDTO.setNivel("new_nivel");
        userUpdateDTO.setPassword("new_password");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncryptionUtil.encryptPassword("new_password")).thenReturn("encrypted_password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userService.updateUser(userId, userUpdateDTO);

        assertEquals("new_username", updatedUser.getUsername());
        assertEquals("new_nivel", updatedUser.getNivel());
        assertEquals("encrypted_password", updatedUser.getPassword());

        verify(userRepository).findById(userId);
        verify(passwordEncryptionUtil).encryptPassword("new_password");
        verify(userRepository).save(existingUser);
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