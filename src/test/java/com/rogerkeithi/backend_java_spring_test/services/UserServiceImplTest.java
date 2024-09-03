package com.rogerkeithi.backend_java_spring_test.services;

import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.CreateUserDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.UserDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.UpdateUserDTO;
import com.rogerkeithi.backend_java_spring_test.model.User;
import com.rogerkeithi.backend_java_spring_test.repositories.UserRepository;
import com.rogerkeithi.backend_java_spring_test.utils.PasswordEncryptionUtil;
import com.rogerkeithi.backend_java_spring_test.utils.ValidationUtil;
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

    @Mock
    private ValidationUtil validationUtil;

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

        List<UserDTO> expectedDtos = users.stream()
                .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getNivel()))
                .collect(Collectors.toList());

        List<UserDTO> actualDtos = userService.getAllUsers();

        assertEquals(expectedDtos, actualDtos);
    }

    @Test
    public void createUser_Success() {
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("testuser");
        createUserDTO.setNivel("admin");
        createUserDTO.setPassword("decryptedPassword");
        String encryptedPassword = "encryptedPassword";

        when(passwordEncryptionUtil.encryptPassword(createUserDTO.getPassword())).thenReturn(encryptedPassword);

        doNothing().when(validationUtil).requireStringNonEmpty(createUserDTO.getUsername(), "Username is required");
        doNothing().when(validationUtil).requireStringNonEmpty(createUserDTO.getNivel(), "Nivel is required");
        doNothing().when(validationUtil).requireStringNonEmpty(encryptedPassword, "Password is required");

        when(userRepository.findByUsername(createUserDTO.getUsername())).thenReturn(null);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

        UserDTO createdUser = userService.createUser(createUserDTO);

        assertNotNull(createdUser);
        assertEquals("testuser", createdUser.getUsername());
        assertEquals("admin", createdUser.getNivel());
        verify(passwordEncryptionUtil).encryptPassword(createUserDTO.getPassword());
        verify(validationUtil).requireStringNonEmpty(createUserDTO.getUsername(), "Username is required");
        verify(validationUtil).requireStringNonEmpty(createUserDTO.getNivel(), "Nivel is required");
        verify(validationUtil).requireStringNonEmpty(encryptedPassword, "Password is required");
        verify(userRepository).findByUsername(createUserDTO.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowBadRequestException_whenUsernameIsNullOrEmpty() {
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername(null);
        createUserDTO.setNivel("admin");
        createUserDTO.setPassword("password");

        doThrow(new BadRequestException("Username is required"))
                .when(validationUtil).requireStringNonEmpty(createUserDTO.getUsername(), "Username is required");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.createUser(createUserDTO));
        assertEquals("Username is required", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowBadRequestException_whenNivelIsNullOrEmpty() {
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("testuser");
        createUserDTO.setNivel(null);
        createUserDTO.setPassword("password");

        doThrow(new BadRequestException("Nivel is required"))
                .when(validationUtil).requireStringNonEmpty(createUserDTO.getNivel(), "Nivel is required");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.createUser(createUserDTO));
        assertEquals("Nivel is required", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowBadRequestException_whenPasswordIsNullOrEmpty() {
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("testuser");
        createUserDTO.setNivel("admin");
        createUserDTO.setPassword(null);

        doThrow(new BadRequestException("Password is required"))
                .when(validationUtil).requireStringNonEmpty(createUserDTO.getPassword(), "Password is required");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.createUser(createUserDTO));
        assertEquals("Password is required", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowBadRequestException_whenUsernameAlreadyExists() {
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("testuser");
        createUserDTO.setNivel("admin");
        createUserDTO.setPassword("password");

        User existingUser = new User();
        existingUser.setUsername("testuser");

        when(userRepository.findByUsername(createUserDTO.getUsername())).thenReturn(existingUser);
        when(passwordEncryptionUtil.encryptPassword(createUserDTO.getPassword())).thenReturn("encryptedPassword");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.createUser(createUserDTO));
        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void updateUser_Success() {
        Long userId = 1L;
        User existingUser = new User(userId, "old_username", "old_nivel", "old_password");
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setUsername("new_username");
        updateUserDTO.setNivel("new_nivel");
        updateUserDTO.setPassword("new_password");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncryptionUtil.encryptPassword("new_password")).thenReturn("encrypted_password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO updatedUser = userService.updateUser(userId, updateUserDTO);

        assertEquals("new_username", updatedUser.getUsername());
        assertEquals("new_nivel", updatedUser.getNivel());

        verify(userRepository).findById(userId);
        verify(passwordEncryptionUtil).encryptPassword("new_password");
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUser_shouldThrowNotFoundException_UserNotFound() {
        Long userId = 1L;
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.updateUser(userId, updateUserDTO));

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