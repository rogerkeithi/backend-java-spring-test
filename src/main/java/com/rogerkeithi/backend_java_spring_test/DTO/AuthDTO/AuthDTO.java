package com.rogerkeithi.backend_java_spring_test.DTO.AuthDTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class AuthDTO {
    private String token;
    private Long expiresIn;
}
