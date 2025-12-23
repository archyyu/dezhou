# Puker Game Load Testing Documentation

## Overview
This document outlines a comprehensive approach to load testing the Puker game server to ensure it can handle production-scale traffic and identify performance bottlenecks.

## Test Objectives
1. Determine maximum sustainable concurrent games
2. Identify system bottlenecks under heavy load
3. Validate database connection handling
4. Test memory management with long-running games
5. Ensure stability during peak usage periods

## Test Environment Requirements

### Hardware Requirements
- **Server**: Minimum 8 CPU cores, 16GB RAM
- **Database**: Dedicated instance with production-like configuration
- **Client Machines**: Multiple machines to simulate distributed load

### Software Requirements
- **Monitoring Tools**: Prometheus, Grafana, or similar
- **Load Generation**: JMeter, Gatling, or custom Java clients
- **Database**: Same version as production with test data

## Test Scenarios

### Scenario 1: Room Creation Stress Test
**Objective**: Test system's ability to handle massive room creation requests

**Test Parameters**:
- Concurrent room creations: 1000+
- Creation rate: 50 rooms/second
- Duration: 5 minutes

**Success Criteria**:
- All rooms created successfully
- No timeouts or errors
- System remains responsive

**Implementation**:
```java
@Test
public void testMassiveRoomCreation() {
    ExecutorService executor = Executors.newFixedThreadPool(100);
    CountDownLatch latch = new CountDownLatch(1000);
    
    for (int i = 0; i < 1000; i++) {
        executor.submit(() -> {
            try {
                GameRoom room = gameService.createRoom("TestRoom" + UUID.randomUUID());
                assertNotNull(room);
                assertTrue(room.isActive());
            } finally {
                latch.countDown();
            }
        });
    }
    
    latch.await(30, TimeUnit.SECONDS);
    assertEquals(0, latch.getCount());
}
```

### Scenario 2: Concurrent Player Joining
**Objective**: Test room joining under heavy concurrent load

**Test Parameters**:
- Existing rooms: 500
- Players per room: 10
- Join rate: 100 players/second
- Total players: 5000

**Success Criteria**:
- All players successfully join rooms
- No duplicate seating
- Room state remains consistent

**Implementation**:
```java
@Test
public void testConcurrentPlayerJoining() {
    // Pre-create 500 rooms
    List<GameRoom> rooms = createTestRooms(500);
    
    ExecutorService executor = Executors.newFixedThreadPool(200);
    CountDownLatch latch = new CountDownLatch(5000);
    
    for (GameRoom room : rooms) {
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                try {
                    Player player = new LoadTestBot(getRandomStrategy());
                    boolean success = room.addPlayer(player);
                    assertTrue(success);
                    assertEquals(10, room.getPlayerCount());
                } finally {
                    latch.countDown();
                }
            });
        }
    }
    
    latch.await(60, TimeUnit.SECONDS);
}
```

### Scenario 3: Gameplay Simulation
**Objective**: Test actual gameplay performance under load

**Test Parameters**:
- Active rooms: 200
- Players per room: 8
- Hands per room: 1000
- Bot strategies:
  - 70%: Always call
  - 20%: Random raise (2-5x)
  - 10%: Random fold

**Success Criteria**:
- All hands complete successfully
- No deadlocks or race conditions
- Consistent game state
- Acceptable response times (<500ms per action)

**Implementation**:
```java
@Test
public void testGameplayUnderLoad() {
    List<GameRoom> rooms = createRoomsWithPlayers(200, 8);
    
    rooms.parallelStream().forEach(room -> {
        for (int hand = 0; hand < 1000; hand++) {
            room.startNextHand();
            
            while (!room.isHandComplete()) {
                room.getActivePlayers().forEach(player -> {
                    if (player instanceof LoadTestBot) {
                        Action action = ((LoadTestBot) player).decideAction(room.getCurrentState());
                        room.processPlayerAction(player.getId(), action);
                    }
                });
            }
            
            verifyHandResults(room);
        }
    });
}
```

