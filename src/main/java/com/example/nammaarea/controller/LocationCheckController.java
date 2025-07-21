package com.example.nammaarea.controller;

import com.example.nammaarea.model.Shop;
import com.example.nammaarea.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/user")
public class LocationCheckController {

    @Autowired
    private ShopService shopService;

    @PostMapping("/checkRange")
    public String checkDeliveryRange(@RequestParam double userLat,
                                     @RequestParam double userLng,
                                     @RequestParam Long shopId) {

        Optional<Shop> shop = shopService.findById(shopId);
        if (shop.isPresent()) {
            Shop s = shop.get();
            double distance = calculateDistance(userLat, userLng, s.getLatitude(), s.getLongitude());

            return distance <= s.getDeliveryRadiusKm() ? "in-range" : "out-of-range";
        }

        return "out-of-range";
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
