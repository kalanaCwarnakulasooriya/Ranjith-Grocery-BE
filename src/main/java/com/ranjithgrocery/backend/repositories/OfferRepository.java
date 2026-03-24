package com.ranjithgrocery.backend.repositories;

import com.ranjithgrocery.backend.models.Offer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OfferRepository extends MongoRepository<Offer, String> {
}
