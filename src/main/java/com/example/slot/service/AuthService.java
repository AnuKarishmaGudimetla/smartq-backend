package com.example.slot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.slot.dto.AuthRequest;
import com.example.slot.model.User;
import com.example.slot.repository.UserRepository;
import com.example.slot.security.JwtUtil;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private PasswordEncoder passwordEncoder;

    public String login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                                  .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        return jwtUtil.generateToken(user.getUsername(), user.getRole().name());
    }
    public void register(User user) {
    if (userRepository.findByUsername(user.getUsername()).isPresent()) {
        throw new RuntimeException("Username already exists");
    }
    if (userRepository.findAll().stream()
            .anyMatch(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(user.getEmail()))) {
        throw new RuntimeException("Email already in use");
    }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userRepository.save(user);
}
}
