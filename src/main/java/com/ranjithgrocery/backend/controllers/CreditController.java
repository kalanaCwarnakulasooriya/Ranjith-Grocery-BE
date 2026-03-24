package com.ranjithgrocery.backend.controllers;

import com.ranjithgrocery.backend.models.Credit;
import com.ranjithgrocery.backend.models.User;
import com.ranjithgrocery.backend.repositories.CreditRepository;
import com.ranjithgrocery.backend.repositories.UserRepository;
import com.ranjithgrocery.backend.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/credits")
@CrossOrigin(origins = "*")
public class CreditController {

    @Autowired
    private CreditRepository creditRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping
    public List<Credit> getAllCredits() {
        return creditRepository.findAll();
    }

    @PostMapping
    public Credit addCredit(@RequestBody Credit credit) {
        credit.setStatus(Credit.Status.PENDING);
        return creditRepository.save(credit);
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<Credit> payCredit(@PathVariable String id) {
        return creditRepository.findById(id).map(credit -> {
            credit.setStatus(Credit.Status.PAID);
            return ResponseEntity.ok(creditRepository.save(credit));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/remind")
    public ResponseEntity<String> remindCredit(@PathVariable String id) {
        return creditRepository.findById(id).map(credit -> {
            if (credit.getCustomerId() != null) {
                User user = userRepository.findById(credit.getCustomerId()).orElse(null);
                if (user != null) {
                    String msg = "Hello " + user.getName() + ",\nThis is a reminder for your pending credit of LKR " + credit.getAmountOwed() + ". Please clear the dues by " + credit.getDueDate() + ".";
                    emailService.sendSimpleEmail(user.getEmail(), "Ranjith Grocery - Credit Reminder", msg);
                    return ResponseEntity.ok("Reminder sent");
                }
            }
            return ResponseEntity.badRequest().body("Customer not found");
        }).orElse(ResponseEntity.notFound().build());
    }
}
