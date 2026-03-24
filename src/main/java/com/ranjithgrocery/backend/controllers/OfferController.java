package com.ranjithgrocery.backend.controllers;

import com.ranjithgrocery.backend.models.Offer;
import com.ranjithgrocery.backend.repositories.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class OfferController {

    @Autowired
    private OfferRepository offerRepository;

    @GetMapping("/offers")
    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }

    @PostMapping("/admin/offers")
    public Offer createOffer(@RequestBody Offer offer) {
        return offerRepository.save(offer);
    }

    @DeleteMapping("/admin/offers/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable String id) {
        if (!offerRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        offerRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
