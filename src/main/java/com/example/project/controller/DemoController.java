package com.example.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/info")
    public ResponseEntity<String> sayHello() {
        System.out.println("sayHello method was called");
        return ResponseEntity.ok("Secure Hello World!");
    }


}
