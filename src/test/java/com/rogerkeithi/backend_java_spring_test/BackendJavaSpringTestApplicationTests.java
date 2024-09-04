package com.rogerkeithi.backend_java_spring_test;

import com.rogerkeithi.backend_java_spring_test.utils.enums.UserNivel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.main.lazy-initialization=true", classes = {UserNivel.class})
class BackendJavaSpringTestApplicationTests {

	@Test
	void contextLoads() {
	}

}
