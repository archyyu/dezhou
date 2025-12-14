package karate.com.archy.dezhou;

import com.intuit.karate.junit5.Karate;

/**
 * Karate Test Runner for Dezhou Poker API
 * This class runs all Karate feature files
 */
public class ApiTestRunner {

    @Karate.Test
    Karate testAll() {
        return Karate.run("classpath:karate/com/archy/dezhou")
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate testGameApi() {
        return Karate.run("game-api").relativeTo(getClass());
    }

    @Karate.Test
    Karate testRoomApi() {
        return Karate.run("room-api").relativeTo(getClass());
    }
}