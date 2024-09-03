package com.rogerkeithi.backend_java_spring_test.DTO.UserDTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class UserGetAllDTO {
    private Long id;
    private String username;
    private String nivel;
}
