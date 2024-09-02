package com.rogerkeithi.backend_java_spring_test.repositories;

import com.rogerkeithi.backend_java_spring_test.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {}
