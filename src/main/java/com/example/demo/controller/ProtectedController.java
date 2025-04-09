package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/protected")
public class ProtectedController {

    @GetMapping
    public ResponseEntity<String> getProtectedMessage() {
        return ResponseEntity.ok("✅ You are authenticated and accessing a protected route!");
    }
}
