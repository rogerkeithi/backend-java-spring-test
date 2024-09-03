package com.rogerkeithi.backend_java_spring_test.controller;

import com.rogerkeithi.backend_java_spring_test.utils.SecurityUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class HomeController {
    private final SecurityUtil securityUtil;

    public HomeController(SecurityUtil securityUtil) {
        this.securityUtil = securityUtil;
    }

    @GetMapping("/")
    public RedirectView redirect() {
        return new RedirectView("swagger-ui.html");
    }
}