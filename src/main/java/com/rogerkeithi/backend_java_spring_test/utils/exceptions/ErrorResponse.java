package com.rogerkeithi.backend_java_spring_test.utils.exceptions;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse {
    private Integer statusCode;
    private String error;
    private String message;

    public ErrorResponse(Integer statusCode, String error, String message) {
        this.statusCode = statusCode;
        this.error = error;
        this.message = message;
    }

}