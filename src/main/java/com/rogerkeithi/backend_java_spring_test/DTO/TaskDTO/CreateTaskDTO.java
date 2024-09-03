package com.rogerkeithi.backend_java_spring_test.DTO.TaskDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateTaskDTO {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private String status;
    private Long user_id;
}
