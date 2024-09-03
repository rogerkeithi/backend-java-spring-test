package com.rogerkeithi.backend_java_spring_test.repositories;

import com.rogerkeithi.backend_java_spring_test.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT t FROM Task t WHERE t._user.username = :username")
    List<Task> findAllByUsername(@Param("username") String username);
}
