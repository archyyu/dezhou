package karate.com.archy.texasholder;

import com.intuit.karate.junit5.Karate;

/**
 * Karate Test Runner for Texas Holder Poker API
 * This class runs all Karate feature files
 */
public class ApiTestRunner {

    @Karate.Test
    Karate testAll() {
        return Karate.run("classpath:karate/com/archy/texasholder")
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate testGameApi() {
        return Karate.run("classpath:karate/com/archy/texasholder/game-api").relativeTo(getClass());
    }

    @Karate.Test
    Karate testRoomApi() {
        return Karate.run("classpath:karate/com/archy/texasholder/room-api").relativeTo(getClass());
    }
}