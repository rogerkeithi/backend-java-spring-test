package com.rogerkeithi.backend_java_spring_test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class HomeController {

    @GetMapping("/")
    public RedirectView redirect() {
        return new RedirectView("swagger-ui.html");
    }
}