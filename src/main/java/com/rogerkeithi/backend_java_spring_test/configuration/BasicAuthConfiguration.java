package com.rogerkeithi.backend_java_spring_test.configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

//Habilita o basic auth no swagger
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "BasicAuth", scheme = "basic")
public class BasicAuthConfiguration {
}