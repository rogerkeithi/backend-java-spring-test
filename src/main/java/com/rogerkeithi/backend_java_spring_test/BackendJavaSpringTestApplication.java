package com.rogerkeithi.backend_java_spring_test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.rogerkeithi.backend_java_spring_test.model")
public class BackendJavaSpringTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendJavaSpringTestApplication.class, args);
	}

}