## Bot Implementation Details

### Bot Strategy Types

```java
public enum BotStrategy {
    ALWAYS_CALL,       // Always calls current bet
    RANDOM_RAISE,      // Raises 2-5x current bet
    SELECTIVE_FOLD,    // Folds based on hand strength
    AGGRESSIVE,        // Mix of raises and calls
    CONSERVATIVE       // Mostly folds, occasional calls
}
```

### Load Test Bot Implementation

```java
@Component
public class LoadTestBot implements Player {
    
    private final String playerId;
    private final BotStrategy strategy;
    private final Random random;
    private int chips;
    
    public LoadTestBot(String playerId, BotStrategy strategy, int startingChips) {
        this.playerId = playerId;
        this.strategy = strategy;
        this.random = new Random();
        this.chips = startingChips;
    }
    
    @Override
    public String getId() {
        return playerId;
    }
    
    @Override
    public Action decideAction(GameState state) {
        switch (strategy) {
            case ALWAYS_CALL:
                return new CallAction();
            
            case RANDOM_RAISE:
                int raiseAmount = state.getCurrentBet() * (2 + random.nextInt(4));
                return new RaiseAction(raiseAmount);
            
            case SELECTIVE_FOLD:
                // Simple hand strength evaluation
                if (shouldFold(state.getPlayerHand())) {
                    return new FoldAction();
                }
                return new CallAction();
            
            case AGGRESSIVE:
                if (random.nextDouble() < 0.7) {
                    return new RaiseAction(state.getCurrentBet() * 3);
                }
                return new CallAction();
            
            case CONSERVATIVE:
                if (random.nextDouble() < 0.3) {
                    return new CallAction();
                }
                return new FoldAction();
            
            default:
                return new CallAction();
        }
    }
    
    private boolean shouldFold(Hand hand) {
        // Simple evaluation - could be enhanced
        return hand.getStrength() < 0.3;
    }
    
    @Override
    public void updateChips(int amount) {
        this.chips += amount;
    }
    
    @Override
    public int getChipCount() {
        return chips;
    }
}
```

## Test Execution Framework

### Test Runner Architecture

```java
public class LoadTestRunner {
    
    private final GameService gameService;
    private final MetricsCollector metricsCollector;
    private final int roomCount;
    private final int playersPerRoom;
    
    public LoadTestRunner(GameService gameService, 
                         MetricsCollector metricsCollector,
                         int roomCount,
                         int playersPerRoom) {
        this.gameService = gameService;
        this.metricsCollector = metricsCollector;
        this.roomCount = roomCount;
        this.playersPerRoom = playersPerRoom;
    }
    
    public void executeTest() {
        try {
            // Phase 1: Ramp-up
            rampUpPhase();
            
            // Phase 2: Steady state
            steadyStatePhase();
            
            // Phase 3: Ramp-down
            rampDownPhase();
            
            // Generate report
            generateTestReport();
            
        } catch (Exception e) {
            metricsCollector.recordError(e);
        } finally {
            cleanup();
        }
    }
    
    private void rampUpPhase() {
        // Gradually increase load
        for (int i = 1; i <= 10; i++) {
            int currentLoad = (roomCount * i) / 10;
            createAndPopulateRooms(currentLoad);
            sleep(30000); // 30 seconds between increments
        }
    }
    
    private void steadyStatePhase() {
        // Maintain peak load
        long startTime = System.currentTimeMillis();
        
        while (System.currentTimeMillis() - startTime < 30 * 60 * 1000) { // 30 minutes
            runGameplayCycle();
            collectMetrics();
            sleep(5000);
        }
    }
    
    private void rampDownPhase() {
        // Gradually reduce load
        for (int i = 10; i >= 1; i--) {
            int currentLoad = (roomCount * i) / 10;
            removeRooms(currentLoad);
            sleep(30000);
        }
    }
    
    private void createAndPopulateRooms(int count) {
        // Implementation details
    }
    
    private void runGameplayCycle() {
        // Implementation details
    }
    
    private void collectMetrics() {
        metricsCollector.collectSystemMetrics();
        metricsCollector.collectGameMetrics();
    }
    
    private void generateTestReport() {
        // Generate comprehensive test report
    }
    
    private void cleanup() {
        // Clean up all test resources
    }
}
```

