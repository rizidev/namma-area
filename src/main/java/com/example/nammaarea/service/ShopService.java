
package com.example.nammaarea.service;
import com.example.nammaarea.model.Shop;
import com.example.nammaarea.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ShopService {

    @Autowired
    private ShopRepository shopRepository;

    public Optional<Shop> login(String username, String password) {
        return shopRepository.findByUsernameAndPassword(username, password);
    }

    public Shop save(Shop shop) {
        return shopRepository.save(shop);
    }

    public Optional<Shop> findById(Long id) {
        return shopRepository.findById(id);
    }
}
