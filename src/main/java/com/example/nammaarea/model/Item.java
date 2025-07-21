package com.example.nammaarea.model;

import jakarta.persistence.*;

@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private Double price;
    
    @Column(nullable = false)
    private Double deliveryCharge;
    
    @Column(length = 500)
    private String description;
    
    // New field for image filename
    @Column(name = "image_filename")
    private String imageFilename;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;
    
    // Constructors
    public Item() {}
    
    public Item(String name, Double price, Double deliveryCharge, String description, Shop shop) {
        this.name = name;
        this.price = price;
        this.deliveryCharge = deliveryCharge;
        this.description = description;
        this.shop = shop;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public Double getDeliveryCharge() {
        return deliveryCharge;
    }
    
    public void setDeliveryCharge(Double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getImageFilename() {
        return imageFilename;
    }
    
    public void setImageFilename(String imageFilename) {
        this.imageFilename = imageFilename;
    }
    
    public Shop getShop() {
        return shop;
    }
    
    public void setShop(Shop shop) {
        this.shop = shop;
    }
    
    // Helper method to get image URL
    public String getImageUrl() {
        if (imageFilename != null && !imageFilename.isEmpty()) {
            return "/uploads/items/" + imageFilename;
        }
        return "/images/default-item.png"; // fallback image
    }
}