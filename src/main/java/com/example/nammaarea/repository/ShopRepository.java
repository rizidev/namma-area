package com.example.nammaarea.repository;

import com.example.nammaarea.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    Optional<Shop> findByUsernameAndPassword(String username, String password);
}
