package com.grocky;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for GROCKY Online Grocery Store
 * 
 * Features:
 * - @EnableAsync: Enables asynchronous method execution (for email notifications)
 * - @EnableScheduling: Enables scheduled tasks (for daily metrics, inventory checks)
 */
@SpringBootApplication
@EnableSpringDataWebSupport
@EnableAsync
@EnableScheduling
public class GroceryApplication {

    public static void main(String[] args) {
        SpringApplication.run(GroceryApplication.class, args);
    }
}
