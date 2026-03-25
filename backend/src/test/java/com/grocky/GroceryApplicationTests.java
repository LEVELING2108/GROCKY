package com.grocky;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Main application context test
 * Verifies that the Spring application context loads successfully
 */
@SpringBootTest
@ActiveProfiles("test")
class GroceryApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that the application context loads without errors
    }
}
