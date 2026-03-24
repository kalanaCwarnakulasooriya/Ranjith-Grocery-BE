package com.ranjithgrocery.backend.controllers;

import com.ranjithgrocery.backend.dto.RegisterRequest;
import com.ranjithgrocery.backend.models.User;
import com.ranjithgrocery.backend.repositories.UserRepository;
import com.ranjithgrocery.backend.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @GetMapping
    public List<User> getAllCustomers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.Role.CUSTOMER)
                .toList();
    }

    @PostMapping
    public ResponseEntity<?> addCustomerManually(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setRole(User.Role.CUSTOMER);

        userRepository.save(user);

        // Send Email
        try {
            emailService.sendRegistrationEmail(user.getEmail(), user.getEmail(), request.getPassword());
        } catch (Exception e) {
            System.err.println("Failed to send email " + e.getMessage());
        }

        return ResponseEntity.ok(user);
    }
}
