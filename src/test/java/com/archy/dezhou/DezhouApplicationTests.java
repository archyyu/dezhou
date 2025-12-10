package com.archy.dezhou;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DezhouApplicationTests {

    @Test
    void contextLoads() {
        // Basic test to verify Spring context loads successfully
    }

    @Test
    void testApplicationStarts() {
        // Test that the application can start without errors
        DezhouApplication.main(new String[]{});
    }
}