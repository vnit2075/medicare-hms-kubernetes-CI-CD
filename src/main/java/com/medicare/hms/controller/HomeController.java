package com.medicare.hms.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "<h1>🏥 MediCare Hospital Management System</h1>" +
               "<p>Application is running successfully on Kubernetes cluster!</p>" +
               "<p>Access Patient Records API: <a href='/api/patients'>/api/patients</a></p>";
    }
}
