package com.ranjithgrocery.backend.services;

import com.ranjithgrocery.backend.models.Item;
import com.ranjithgrocery.backend.models.User;
import com.ranjithgrocery.backend.repositories.ItemRepository;
import com.ranjithgrocery.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryScheduler {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    // Run every day at 8 AM
    @Scheduled(cron = "0 0 8 * * ?")
    public void checkLowStockAndExpiry() {
        List<User> admins = userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.Role.ADMIN)
                .toList();
        
        if (admins.isEmpty()) return;
        String adminEmail = admins.get(0).getEmail(); // Sending to the first admin for simplicity

        // Check Low Stock (Threshold: 10 items)
        List<Item> lowStockItems = itemRepository.findByQuantityLessThan(10);
        if (!lowStockItems.isEmpty()) {
            String message = "The following items are running low on stock:\n\n";
            for (Item item : lowStockItems) {
                message += item.getName() + " - Remaining Quantity: " + item.getQuantity() + "\n";
            }
            emailService.sendSimpleEmail(adminEmail, "Low Stock Alert for Ranjith Grocery", message);
        }

        // Check Expiring soon (within 7 days)
        LocalDate weekFromNow = LocalDate.now().plusDays(7);
        List<Item> expiringItems = itemRepository.findByExpiryDateBefore(weekFromNow);
        
        if (!expiringItems.isEmpty()) {
            String message = "The following items are expiring soon or already expired:\n\n";
            for (Item item : expiringItems) {
                message += item.getName() + " - Expiry Date: " + item.getExpiryDate() + "\n";
            }
            emailService.sendSimpleEmail(adminEmail, "Item Expiry Alert for Ranjith Grocery", message);
        }
    }
}
