package com.example.nammaarea.config;

import org.springframework.boot.web.servlet.MultipartConfigElement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.MultipartConfigElement;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded images from /uploads/items/ URL path
        registry.addResourceHandler("/uploads/items/**")
                .addResourceLocations("file:src/main/resources/static/uploads/items/");
        
        // Also serve default images
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        return new MultipartConfigElement(
            "", // temp location
            5242880, // max file size (5MB)
            5242880, // max request size (5MB)
            0 // file size threshold
        );
    }
}