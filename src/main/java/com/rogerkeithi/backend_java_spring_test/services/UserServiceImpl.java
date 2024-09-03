package com.rogerkeithi.backend_java_spring_test.services;

import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.CreateUserDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.UserDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.UpdateUserDTO;
import com.rogerkeithi.backend_java_spring_test.utils.PasswordEncryptionUtil;
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

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncryptionUtil passwordEncryptionUtil) {
        this.userRepository = userRepository;
        this.passwordEncryptionUtil = passwordEncryptionUtil;
    }

    @Override
    public User createUser(CreateUserDTO createUserDTO){
        String encryptedPassword = passwordEncryptionUtil.encryptPassword(createUserDTO.getPassword());

        if (createUserDTO.getUsername() == null || createUserDTO.getUsername().trim().isEmpty()) {
            throw new BadRequestException("Username is required");
        }
        if (createUserDTO.getNivel() == null || createUserDTO.getNivel().trim().isEmpty()) {
            throw new BadRequestException("Nivel is required");
        }
        if (encryptedPassword == null || encryptedPassword.trim().isEmpty()) {
            throw new BadRequestException("Password is required");
        }

        User userFound = userRepository.findByUsername(createUserDTO.getUsername());

        if (userFound != null) {
            throw new BadRequestException("Username already exists");
        }

        User user = new User();
        user.setUsername(createUserDTO.getUsername());
        user.setNivel(createUserDTO.getNivel());
        user.setPassword(encryptedPassword);

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, UpdateUserDTO updateUserDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (updateUserDTO.getNivel() != null && !updateUserDTO.getNivel().trim().isEmpty()) {
            existingUser.setNivel(updateUserDTO.getNivel());
        }
        if (updateUserDTO.getUsername() != null && !updateUserDTO.getUsername().trim().isEmpty()) {
            existingUser.setUsername(updateUserDTO.getUsername());
        }
        if (updateUserDTO.getPassword() != null && !updateUserDTO.getPassword().trim().isEmpty()) {
            String encryptedPassword = passwordEncryptionUtil.encryptPassword(updateUserDTO.getPassword());
            existingUser.setPassword(encryptedPassword);
        }
        return userRepository.save(existingUser);
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
