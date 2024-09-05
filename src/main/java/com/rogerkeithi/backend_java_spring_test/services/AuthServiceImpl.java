package com.rogerkeithi.backend_java_spring_test.services;

import com.rogerkeithi.backend_java_spring_test.DTO.AuthDTO.AuthDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.AuthDTO.LoginAuthDTO;
import com.rogerkeithi.backend_java_spring_test.model.User;
import com.rogerkeithi.backend_java_spring_test.repositories.UserRepository;
import com.rogerkeithi.backend_java_spring_test.services.interfaces.IAuthService;
import com.rogerkeithi.backend_java_spring_test.utils.JWTUtil;
import com.rogerkeithi.backend_java_spring_test.utils.PasswordEncryptionUtil;
import com.rogerkeithi.backend_java_spring_test.utils.exceptions.BadRequestException;
import com.rogerkeithi.backend_java_spring_test.utils.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements IAuthService {
    @Value("${token.expires_in}")
    private Long expiresIn;

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final PasswordEncryptionUtil passwordEncryptionUtil;

    public AuthServiceImpl(UserRepository userRepository, JWTUtil jwtUtil, PasswordEncryptionUtil passwordEncryptionUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncryptionUtil = passwordEncryptionUtil;
    }

    @Override
    public AuthDTO login(LoginAuthDTO loginAuthDTO) {
        User userFound = userRepository.findByUsername(loginAuthDTO.getUsername());

        if (userFound == null) {
            throw new NotFoundException("User not found");
        }

        boolean passwordMatches = passwordEncryptionUtil.matchesPassword(loginAuthDTO.getPassword(), userFound.getPassword());

        if (loginAuthDTO.getUsername().equals(userFound.getUsername()) && passwordMatches) {

            String token = jwtUtil.generateToken(Long.toString(userFound.getId()), userFound.getUsername(), userFound.getNivel().name());

            return new AuthDTO(token, expiresIn);

        } else {
            throw new BadRequestException("Invalid username or password");
        }
    }
}
