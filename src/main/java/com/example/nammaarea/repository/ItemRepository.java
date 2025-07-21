package com.example.naamaarea.repository;

import com.nammaarea.model.Item;
import com.nammaarea.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByShop(Shop shop);
}
