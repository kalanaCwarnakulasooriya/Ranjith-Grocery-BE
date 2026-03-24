package com.ranjithgrocery.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendRegistrationEmail(String to, String username, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("admin@ranjithgrocery.com");
        message.setTo(to);
        message.setSubject("Welcome to Ranjith Grocery!");
        message.setText("Hello,\n\nYour account has been created successfully.\nUsername: " + username + "\nPassword: " + password + "\n\nThank you for shopping with us!");
        mailSender.send(message);
    }

    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("admin@ranjithgrocery.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
