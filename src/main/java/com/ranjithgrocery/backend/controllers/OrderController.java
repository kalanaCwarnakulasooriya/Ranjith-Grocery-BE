package com.ranjithgrocery.backend.controllers;

import com.ranjithgrocery.backend.models.Item;
import com.ranjithgrocery.backend.models.Order;
import com.ranjithgrocery.backend.models.User;
import com.ranjithgrocery.backend.repositories.ItemRepository;
import com.ranjithgrocery.backend.repositories.OrderRepository;
import com.ranjithgrocery.backend.repositories.UserRepository;
import com.ranjithgrocery.backend.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @PostMapping("/orders") // Online Order
    public ResponseEntity<?> placeOrder(@RequestBody Order order) {
        return processOrder(order, false);
    }

    @PostMapping("/admin/orders") // POS Order
    public ResponseEntity<?> posOrder(@RequestBody Order order) {
        return processOrder(order, true);
    }

    private ResponseEntity<?> processOrder(Order order, boolean isPos) {
        // Stock Reduction
        for (Order.OrderItem orderItem : order.getItems()) {
            Item item = itemRepository.findById(orderItem.getItemId()).orElse(null);
            if (item != null) {
                if (item.getQuantity() < orderItem.getQuantity()) {
                    return ResponseEntity.badRequest().body("Insufficient stock for item: " + item.getName());
                }
                item.setQuantity(item.getQuantity() - orderItem.getQuantity());
                itemRepository.save(item);
            }
        }

        order.setDate(LocalDateTime.now());
        if (order.getStatus() == null) {
            order.setStatus(Order.Status.PENDING);
        }

        Order savedOrder = orderRepository.save(order);

        // Send Email if Customer ID is present
        if (order.getCustomerId() != null) {
            User user = userRepository.findById(order.getCustomerId()).orElse(null);
            if (user != null) {
                String bill = "Order ID: " + savedOrder.getId() + "\nTotal: LKR " + order.getTotalAmount() + "\nItems:\n";
                for (Order.OrderItem item : order.getItems()) {
                    bill += "- " + item.getName() + " x" + item.getQuantity() + "\n";
                }
                emailService.sendSimpleEmail(user.getEmail(), "Your Ranjith Grocery Invoice", bill);
            }
        }

        // Notify Admin
        List<User> admins = userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.Role.ADMIN)
                .toList();
        if (!admins.isEmpty()) {
            emailService.sendSimpleEmail(admins.get(0).getEmail(), "New Order Received", "Order ID: " + savedOrder.getId() + "\nTotal: LKR " + order.getTotalAmount());
        }

        return ResponseEntity.ok(savedOrder);
    }

    @GetMapping("/orders/customer/{customerId}")
    public List<Order> getCustomerOrders(@PathVariable String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @GetMapping("/admin/orders")
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