## Key Metrics to Monitor

### System Metrics
- **CPU Usage**: Overall and per-core utilization
- **Memory Usage**: Heap and non-heap memory
- **GC Activity**: Frequency and duration of garbage collection
- **Thread Count**: Active and waiting threads
- **Network I/O**: Bandwidth and connection count

### Database Metrics
- **Query Execution Time**: Average and 95th percentile
- **Connection Pool Usage**: Active and idle connections
- **Lock Contention**: Deadlocks and wait times
- **Transaction Rates**: Transactions per second

### Game-Specific Metrics
- **Hand Completion Rate**: Hands processed per second
- **Action Processing Time**: Time to process player actions
- **Room State Updates**: Frequency and duration
- **Player Join/Leave Times**: Duration of join/leave operations

### Error Metrics
- **Failed Operations**: Count and types of failures
- **Timeouts**: Operations exceeding time limits
- **Deadlocks**: Thread contention issues
- **Resource Leaks**: Unclosed connections or resources

## Test Data Management

### Database Setup
```sql
-- Create test database schema
CREATE DATABASE puker_load_test;

-- Create tables (same as production)
CREATE TABLE game_rooms (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255),
    status VARCHAR(20),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE players (
    id VARCHAR(36) PRIMARY KEY,
    room_id VARCHAR(36),
    name VARCHAR(255),
    chips INT,
    status VARCHAR(20),
    FOREIGN KEY (room_id) REFERENCES game_rooms(id)
);

-- Additional tables as needed
```

### Test Data Generation
```java
public class TestDataGenerator {
    
    public void generateTestData(int roomCount, int playersPerRoom) {
        // Generate room data
        for (int i = 0; i < roomCount; i++) {
            String roomId = "room_" + UUID.randomUUID();
            String roomName = "TestRoom_" + i;
            
            // Insert room
            database.insertRoom(roomId, roomName);
            
            // Generate player data
            for (int j = 0; j < playersPerRoom; j++) {
                String playerId = "player_" + UUID.randomUUID();
                String playerName = "Bot_" + roomId + "_" + j;
                int chips = 1000 + random.nextInt(9000);
                
                database.insertPlayer(playerId, roomId, playerName, chips);
            }
        }
    }
}
```

## Performance Optimization Recommendations

### Database Optimization
1. **Connection Pooling**: Configure optimal pool size
2. **Indexing**: Ensure proper indexes on frequently queried columns
3. **Batch Operations**: Use batch inserts/updates where possible
4. **Caching**: Implement caching for frequently accessed data

### Application Optimization
1. **Thread Pool Configuration**: Optimize thread pool sizes
2. **Asynchronous Processing**: Use async for non-critical operations
3. **Memory Management**: Monitor and optimize object creation
4. **Locking Strategy**: Minimize lock contention

### Game-Specific Optimization
1. **Room Partitioning**: Distribute rooms across multiple instances
2. **State Management**: Optimize game state serialization
3. **Action Processing**: Batch process player actions
4. **Cleanup**: Implement efficient room cleanup

## Test Reporting

### Report Structure
1. **Executive Summary**: High-level results and findings
2. **Test Configuration**: Environment and parameters
3. **Performance Metrics**: Detailed metrics with charts
4. **Bottleneck Analysis**: Identified performance issues
5. **Recommendations**: Specific improvement suggestions

