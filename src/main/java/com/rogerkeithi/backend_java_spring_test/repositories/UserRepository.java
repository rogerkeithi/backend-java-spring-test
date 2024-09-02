package com.rogerkeithi.backend_java_spring_test.repositories;

import com.rogerkeithi.backend_java_spring_test.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
