package com.ranjithgrocery.backend.controllers;

import com.ranjithgrocery.backend.dto.AuthRequest;
import com.ranjithgrocery.backend.dto.AuthResponse;
import com.ranjithgrocery.backend.dto.RegisterRequest;
import com.ranjithgrocery.backend.models.User;
import com.ranjithgrocery.backend.repositories.UserRepository;
import com.ranjithgrocery.backend.security.CustomUserDetailsService;
import com.ranjithgrocery.backend.security.JwtUtils;
import com.ranjithgrocery.backend.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );

            final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
            final String jwt = jwtUtils.generateToken(userDetails);
            User user = userRepository.findByEmail(authRequest.getEmail()).orElseThrow();

            return ResponseEntity.ok(new AuthResponse(jwt, user.getEmail(), user.getRole().name(), user.getName()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setName(registerRequest.getName());
        user.setPhone(registerRequest.getPhone());
        user.setAddress(registerRequest.getAddress());
        user.setRole(User.Role.CUSTOMER);

        userRepository.save(user);

        // Send Email
        try {
            emailService.sendRegistrationEmail(user.getEmail(), user.getEmail(), registerRequest.getPassword());
        } catch (Exception e) {
            System.err.println("Failed to send email " + e.getMessage());
        }

        return ResponseEntity.ok("User registered successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        java.util.Map<String, Object> details = new java.util.HashMap<>();
        details.put("username", auth.getName());
        details.put("authorities", auth.getAuthorities());
        return ResponseEntity.ok(details);
    }
}