### Sample Report Format
```
# Load Test Report - Puker Game

## Test Execution Details
- **Date**: 2023-11-15
- **Duration**: 2 hours
- **Peak Load**: 200 rooms, 1600 players
- **Test Environment**: Staging Server (8 cores, 16GB RAM)

## Key Findings

### Performance Metrics
| Metric | Value | Target |
|--------|-------|--------|
| Max Concurrent Rooms | 200 | 250 |
| Avg Hand Completion Time | 420ms | <500ms |
| 95th Percentile Action Time | 380ms | <400ms |
| CPU Usage (Peak) | 75% | <80% |
| Memory Usage (Peak) | 12GB | <14GB |

### Identified Issues
1. **Database Connection Pool Exhaustion**: Occurred at 180+ rooms
   - Recommendation: Increase pool size from 50 to 100

2. **Memory Leak in Room Cleanup**: Memory not released after room closure
   - Recommendation: Implement proper resource cleanup

3. **Lock Contention in Hand Processing**: Thread blocking during hand transitions
   - Recommendation: Optimize locking strategy

## Conclusion
The system performed well under test conditions, handling up to 200 concurrent rooms with acceptable performance. The identified issues are addressable through configuration changes and code optimizations.
```

## Continuous Load Testing

### Integration with CI/CD
1. **Smoke Tests**: Quick tests on every build
2. **Nightly Load Tests**: Comprehensive tests during off-peak
3. **Pre-Production Tests**: Full load tests before releases

### Automated Test Suite
```java
public class AutomatedLoadTestSuite {
    
    @Test
    @Tag("smoke")
    public void smokeTest() {
        runLoadTest(10, 5, 5); // 10 rooms, 5 players, 5 hands
    }
    
    @Test
    @Tag("nightly")
    public void nightlyLoadTest() {
        runLoadTest(50, 8, 100); // 50 rooms, 8 players, 100 hands
    }
    
    @Test
    @Tag("preprod")
    public void preProductionLoadTest() {
        runLoadTest(150, 8, 500); // 150 rooms, 8 players, 500 hands
    }
    
    private void runLoadTest(int rooms, int players, int hands) {
        // Test implementation
    }
}
```

## Best Practices

1. **Realistic Test Data**: Use production-like data distributions
2. **Gradual Load Increase**: Avoid sudden spikes that don't reflect real usage
3. **Comprehensive Monitoring**: Monitor all system components
4. **Proper Cleanup**: Ensure no test data contaminates production
5. **Document Results**: Maintain historical performance data
6. **Regular Testing**: Integrate load testing into development cycle

## Troubleshooting Guide

### Common Issues and Solutions

**Issue: Database Connection Timeouts**
- Solution: Increase connection pool size
- Solution: Optimize database queries
- Solution: Implement retry logic

**Issue: High CPU Usage**
- Solution: Profile application to find hotspots
- Solution: Optimize algorithms
- Solution: Increase hardware resources

**Issue: Memory Leaks**
- Solution: Use profiling tools to identify leaks
- Solution: Implement proper resource cleanup
- Solution: Review object lifecycle management

**Issue: Slow Response Times**
- Solution: Optimize database queries
- Solution: Implement caching
- Solution: Review thread pool configuration

## Appendix

### Test Configuration Examples

**JMeter Configuration**:
```
Thread Group:
- Number of Threads: 1000
- Ramp-Up Period: 60 seconds
- Loop Count: Forever

HTTP Request:
- Server: localhost:8080
- Path: /api/game/join
- Parameters: roomId, playerId
```

**Gatling Configuration**:
```scala
val scn = scenario("Player Join Test")
  .exec(http("join_room")
    .post("/api/game/join")
    .body(StringBody("{"roomId":"${roomId}","playerId":"${playerId}"}"))
    .check(status.is(200)))

setUp(
  scn.inject(
    rampUsers(1000) during (60 seconds)
  )
).protocols(httpProtocol)
```

### Useful Tools
- **Load Generation**: JMeter, Gatling, Locust
- **Monitoring**: Prometheus, Grafana, Datadog
- **Profiling**: VisualVM, YourKit, JProfiler
- **Database**: pgAdmin, MySQL Workbench, DBeaver

This comprehensive load testing approach will help ensure the Puker game server can handle production-scale traffic while maintaining performance and stability.