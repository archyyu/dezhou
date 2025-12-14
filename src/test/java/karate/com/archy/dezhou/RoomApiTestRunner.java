package karate.com.archy.dezhou;

import com.intuit.karate.junit5.Karate;

/**
 * Dedicated test runner for Room API Karate tests
 */
public class RoomApiTestRunner {

    @Karate.Test
    Karate testRoomApiFeature() {
        return Karate.run("classpath:karate/com/archy/dezhou/room-api").relativeTo(getClass());
    }
}