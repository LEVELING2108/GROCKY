`package com.grocky;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

/**
 * Main application class for GROCKY Online Grocery Store
 */
@SpringBootApplication
@EnableSpringDataWebSupport
public class GroceryApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(GroceryApplication.class, args);
    }
}
