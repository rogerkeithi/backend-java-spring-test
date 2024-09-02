package com.rogerkeithi.backend_java_spring_test.utils.exceptions;

public class BadRequestException extends RuntimeException {
    private static final Integer STATUS_CODE = 400;
    private static final String ERROR = "Bad Request";

    public BadRequestException(String message) {
        super(message);
    }

    public Integer getStatusCode() {
        return STATUS_CODE;
    }

    public String getError() {
        return ERROR;
    }
}