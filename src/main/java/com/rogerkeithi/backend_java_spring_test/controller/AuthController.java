package com.rogerkeithi.backend_java_spring_test.controller;

import com.rogerkeithi.backend_java_spring_test.DTO.AuthDTO.AuthDTO;
import com.rogerkeithi.backend_java_spring_test.DTO.AuthDTO.LoginAuthDTO;
import com.rogerkeithi.backend_java_spring_test.services.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://127.0.0.1:3000", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthServiceImpl authServiceImpl;

    public AuthController(AuthServiceImpl authServiceImpl) {
        this.authServiceImpl = authServiceImpl;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login",
            description = "Return user informations"
    )
    public ResponseEntity<AuthDTO> login(@RequestBody LoginAuthDTO loginAuthDTO) {
        AuthDTO loginAuth = authServiceImpl.login(loginAuthDTO);
        return ResponseEntity.ok(loginAuth);
    }
}
