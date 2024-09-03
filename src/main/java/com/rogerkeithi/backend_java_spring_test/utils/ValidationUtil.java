package com.rogerkeithi.backend_java_spring_test.utils;

import com.rogerkeithi.backend_java_spring_test.utils.exceptions.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ValidationUtil {
    public void requireStringNonEmpty(String value, String errorMessage) {
        if (value == null || value.trim().isEmpty()) {
            throw new BadRequestException(errorMessage);
        }
    }

    public <E extends Enum<E>> void requireValidEnum(Class<E> enumClass, String value, String errorMessage) {
        boolean isValid = Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(e -> e.name().equalsIgnoreCase(value));

        if (!isValid) {
            throw new BadRequestException(errorMessage);
        }
    }
}
