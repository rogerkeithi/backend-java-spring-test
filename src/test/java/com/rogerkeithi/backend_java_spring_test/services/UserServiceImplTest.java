package com.rogerkeithi.backend_java_spring_test.services;

import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.CreateUserDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.UserDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.UpdateUserDTO;
import com.rogerkeithi.backend_java_spring_test.model.User;
import com.rogerkeithi.backend_java_spring_test.repositories.UserRepository;
import com.rogerkeithi.backend_java_spring_test.utils.PasswordEncryptionUtil;
import com.rogerkeithi.backend_java_spring_test.utils.ValidationUtil;
import com.rogerkeithi.backend_java_spring_test.utils.enums.UserNivel;
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
    void testGetAllUsers_Success() {
        User user1 = new User(1L, "testuser1", UserNivel.ADMIN, "123");
        User user2 = new User(2L, "testuser2", UserNivel.USER, "456");
        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        List<UserDTO> expectedDtos = users.stream()
                .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getNivel().name()))
                .collect(Collectors.toList());

        List<UserDTO> actualDtos = userService.getAllUsers();

        assertEquals(expectedDtos, actualDtos);
    }

    @Test
    void testCreateUser_Success() {
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("testuser");
        createUserDTO.setNivel("ADMIN");
        createUserDTO.setPassword("decryptedPassword");
        String encryptedPassword = "encryptedPassword";

        when(passwordEncryptionUtil.encryptPassword(createUserDTO.getPassword())).thenReturn(encryptedPassword);

        doNothing().when(validationUtil).requireStringNonEmpty(createUserDTO.getUsername(), "Username is required");
        doNothing().when(validationUtil).requireValidEnum(UserNivel.class, createUserDTO.getNivel(), "Invalid Nivel value");
        doNothing().when(validationUtil).requireStringNonEmpty(encryptedPassword, "Password is required");

        when(userRepository.findByUsername(createUserDTO.getUsername())).thenReturn(null);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

        UserDTO createdUser = userService.createUser(createUserDTO);

        assertNotNull(createdUser);
        assertEquals("testuser", createdUser.getUsername());
        assertEquals(UserNivel.ADMIN.name(), createdUser.getNivel());

        verify(passwordEncryptionUtil).encryptPassword(createUserDTO.getPassword());
        verify(validationUtil).requireStringNonEmpty(createUserDTO.getUsername(), "Username is required");
        verify(validationUtil).requireValidEnum(UserNivel.class, createUserDTO.getNivel(), "Invalid Nivel value");
        verify(validationUtil).requireStringNonEmpty(encryptedPassword, "Password is required");
        verify(userRepository).findByUsername(createUserDTO.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUser_shouldThrowBadRequestException_whenUsernameIsNullOrEmpty() {
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername(null);
        createUserDTO.setNivel("ADMIN");
        createUserDTO.setPassword("password");

        doThrow(new BadRequestException("Username is required"))
                .when(validationUtil).requireStringNonEmpty(createUserDTO.getUsername(), "Username is required");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.createUser(createUserDTO));
        assertEquals("Username is required", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateUser_shouldThrowBadRequestException_whenNivelIsInvalid() {
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("testuser");
        createUserDTO.setNivel("NOT_VALID");
        createUserDTO.setPassword("password");

        doThrow(new BadRequestException("Invalid Nivel value"))
                .when(validationUtil).requireValidEnum(UserNivel.class, createUserDTO.getNivel(), "Invalid Nivel value");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.createUser(createUserDTO));
        assertEquals("Invalid Nivel value", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateUser_shouldThrowBadRequestException_whenPasswordIsNullOrEmpty() {
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("testuser");
        createUserDTO.setNivel("ADMIN");
        createUserDTO.setPassword(null);

        doThrow(new BadRequestException("Password is required"))
                .when(validationUtil).requireStringNonEmpty(createUserDTO.getPassword(), "Password is required");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.createUser(createUserDTO));
        assertEquals("Password is required", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateUser_shouldThrowBadRequestException_whenUsernameAlreadyExists() {
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("testuser");
        createUserDTO.setNivel("ADMIN");
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
    void testUpdateUser_Success() {
        Long userId = 1L;
        User existingUser = new User(userId, "old_username", UserNivel.ADMIN, "old_password");
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setUsername("new_username");
        updateUserDTO.setNivel("USER");
        updateUserDTO.setPassword("new_password");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncryptionUtil.encryptPassword("new_password")).thenReturn("encrypted_password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO updatedUser = userService.updateUser(userId, updateUserDTO);

        assertEquals("new_username", updatedUser.getUsername());
        assertEquals(UserNivel.USER.name(), updatedUser.getNivel());

        verify(userRepository).findById(userId);
        verify(passwordEncryptionUtil).encryptPassword("new_password");
        verify(userRepository).save(existingUser);
    }

    @Test
    void testUpdateUser_shouldThrowNotFoundException_UserNotFound() {
        Long userId = 1L;
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setNivel("ADMIN");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.updateUser(userId, updateUserDTO));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUser_shouldThrowNotFoundException_UserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).deleteById(anyLong());
    }

}