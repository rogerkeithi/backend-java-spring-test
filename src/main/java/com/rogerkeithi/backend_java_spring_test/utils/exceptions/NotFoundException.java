package com.rogerkeithi.backend_java_spring_test.utils.exceptions;

public class NotFoundException extends RuntimeException {
    private static final Integer STATUS_CODE = 404;
    private static final String ERROR = "Not Found";

    public NotFoundException(String message) {
        super(message);
    }

    public Integer getStatusCode() {
        return STATUS_CODE;
    }

    public String getError() {
        return ERROR;
    }
}