package com.ranjithgrocery.backend.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Data
@Document(collection = "credits")
public class Credit {
    @Id
    private String id;
    private String customerId;
    private Double amountOwed;
    private LocalDate dueDate;
    private Status status;

    public enum Status {
        PENDING, PAID
    }
}
