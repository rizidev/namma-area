package com.example.nammaarea.controller;

import com.example.nammaarea.model.Item;
import com.example.nammaarea.model.Shop;
import com.example.nammaarea.model.User;
import com.example.nammaarea.service.ItemService;
import com.example.nammaarea.service.ShopService;
import com.example.nammaarea.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private ItemService itemService;

    @PostMapping("/login")
    public User login(@RequestParam String username, @RequestParam String password) {
        return userService.login(username, password).orElse(null);
    }

    @PostMapping("/checkArea")
    public String checkUserInShopRange(@RequestParam double userLat,
                                       @RequestParam double userLng,
                                       @RequestParam Long shopId) {
        Optional<Shop> shop = shopService.findById(shopId);

        if (shop.isPresent()) {
            double distance = calculateDistance(userLat, userLng,
                                                shop.get().getLatitude(),
                                                shop.get().getLongitude());

            return distance <= shop.get().getDeliveryRadiusKm()
                    ? "IN_RANGE"
                    : "OUT_OF_RANGE";
        }

        return "SHOP_NOT_FOUND";
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        int R = 6371; // Radius of earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in km
    }

    @GetMapping("/shopItems")
    public List<Item> getItemsFromShop(@RequestParam Long shopId) {
        return shopService.findById(shopId)
                .map(itemService::getItemsByShop)
                .orElse(null);
    }
}
