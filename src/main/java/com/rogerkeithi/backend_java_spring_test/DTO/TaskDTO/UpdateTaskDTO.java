package com.rogerkeithi.backend_java_spring_test.DTO.TaskDTO;

import com.rogerkeithi.backend_java_spring_test.utils.enums.TaskStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UpdateTaskDTO {
    private String title;
    private String description;
    private Date dueDate;
    private TaskStatus status;
    private Long user_id;
}
