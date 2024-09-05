package com.rogerkeithi.backend_java_spring_test.services;

import com.rogerkeithi.backend_java_spring_test.DTO.AuthDTO.AuthDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.AuthDTO.LoginAuthDTO;
import com.rogerkeithi.backend_java_spring_test.model.User;
import com.rogerkeithi.backend_java_spring_test.repositories.UserRepository;
import com.rogerkeithi.backend_java_spring_test.utils.JWTUtil;
import com.rogerkeithi.backend_java_spring_test.utils.PasswordEncryptionUtil;
import com.rogerkeithi.backend_java_spring_test.utils.enums.UserNivel;
import com.rogerkeithi.backend_java_spring_test.utils.exceptions.BadRequestException;
import com.rogerkeithi.backend_java_spring_test.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private PasswordEncryptionUtil passwordEncryptionUtil;

    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPassword("encryptedPassword");
        mockUser.setNivel(UserNivel.USER);
    }

    @Test
    void testLoginSuccess() {
        when(userRepository.findByUsername("testuser")).thenReturn(mockUser);
        when(passwordEncryptionUtil.matchesPassword("plainPassword", "encryptedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("1", "testuser", "USER")).thenReturn("mockToken");

        LoginAuthDTO loginAuthDTO = new LoginAuthDTO();
        loginAuthDTO.setUsername("testuser");
        loginAuthDTO.setPassword("plainPassword");

        AuthDTO authDTO = authServiceImpl.login(loginAuthDTO);

        assertEquals("mockToken", authDTO.getToken());

        verify(userRepository).findByUsername("testuser");
        verify(passwordEncryptionUtil).matchesPassword("plainPassword", "encryptedPassword");
        verify(jwtUtil).generateToken("1", "testuser", "USER");
    }

    @Test
    void testLoginUserNotFound() {
        when(userRepository.findByUsername("invalidUser")).thenReturn(null);

        LoginAuthDTO loginAuthDTO = new LoginAuthDTO();
        loginAuthDTO.setUsername("invalidUser");
        loginAuthDTO.setPassword("anyPassword");

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            authServiceImpl.login(loginAuthDTO);
        });

        assertEquals("User not found", thrown.getMessage());

        verify(userRepository).findByUsername("invalidUser");
        verify(passwordEncryptionUtil, never()).matchesPassword(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyString(), anyString());
    }

    @Test
    void testLoginInvalidPassword() {
        when(userRepository.findByUsername("testuser")).thenReturn(mockUser);
        when(passwordEncryptionUtil.matchesPassword("wrongPassword", "encryptedPassword")).thenReturn(false);

        LoginAuthDTO loginAuthDTO = new LoginAuthDTO();
        loginAuthDTO.setUsername("testuser");
        loginAuthDTO.setPassword("wrongPassword");

        BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
            authServiceImpl.login(loginAuthDTO);
        });

        assertEquals("Invalid username or password", thrown.getMessage());

        verify(userRepository).findByUsername("testuser");
        verify(passwordEncryptionUtil).matchesPassword("wrongPassword", "encryptedPassword");
        verify(jwtUtil, never()).generateToken(anyString(), anyString(), anyString());
    }
}
