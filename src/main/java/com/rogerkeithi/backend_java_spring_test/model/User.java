package com.rogerkeithi.backend_java_spring_test.model;

import com.rogerkeithi.backend_java_spring_test.DTO.UserDTO.UserDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
//I had to keep the table name with an underscore because 'user' and 'users' are reserved words in H2.
@Table(name = "_user")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String nivel;

    //I also chose to add a password to the entity due to the requirement 'List all tasks of the authenticated user'.
    @Column(nullable = false)
    private String password;

    public static UserDTO toDTO(User user) {
        UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getNivel());
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setNivel(user.getNivel());
        return userDTO;
    }
}