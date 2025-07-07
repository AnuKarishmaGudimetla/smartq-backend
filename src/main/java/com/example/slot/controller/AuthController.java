package com.example.slot.controller;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
import com.example.slot.dto.AuthRequest;
import com.example.slot.model.User;
import com.example.slot.repository.UserRepository;
import com.example.slot.service.AuthService;
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            authService.register(user);
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
    try {
    String token = authService.login(request);
    return ResponseEntity.ok(Map.of("token", "Bearer " + token));
    } catch (Exception e) {
    return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
    }
    }
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/users")
    public List<User> getAllUsers() {
    return userRepository.findAll();
    };
}
