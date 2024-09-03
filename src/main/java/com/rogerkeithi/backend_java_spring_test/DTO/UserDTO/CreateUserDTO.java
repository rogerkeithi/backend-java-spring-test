package com.rogerkeithi.backend_java_spring_test.DTO.UserDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserDTO {
    private String username;
    private String nivel;
    private String password;
}
