package com.ranjithgrocery.backend.repositories;

import com.ranjithgrocery.backend.models.Credit;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CreditRepository extends MongoRepository<Credit, String> {
    List<Credit> findByCustomerId(String customerId);
}
