package com.rogerkeithi.backend_java_spring_test;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.TimeZone;

@SpringBootApplication
@EntityScan(basePackages = "com.rogerkeithi.backend_java_spring_test.model")
public class BackendJavaSpringTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendJavaSpringTestApplication.class, args);
	}

}
