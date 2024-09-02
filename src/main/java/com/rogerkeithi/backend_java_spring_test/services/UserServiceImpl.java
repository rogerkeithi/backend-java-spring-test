package com.rogerkeithi.backend_java_spring_test.services;

import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.UserCreateDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.UserUpdateDTO;
import com.rogerkeithi.backend_java_spring_test.utils.exceptions.BadRequestException;
import com.rogerkeithi.backend_java_spring_test.model.User;
import com.rogerkeithi.backend_java_spring_test.repositories.UserRepository;
import com.rogerkeithi.backend_java_spring_test.services.interfaces.IUserService;
import com.rogerkeithi.backend_java_spring_test.utils.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(UserCreateDTO userCreateDTO) throws BadRequestException {
        if (userCreateDTO.getUsername() == null || userCreateDTO.getUsername().trim().isEmpty()) {
            throw new BadRequestException("Username is required");
        }

        if (userCreateDTO.getNivel() == null || userCreateDTO.getNivel().trim().isEmpty()) {
            throw new BadRequestException("Nivel is required");
        }

        User userFound = userRepository.findByUsername(userCreateDTO.getUsername());

        if (userFound != null) {
            throw new BadRequestException("Username already exists");
        }

        User user = new User();
        user.setUsername(userCreateDTO.getUsername());
        user.setNivel(userCreateDTO.getNivel());

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (userUpdateDTO.getNivel() != null && !userUpdateDTO.getNivel().trim().isEmpty()) {
            existingUser.setNivel(userUpdateDTO.getNivel());
        }
        if (userUpdateDTO.getUsername() != null && !userUpdateDTO.getUsername().trim().isEmpty()) {
            existingUser.setUsername(userUpdateDTO.getUsername());
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
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
