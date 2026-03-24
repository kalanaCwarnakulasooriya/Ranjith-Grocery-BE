package com.ranjithgrocery.backend.controllers;

import com.ranjithgrocery.backend.models.Credit;
import com.ranjithgrocery.backend.models.Item;
import com.ranjithgrocery.backend.models.Order;
import com.ranjithgrocery.backend.repositories.CreditRepository;
import com.ranjithgrocery.backend.repositories.ItemRepository;
import com.ranjithgrocery.backend.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CreditRepository creditRepository;

    @GetMapping
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        List<Order> allOrders = orderRepository.findAll();
        double totalSales = allOrders.stream().mapToDouble(Order::getTotalAmount).sum();
        
        List<Item> lowStockItems = itemRepository.findByQuantityLessThan(10);
        LocalDate weekFromNow = LocalDate.now().plusDays(7);
        List<Item> expiringItems = itemRepository.findByExpiryDateBefore(weekFromNow);

        List<Credit> allCredits = creditRepository.findAll();
        double pendingCredit = allCredits.stream()
                .filter(c -> c.getStatus() == Credit.Status.PENDING)
                .mapToDouble(Credit::getAmountOwed).sum();

        stats.put("totalSales", totalSales);
        stats.put("totalOrders", allOrders.size());
        stats.put("lowStockCount", lowStockItems.size());
        stats.put("expiringCount", expiringItems.size());
        stats.put("totalPendingCredit", pendingCredit);
        stats.put("lowStockItems", lowStockItems);
        stats.put("expiringItems", expiringItems);

        return stats;
    }
}
