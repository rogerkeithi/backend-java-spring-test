package com.rogerkeithi.backend_java_spring_test.services.interfaces;

import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.UserCreateDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.UserGetAllDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.UserUpdateDTO;
import com.rogerkeithi.backend_java_spring_test.model.User;
import java.util.List;

public interface IUserService {
    User createUser(UserCreateDTO userCreateDTO);
    User updateUser(Long id, UserUpdateDTO userUpdateDTO);
    void deleteUser(Long id);
    List<UserGetAllDTO> getAllUsers();
}