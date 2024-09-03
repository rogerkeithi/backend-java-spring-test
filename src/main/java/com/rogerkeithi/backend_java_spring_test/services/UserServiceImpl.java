package com.rogerkeithi.backend_java_spring_test.services;

import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.CreateUserDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.UserDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.UpdateUserDTO;
import com.rogerkeithi.backend_java_spring_test.utils.PasswordEncryptionUtil;
import com.rogerkeithi.backend_java_spring_test.utils.ValidationUtil;
import com.rogerkeithi.backend_java_spring_test.utils.exceptions.BadRequestException;
import com.rogerkeithi.backend_java_spring_test.model.User;
import com.rogerkeithi.backend_java_spring_test.repositories.UserRepository;
import com.rogerkeithi.backend_java_spring_test.services.interfaces.IUserService;
import com.rogerkeithi.backend_java_spring_test.utils.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncryptionUtil passwordEncryptionUtil;
    private final ValidationUtil validationUtil;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncryptionUtil passwordEncryptionUtil, ValidationUtil validationUtil) {
        this.userRepository = userRepository;
        this.passwordEncryptionUtil = passwordEncryptionUtil;
        this.validationUtil = validationUtil;
    }

    @Override
    public UserDTO createUser(CreateUserDTO createUserDTO){
        String encryptedPassword = passwordEncryptionUtil.encryptPassword(createUserDTO.getPassword());

        validationUtil.requireStringNonEmpty(createUserDTO.getUsername(), "Username is required");
        validationUtil.requireStringNonEmpty(createUserDTO.getNivel(), "Nivel is required");
        validationUtil.requireStringNonEmpty(encryptedPassword, "Password is required");

        User userFound = userRepository.findByUsername(createUserDTO.getUsername());

        if (userFound != null) {
            throw new BadRequestException("Username already exists");
        }

        User user = new User();
        user.setUsername(createUserDTO.getUsername());
        user.setNivel(createUserDTO.getNivel());
        user.setPassword(encryptedPassword);
        userRepository.save(user);

        return User.toDTO(user);
    }

    @Override
    public UserDTO updateUser(Long id, UpdateUserDTO updateUserDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (updateUserDTO.getNivel() != null && !updateUserDTO.getNivel().trim().isEmpty()) {
            user.setNivel(updateUserDTO.getNivel());
        }
        if (updateUserDTO.getUsername() != null && !updateUserDTO.getUsername().trim().isEmpty()) {
            user.setUsername(updateUserDTO.getUsername());
        }
        if (updateUserDTO.getPassword() != null && !updateUserDTO.getPassword().trim().isEmpty()) {
            String encryptedPassword = passwordEncryptionUtil.encryptPassword(updateUserDTO.getPassword());
            user.setPassword(encryptedPassword);
        }

        userRepository.save(user);

        return User.toDTO(user);
    }

    @Override
    public void deleteUser(Long id) {
        Optional<User> userFound = userRepository.findById(id);

        if (userFound.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        userRepository.deleteById(id);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getNivel()))
                .collect(Collectors.toList());
    }
}
