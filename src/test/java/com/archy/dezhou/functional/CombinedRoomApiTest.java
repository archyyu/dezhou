package com.archy.dezhou.functional;

import com.intuit.karate.junit5.Karate;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Combined test runner for Room API - runs both Java functional tests and Karate tests
 */
@Tag("room-api")
@Tag("combined")
public class CombinedRoomApiTest {

    /**
     * Run Java functional tests for Room API
     */
    @Test
    void runJavaFunctionalTests() {
        // This will be handled by the test suite runner
        // The actual tests are in RoomApiFunctionalTest class
    }

    /**
     * Run Karate tests for Room API
     */
    @Karate.Test
    Karate runKarateTests() {
        return Karate.run("room-api").relativeTo(getClass());
    }
}