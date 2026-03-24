package com.ranjithgrocery.backend.controllers;

import com.ranjithgrocery.backend.models.Item;
import com.ranjithgrocery.backend.repositories.ItemRepository;
import com.ranjithgrocery.backend.services.ImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ImageUploadService imageUploadService;

    // Public view
    @GetMapping("/items")
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<Item> getItem(@PathVariable String id) {
        return itemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Admin routes
    @PostMapping("/admin/items")
    public ResponseEntity<Item> createItem(
            @RequestParam("itemData") String itemData,
            @RequestParam(value = "files", required = false) MultipartFile[] files) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        Item item = mapper.readValue(itemData, Item.class);

        List<String> imageUrls = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                String url = imageUploadService.saveImage(file);
                imageUrls.add(url);
            }
        }
        item.setImages(imageUrls);

        Item savedItem = itemRepository.save(item);
        return ResponseEntity.ok(savedItem);
    }

    @PutMapping("/admin/items/{id}")
    public ResponseEntity<Item> updateItem(
            @PathVariable String id,
            @RequestParam("itemData") String itemData,
            @RequestParam(value = "files", required = false) MultipartFile[] files) throws IOException {

        return itemRepository.findById(id).map(existingItem -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
                Item updatedData = mapper.readValue(itemData, Item.class);
                
                existingItem.setName(updatedData.getName());
                existingItem.setPrice(updatedData.getPrice());
                existingItem.setQuantity(updatedData.getQuantity());
                existingItem.setUnit(updatedData.getUnit());
                existingItem.setCategory(updatedData.getCategory());
                existingItem.setManufactureDate(updatedData.getManufactureDate());
                existingItem.setExpiryDate(updatedData.getExpiryDate());
                existingItem.setDescription(updatedData.getDescription());

                if (files != null && files.length > 0) {
                    List<String> imageUrls = new ArrayList<>();
                    for (MultipartFile file : files) {
                        imageUrls.add(imageUploadService.saveImage(file));
                    }
                    existingItem.setImages(imageUrls);
                }
                
                return ResponseEntity.ok(itemRepository.save(existingItem));
            } catch (IOException e) {
                return ResponseEntity.internalServerError().<Item>build();
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/admin/items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable String id) {
        if (!itemRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        itemRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
