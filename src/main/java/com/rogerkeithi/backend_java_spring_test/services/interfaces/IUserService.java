package com.rogerkeithi.backend_java_spring_test.services.interfaces;

import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.UserCreateDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.UserUpdateDTO;
import com.rogerkeithi.backend_java_spring_test.utils.exceptions.BadRequestException;
import com.rogerkeithi.backend_java_spring_test.model.User;
import java.util.List;

public interface IUserService {
    User createUser(UserCreateDTO user) throws BadRequestException;
    User updateUser(Long id, UserUpdateDTO user);
    void deleteUser(Long id);
    List<User> getAllUsers();
}