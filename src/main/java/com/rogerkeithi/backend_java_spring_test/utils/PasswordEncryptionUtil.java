package com.rogerkeithi.backend_java_spring_test.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

//Este é o nosso codificador de senha para verifica-las
@Component
public class PasswordEncryptionUtil {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordEncryptionUtil(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String encryptPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }
}