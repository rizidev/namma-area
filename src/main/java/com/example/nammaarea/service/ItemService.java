package com.example.nammaarea.service;

import com.example.nammaarea.model.Item;
import com.example.nammaarea.model.Shop;
import com.example.nammaarea.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {
    
    @Autowired
    private ItemRepository itemRepository;
    
    public List<Item> getItemsByShop(Shop shop) {
        return itemRepository.findByShop(shop);
    }
    
    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }
    
    public Item getItemById(Long id) {
        return itemRepository.findById(id).orElse(null);
    }
    
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
    
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
    
    // Method to get items within delivery range of user location
    public List<Item> getItemsInRange(double userLat, double userLon) {
        List<Item> allItems = itemRepository.findAll();
        return allItems.stream()
            .filter(item -> {
                Shop shop = item.getShop();
                double distance = calculateDistance(userLat, userLon, 
                                                  shop.getLatitude(), shop.getLongitude());
                return distance <= shop.getDeliveryRadius();
            })
            .toList();
    }
    
    // Haversine formula to calculate distance between two points
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // convert to kilometers
        
        return distance;
    }
}