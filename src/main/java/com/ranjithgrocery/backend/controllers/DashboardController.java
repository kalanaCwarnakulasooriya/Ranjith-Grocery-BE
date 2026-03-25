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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CreditRepository creditRepository;

    @GetMapping("/admin/dashboard")
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        List<Order> allOrders = orderRepository.findAll();
        double totalSales = allOrders.stream().mapToDouble(Order::getTotalAmount).sum();
        
        LocalDate today = LocalDate.now();
        List<Order> todayOrdersList = allOrders.stream()
                .filter(o -> o.getDate() != null && o.getDate().toLocalDate().equals(today))
                .toList();
        
        double todaySales = todayOrdersList.stream().mapToDouble(Order::getTotalAmount).sum();
        long todayOrders = todayOrdersList.size();

        List<Item> lowStockItems = itemRepository.findByQuantityLessThan(10);
        LocalDate weekFromNow = today.plusDays(7);
        List<Item> expiringItems = itemRepository.findByExpiryDateBefore(weekFromNow);

        List<Credit> allCredits = creditRepository.findAll();
        double pendingCredit = allCredits.stream()
                .filter(c -> c.getStatus() == Credit.Status.PENDING)
                .mapToDouble(Credit::getAmountOwed).sum();

        // Generate Chart Data for the last 7 days
        List<Map<String, Object>> chartData = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            double daySales = allOrders.stream()
                    .filter(o -> o.getDate() != null && o.getDate().toLocalDate().equals(d))
                    .mapToDouble(Order::getTotalAmount).sum();
            long dayOrdersCount = allOrders.stream()
                    .filter(o -> o.getDate() != null && o.getDate().toLocalDate().equals(d))
                    .count();
            
            Map<String, Object> point = new HashMap<>();
            // Format date string to display short month and day (e.g. "Mar 25")
            point.put("date", d.getMonth().name().substring(0, 3) + " " + d.getDayOfMonth());
            point.put("sales", daySales);
            point.put("orders", dayOrdersCount);
            chartData.add(point);
        }

        stats.put("totalSales", totalSales);
        stats.put("totalOrders", allOrders.size());
        stats.put("todaySales", todaySales);
        stats.put("todayOrders", todayOrders);
        stats.put("lowStockCount", lowStockItems.size());
        stats.put("expiringCount", expiringItems.size());
        stats.put("totalPendingCredit", pendingCredit);
        stats.put("lowStockItems", lowStockItems);
        stats.put("expiringItems", expiringItems);
        stats.put("chartData", chartData);

        return stats;
    }
}
