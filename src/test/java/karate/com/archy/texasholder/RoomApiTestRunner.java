package karate.com.archy.texasholder;

import com.intuit.karate.junit5.Karate;

/**
 * Dedicated test runner for Room API Karate tests
 */
public class RoomApiTestRunner {

    @Karate.Test
    Karate testRoomApiFeature() {
        return Karate.run("classpath:karate/com/archy/texasholder/room-api").relativeTo(getClass());
    }
}