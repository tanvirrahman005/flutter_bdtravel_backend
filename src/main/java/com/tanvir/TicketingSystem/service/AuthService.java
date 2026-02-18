package com.tanvir.TicketingSystem.service;

import com.tanvir.TicketingSystem.dto.JwtResponse;
import com.tanvir.TicketingSystem.dto.LoginRequest;
import com.tanvir.TicketingSystem.dto.RegisterRequest;
import com.tanvir.TicketingSystem.entity.User;
import com.tanvir.TicketingSystem.entity.User.UserRole;
import com.tanvir.TicketingSystem.repository.UserRepository;
import com.tanvir.TicketingSystem.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    public JwtResponse login(LoginRequest loginRequest) {
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Load user details
        User user = userRepository.findByUserName(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate JWT token
        String jwt = jwtUtil.generateTokenFromUser(user);

        return new JwtResponse(
                jwt,
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getRole());
    }

    public String register(RegisterRequest registerRequest) {
        // Check if username already exists
        if (userRepository.findByUserName(registerRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already taken!");
        }

        // Check if email already exists
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already in use!");
        }

        // Create new user
        User user = new User();
        user.setUserName(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(UserRole.USER); // Default role
        user.setIsActive(true);

        userRepository.save(user);

        return "User registered successfully!";
    }
}
