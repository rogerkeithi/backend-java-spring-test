package com.rogerkeithi.backend_java_spring_test.utils;

import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class EnumUtil {
    public <E extends Enum<E>> boolean isValidEnum(Class<E> enumClass, String value) {
        return Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(e -> e.name().equalsIgnoreCase(value));
    }
}