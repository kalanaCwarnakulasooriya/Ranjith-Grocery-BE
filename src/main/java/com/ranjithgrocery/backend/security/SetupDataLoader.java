package com.ranjithgrocery.backend.security;

import com.ranjithgrocery.backend.models.User;
import com.ranjithgrocery.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SetupDataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin@ranjithgrocery.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("admin@ranjithgrocery.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setName("Admin User");
            admin.setRole(User.Role.ADMIN);
            admin.setPhone("0000000000");
            userRepository.save(admin);
            System.out.println("Default admin user created: admin@ranjithgrocery.com / admin123");
        }
    }
}
