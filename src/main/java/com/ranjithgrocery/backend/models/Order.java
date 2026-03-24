package com.ranjithgrocery.backend.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private String customerId; // Reference to User
    private List<OrderItem> items;
    private Double totalAmount;
    private DeliveryType deliveryType; // HOME, PICKUP
    private Double deliveryCharge; // Only for HOME
    private Status status; // PENDING, PAID, CANCELLED
    private LocalDateTime date;

    public enum DeliveryType {
        HOME, PICKUP
    }

    public enum Status {
        PENDING, PAID, CANCELLED
    }

    @Data
    public static class OrderItem {
        private String itemId;
        private String name;
        private Double price;
        private Integer quantity;
    }
}
