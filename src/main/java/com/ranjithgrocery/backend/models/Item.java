package com.ranjithgrocery.backend.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@Document(collection = "items")
public class Item {
    @Id
    private String id;
    private String name;
    private Double price;
    private Integer quantity; // stock
    private String unit; // e.g. grams, kg, L, ml, pcs
    private String category;
    private LocalDate manufactureDate;
    private LocalDate expiryDate;
    private String description;
    private List<String> images; // List of image URLs
}
