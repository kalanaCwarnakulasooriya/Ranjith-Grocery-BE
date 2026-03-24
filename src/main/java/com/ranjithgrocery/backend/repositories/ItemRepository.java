package com.ranjithgrocery.backend.repositories;

import com.ranjithgrocery.backend.models.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDate;
import java.util.List;

public interface ItemRepository extends MongoRepository<Item, String> {
    List<Item> findByExpiryDateBefore(LocalDate date);
    List<Item> findByQuantityLessThan(Integer quantity);
}
