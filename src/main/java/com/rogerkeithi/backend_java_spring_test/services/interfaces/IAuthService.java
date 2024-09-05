package com.rogerkeithi.backend_java_spring_test.services.interfaces;

import com.rogerkeithi.backend_java_spring_test.DTO.AuthDTO.AuthDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.AuthDTO.LoginAuthDTO;

public interface IAuthService {
    AuthDTO login(LoginAuthDTO loginAuthDTO);
}
