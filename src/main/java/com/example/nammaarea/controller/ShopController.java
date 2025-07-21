package com.example.nammaarea.controller;

import com.example.nammaarea.model.Item;
import com.example.nammaarea.model.Shop;
import com.example.nammaarea.service.ItemService;
import com.example.nammaarea.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/shop")
public class ShopController {
    
    @Autowired
    private ShopService shopService;
    
    @Autowired
    private ItemService itemService;
    
    // Upload directory - creates folder in static/uploads/items/
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/items/";
    
    @GetMapping("/login")
    public String showLoginForm() {
        return "shop-login";
    }
    
    @PostMapping("/login")
    public String processLogin(@RequestParam String username, 
                              @RequestParam String password,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        Shop shop = shopService.authenticateShop(username, password);
        if (shop != null) {
            session.setAttribute("shop", shop);
            return "redirect:/shop/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid credentials");
            return "redirect:/shop/login";
        }
    }
    
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        Shop shop = (Shop) session.getAttribute("shop");
        if (shop == null) {
            return "redirect:/shop/login";
        }
        
        List<Item> items = itemService.getItemsByShop(shop);
        model.addAttribute("shop", shop);
        model.addAttribute("items", items);
        model.addAttribute("newItem", new Item());
        return "shop-dashboard";
    }
    
    @PostMapping("/add-item")
    public String addItem(@ModelAttribute Item item,
                         @RequestParam("imageFile") MultipartFile imageFile,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        Shop shop = (Shop) session.getAttribute("shop");
        if (shop == null) {
            return "redirect:/shop/login";
        }
        
        try {
            // Handle image upload
            if (!imageFile.isEmpty()) {
                String filename = saveImage(imageFile);
                item.setImageFilename(filename);
            }
            
            item.setShop(shop);
            itemService.saveItem(item);
            redirectAttributes.addFlashAttribute("success", "Item added successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add item: " + e.getMessage());
        }
        
        return "redirect:/shop/dashboard";
    }
    
    @PostMapping("/update-item/{id}")
    public String updateItem(@PathVariable Long id,
                           @ModelAttribute Item updatedItem,
                           @RequestParam("imageFile") MultipartFile imageFile,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        Shop shop = (Shop) session.getAttribute("shop");
        if (shop == null) {
            return "redirect:/shop/login";
        }
        
        try {
            Item existingItem = itemService.getItemById(id);
            if (existingItem == null || !existingItem.getShop().getId().equals(shop.getId())) {
                redirectAttributes.addFlashAttribute("error", "Item not found or unauthorized");
                return "redirect:/shop/dashboard";
            }
            
            // Update basic fields
            existingItem.setName(updatedItem.getName());
            existingItem.setPrice(updatedItem.getPrice());
            existingItem.setDeliveryCharge(updatedItem.getDeliveryCharge());
            existingItem.setDescription(updatedItem.getDescription());
            
            // Handle new image upload
            if (!imageFile.isEmpty()) {
                // Delete old image if exists
                if (existingItem.getImageFilename() != null) {
                    deleteImage(existingItem.getImageFilename());
                }
                // Save new image
                String filename = saveImage(imageFile);
                existingItem.setImageFilename(filename);
            }
            
            itemService.saveItem(existingItem);
            redirectAttributes.addFlashAttribute("success", "Item updated successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update item: " + e.getMessage());
        }
        
        return "redirect:/shop/dashboard";
    }
    
    @PostMapping("/delete-item/{id}")
    public String deleteItem(@PathVariable Long id,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        Shop shop = (Shop) session.getAttribute("shop");
        if (shop == null) {
            return "redirect:/shop/login";
        }
        
        try {
            Item item = itemService.getItemById(id);
            if (item == null || !item.getShop().getId().equals(shop.getId())) {
                redirectAttributes.addFlashAttribute("error", "Item not found or unauthorized");
                return "redirect:/shop/dashboard";
            }
            
            // Delete image file if exists
            if (item.getImageFilename() != null) {
                deleteImage(item.getImageFilename());
            }
            
            itemService.deleteItem(id);
            redirectAttributes.addFlashAttribute("success", "Item deleted successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete item: " + e.getMessage());
        }
        
        return "redirect:/shop/dashboard";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/shop/login";
    }
    
    // Helper method to save image
    private String saveImage(MultipartFile file) throws IOException {
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;
        
        // Save file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);
        
        return filename;
    }
    
    // Helper method to delete image
    private void deleteImage(String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR + filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log error but don't throw exception
            System.err.println("Failed to delete image: " + filename);
        }
    }
}