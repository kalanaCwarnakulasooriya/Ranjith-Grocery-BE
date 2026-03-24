package com.ranjithgrocery.backend.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String email;
    private String password;
    private String name;
    private String phone;
    private String address;
    private Role role; // ADMIN or CUSTOMER

    public enum Role {
        ADMIN, CUSTOMER
    }
}
