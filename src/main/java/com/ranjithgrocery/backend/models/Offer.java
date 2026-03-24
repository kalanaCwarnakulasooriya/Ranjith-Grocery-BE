package com.ranjithgrocery.backend.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Data
@Document(collection = "offers")
public class Offer {
    @Id
    private String id;
    private String itemId;
    private String discountType; // PERCENTAGE or FIXED
    private Double discountValue;
    private LocalDate startDate;
    private LocalDate endDate;
}
