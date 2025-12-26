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
- **Load Generation**: k6 (recommended), JMeter, Gatling, or custom Java clients
- **Database**: Same version as production with test data
- **k6 Installation**: Node.js-based tool for modern load testing

## Authentication Requirements

All API endpoints in the Puker game server require JWT authentication. Before running any load tests, users must:

1. **Login** to obtain a JWT token via the `/api/login` endpoint
2. **Include the token** in the `Authorization: Bearer <token>` header for all subsequent requests
3. **Handle token expiration** - tokens may need to be refreshed during long-running tests

The test scenarios below have been updated to include proper authentication flows using k6's `setup()` function to obtain tokens before running the main test iterations.

## Game Command Reference

The Puker game server uses numeric command codes for game actions:

| Action | Command Code | Description |
|--------|--------------|-------------|
| Look Cards | `1` | CMD_LOOK_CARD - Reveal player's hand |
| Call | `2` | CMD_ADD_BET - Add bet (call when amount = current bet) |
| Raise | `2` | CMD_ADD_BET - Add bet (raise when amount > current bet) |
| Follow Bet | `3` | CMD_FOLLOW_BET - Call current bet |
| Fold | `4` | CMD_DROP_CARD - Fold current hand |
| All In | `5` | CMD_ALL_IN - Bet all remaining chips |
| Sit Down | `6` | CMD_SITDOWN - Join a table seat |
| Stand Up | `7` | CMD_STANDUP - Leave table seat |
| Leave Room | `8` | CMD_LEAVE - Exit room completely |
| Start Game | `sbot` | CMD_SBOT - Start game/deal initial cards |

The complete gameplay simulation uses these commands in the proper sequence: login → create room → join room → start game (sbot) → betting actions (2, 3, 4).

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

**k6 Implementation**:
```javascript
import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export const options = {
  stages: [
    { duration: '1m', target: 50 },  // Ramp-up to 50 RPS
    { duration: '4m', target: 50 },  // Maintain 50 RPS
    { duration: '1m', target: 0 },   // Ramp-down
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'],  // <1% errors
    http_req_duration: ['p(95)<500'], // 95% < 500ms
  }
};

// Setup function to authenticate and get JWT token
export function setup() {
  // Login to get authentication token
  const loginPayload = JSON.stringify({
    name: `loadtest_user_${randomString(6)}`,
    password: 'testpassword'
  });
  
  const loginParams = {
    headers: {
      'Content-Type': 'application/json',
    },
  };
  
  const loginRes = http.post('http://localhost:8080/api/login', loginPayload, loginParams);
  
  if (loginRes.status !== 200) {
    console.error('Login failed:', loginRes.body);
    return null;
  }
  
  const loginData = loginRes.json();
  const token = loginData.data.token;
  
  return {
    token: token,
    userId: loginData.data.user.uid
  };
}

export default function (data) {
  if (!data || !data.token) {
    console.error('No authentication token available');
    return;
  }
  
  const roomName = `TestRoom_${randomString(8)}`;
  const payload = JSON.stringify({
    name: roomName,
    roomType: 'TEXAS_HOLDEM',
    maxPlayers: 8
  });
  
  const params = {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${data.token}`,
    },
  };
  
  const res = http.post('http://localhost:8080/api/rooms', payload, params);
  
  check(res, {
    'Room created successfully': (r) => r.status === 201,
    'Response time acceptable': (r) => r.timings.duration < 500,
  });
  
  sleep(1);
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

**k6 Implementation**:
```javascript
import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomIntBetween, randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export const options = {
  stages: [
    { duration: '2m', target: 100 },  // Ramp-up to 100 RPS
    { duration: '8m', target: 100 },  // Maintain 100 RPS
    { duration: '2m', target: 0 },    // Ramp-down
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<800'],
  }
};

// Pre-create rooms (this would be done in setup)
const roomIds = Array(500).fill().map((_, i) => `room_${i}`);

// Setup function to authenticate and get JWT token
export function setup() {
  // Login to get authentication token
  const loginPayload = JSON.stringify({
    name: `loadtest_user_${randomString(6)}`,
    password: 'testpassword'
  });
  
  const loginParams = {
    headers: {
      'Content-Type': 'application/json',
    },
  };
  
  const loginRes = http.post('http://localhost:8080/api/login', loginPayload, loginParams);
  
  if (loginRes.status !== 200) {
    console.error('Login failed:', loginRes.body);
    return null;
  }
  
  const loginData = loginRes.json();
  const token = loginData.data.token;
  
  return {
    token: token,
    userId: loginData.data.user.uid
  };
}

export default function (data) {
  if (!data || !data.token) {
    console.error('No authentication token available');
    return;
  }
  
  const roomId = roomIds[randomIntBetween(0, 499)];
  const playerName = `Bot_${randomString(8)}`;
  
  const payload = JSON.stringify({
    roomId: roomId,
    playerName: playerName,
    startingChips: 1000,
    botStrategy: 'ALWAYS_CALL'
  });
  
  const params = {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${data.token}`,
    },
  };
  
  const res = http.post('http://localhost:8080/api/rooms/join', payload, params);
  
  check(res, {
    'Player joined successfully': (r) => r.status === 200,
    'Response time acceptable': (r) => r.timings.duration < 800,
  });
  
  sleep(1);
}
```

### Scenario 3: Complete Gameplay Simulation
**Objective**: Test the full gameplay lifecycle from login to betting under load

**Test Parameters**:
- Test users: 200
- Rooms per user: 1
- Players per room: 8 (including creator)
- Hands per room: 50
- Bot strategies:
  - 70%: Always call
  - 20%: Random raise (2-5x current bet)
  - 10%: Random fold based on hand strength

**Success Criteria**:
- All users successfully authenticate
- All rooms created successfully
- All players join their respective rooms
- Games start without errors
- All betting actions complete successfully
- No deadlocks or race conditions
- Consistent game state throughout
- Acceptable response times (<500ms for actions, <800ms for room operations)

**k6 Implementation**:
```javascript
import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { randomIntBetween, randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export const options = {
  stages: [
    { duration: '5m', target: 50 },   // Ramp-up to 50 VUs
    { duration: '15m', target: 50 },  // Maintain 50 VUs for gameplay
    { duration: '5m', target: 0 },    // Ramp-down
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<1000'], // Slightly higher threshold for full workflow
  }
};

// Global state for the test
const testState = {
  users: [],
  rooms: new Map(), // roomId -> { creatorId, players: [] }
  currentHand: 0,
  maxHands: 50
};

export function setup() {
  console.log('Setting up complete gameplay simulation...');
  
  // Pre-create test users and their authentication tokens
  for (let i = 0; i < 200; i++) {
    const userId = i;
    const username = `loadtest_user_${userId}`;
    const password = 'testpassword';
    
    // Login to get authentication token
    const loginPayload = JSON.stringify({
      name: username,
      password: password
    });
    
    const loginParams = {
      headers: {
        'Content-Type': 'application/json',
      },
    };
    
    const loginRes = http.post('http://localhost:8080/api/login', loginPayload, loginParams);
    
    if (loginRes.status === 200) {
      const loginData = loginRes.json();
      testState.users.push({
        userId: userId,
        username: username,
        token: loginData.data.token,
        playerId: loginData.data.user.uid
      });
    } else {
      console.error(`Login failed for user ${username}:`, loginRes.body);
    }
  }
  
  console.log(`Successfully authenticated ${testState.users.length} users`);
  return testState;
}

export default function (data) {
  if (!data || data.users.length === 0) {
    console.error('No authenticated users available');
    return;
  }
  
  // Select a random user for this iteration
  const userIndex = randomIntBetween(0, data.users.length - 1);
  const user = data.users[userIndex];
  
  // Execute the complete gameplay workflow
  executeGameplayWorkflow(user, data);
  
  // Sleep to simulate realistic player behavior
  sleep(randomIntBetween(1, 3));
}

function executeGameplayWorkflow(user, data) {
  group('Complete Gameplay Workflow', function () {
    
    // Step 1: Create a room (if user doesn't have one yet)
    const userRoom = Array.from(data.rooms.values()).find(room => room.creatorId === user.userId);
    
    if (!userRoom) {
      const roomId = createRoom(user);
      if (roomId) {
        data.rooms.set(roomId, {
          creatorId: user.userId,
          players: [user.playerId],
          status: 'LOBBY',
          currentHand: 0
        });
      }
    }
    
    // Step 2: Find or join a room
    const roomId = findUserRoom(user.userId, data);
    if (!roomId) {
      console.log(`User ${user.userId} could not find or create a room`);
      return;
    }
    
    const room = data.rooms.get(roomId);
    
    // Step 3: If room is in LOBBY state, try to start the game
    if (room.status === 'LOBBY' && room.players.length >= 2) {
      const success = startGame(user, roomId, data);
      if (success) {
        room.status = 'IN_PROGRESS';
      }
    }
    
    // Step 4: If game is in progress, perform betting actions
    if (room.status === 'IN_PROGRESS') {
      performBettingAction(user, roomId, data);
      
      // Increment hand counter and check if we've reached max hands
      room.currentHand++;
      if (room.currentHand >= data.maxHands) {
        room.status = 'COMPLETED';
      }
    }
    
  });
}

function createRoom(user) {
  group('Room Creation', function () {
    const roomName = `LoadTestRoom_${user.userId}_${randomString(4)}`;
    const payload = JSON.stringify({
      name: roomName,
      roomType: 'TEXAS_HOLDEM',
      maxPlayers: 8
    });
    
    const params = {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${user.token}`,
      },
    };
    
    const res = http.post('http://localhost:8080/api/rooms', payload, params);
    
    const success = check(res, {
      'Room created successfully': (r) => r.status === 201,
      'Response time acceptable': (r) => r.timings.duration < 800,
    });
    
    if (success && res.status === 201) {
      const roomData = res.json().data;
      console.log(`User ${user.userId} created room ${roomData.roomid}`);
      return roomData.roomid;
    } else {
      console.error(`Room creation failed for user ${user.userId}:`, res.body);
      return null;
    }
  });
}

function findUserRoom(userId, data) {
  // Look for a room created by this user
  for (const [roomId, room] of data.rooms) {
    if (room.creatorId === userId) {
      return roomId;
    }
  }
  
  // If no room found, look for any room with available slots
  for (const [roomId, room] of data.rooms) {
    if (room.players.length < 8 && room.status === 'LOBBY') {
      return roomId;
    }
  }
  
  return null;
}

function joinRoom(user, roomId, data) {
  group('Room Joining', function () {
    const payload = JSON.stringify({
      roomId: roomId,
      playerName: user.username,
      startingChips: 1000,
      botStrategy: getRandomStrategy()
    });
    
    const params = {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${user.token}`,
      },
    };
    
    const res = http.post(`http://localhost:8080/api/rooms/${roomId}/join`, payload, params);
    
    const success = check(res, {
      'Player joined successfully': (r) => r.status === 200,
      'Response time acceptable': (r) => r.timings.duration < 800,
    });
    
    if (success && res.status === 200) {
      const room = data.rooms.get(roomId);
      if (room && !room.players.includes(user.playerId)) {
        room.players.push(user.playerId);
        console.log(`User ${user.userId} joined room ${roomId}, now has ${room.players.length} players`);
      }
      return true;
    } else {
      console.error(`Join failed for user ${user.userId} to room ${roomId}:`, res.body);
      return false;
    }
  });
}

function startGame(user, roomId, data) {
  group('Game Start', function () {
    // Use the game action endpoint with sbot command to start the game
    const params = {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${user.token}`,
      },
    };
    
    // The sbot command starts the game (deals initial cards)
    const res = http.post(`http://localhost:8080/api/game?cmd=sbot`, JSON.stringify({}), params);
    
    const success = check(res, {
      'Game started successfully': (r) => r.status === 200,
      'Response time acceptable': (r) => r.timings.duration < 800,
    });
    
    if (success) {
      console.log(`User ${user.userId} started game in room ${roomId}`);
      return true;
    } else {
      console.error(`Game start failed for user ${user.userId} in room ${roomId}:`, res.body);
      return false;
    }
  });
}

function performBettingAction(user, roomId, data) {
  group('Betting Action', function () {
    const actionType = getRandomAction();
    
    // Map action types to game commands
    const commandMap = {
      'CALL': '3',      // CMD_FOLLOW_BET
      'RAISE': '2',     // CMD_ADD_BET
      'FOLD': '4'       // CMD_DROP_CARD
    };
    
    const cmd = commandMap[actionType];
    const amount = actionType === 'RAISE' ? randomIntBetween(20, 100) : 0;
    
    const params = {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${user.token}`,
      },
    };
    
    // Use the game action endpoint with the appropriate command
    const url = `http://localhost:8080/api/game?cmd=${cmd}`;
    const payload = amount > 0 ? JSON.stringify({ amount: amount }) : JSON.stringify({});
    
    const res = http.post(url, payload, params);
    
    check(res, {
      'Action processed successfully': (r) => r.status === 200,
      'Response time acceptable': (r) => r.timings.duration < 500,
    });
    
    if (res.status !== 200) {
      console.error(`Action failed for user ${user.userId} in room ${roomId}:`, res.body);
    }
  });
}

function getRandomStrategy() {
  const rand = randomIntBetween(1, 10);
  if (rand <= 7) return 'ALWAYS_CALL';
  if (rand <= 9) return 'RANDOM_RAISE';
  return 'SELECTIVE_FOLD';
}

function getRandomAction() {
  const rand = randomIntBetween(1, 10);
  if (rand <= 7) return 'CALL';
  if (rand <= 9) return 'RAISE';
  return 'FOLD';
}

export function teardown(data) {
  console.log('Cleaning up test data...');
  
  // Cleanup: Remove test rooms
  for (const roomId of data.rooms.keys()) {
    try {
      const user = data.users[0]; // Use first user for cleanup
      if (user && user.token) {
        const params = {
          headers: {
            'Authorization': `Bearer ${user.token}`,
          },
        };
        http.del(`http://localhost:8080/api/rooms/${roomId}`, params);
      }
    } catch (e) {
      console.error(`Failed to cleanup room ${roomId}:`, e);
    }
  }
  
  console.log('Test completed successfully');
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

### k6 Test Execution

k6 provides a powerful and flexible way to execute load tests with built-in metrics collection and reporting.

**Basic Execution Command**:
```bash
k6 run --vus 50 --duration 30s script.js
```

**Advanced Execution with Cloud Results**:
```bash
k6 cloud --stage 1m:50 --stage 4m:50 --stage 1m:0 script.js
```

### Test Runner Architecture with k6

```javascript
// load_test_runner.js
import { execSync } from 'child_process';
import fs from 'fs';

class LoadTestRunner {
  constructor(config) {
    this.config = config;
    this.results = [];
  }
  
  async executeTest() {
    try {
      // Phase 1: Setup
      await this.setupPhase();
      
      // Phase 2: Ramp-up
      await this.rampUpPhase();
      
      // Phase 3: Steady state
      await this.steadyStatePhase();
      
      // Phase 4: Ramp-down
      await this.rampDownPhase();
      
      // Generate report
      this.generateTestReport();
      
    } catch (error) {
      console.error('Test failed:', error);
      this.recordError(error);
    } finally {
      this.cleanup();
    }
  }
  
  async setupPhase() {
    console.log('Setting up test environment...');
    // Initialize database, create test rooms, etc.
    execSync('node setup_test_data.js');
  }
  
  async rampUpPhase() {
    console.log('Starting ramp-up phase...');
    
    // Execute k6 with increasing load
    for (let stage = 1; stage <= 5; stage++) {
      const targetVUs = stage * 20;
      console.log(`Ramping up to ${targetVUs} VUs...`);
      
      execSync(`k6 run --vus ${targetVUs} --duration 2m --out json=results/stage_${stage}.json room_creation_test.js`);
      
      // Collect and store results
      this.collectResults(`results/stage_${stage}.json`);
      
      // Wait before next stage
      await new Promise(resolve => setTimeout(resolve, 30000));
    }
  }
  
  async steadyStatePhase() {
    console.log('Entering steady state phase...');
    
    // Run main test at peak load
    execSync(`k6 run --stage 5m:200 --stage 30m:200 --stage 5m:0 --out json=results/steady_state.json gameplay_test.js`);
    
    this.collectResults('results/steady_state.json');
  }
  
  async rampDownPhase() {
    console.log('Starting ramp-down phase...');
    
    // Gradually reduce load
    for (let stage = 5; stage >= 1; stage--) {
      const targetVUs = stage * 20;
      console.log(`Ramping down to ${targetVUs} VUs...`);
      
      execSync(`k6 run --vus ${targetVUs} --duration 2m --out json=results/rampdown_${stage}.json room_creation_test.js`);
      
      this.collectResults(`results/rampdown_${stage}.json`);
      
      await new Promise(resolve => setTimeout(resolve, 30000));
    }
  }
  
  collectResults(filePath) {
    const data = fs.readFileSync(filePath, 'utf8');
    const results = JSON.parse(data);
    this.results.push({ file: filePath, data: results });
  }
  
  generateTestReport() {
    console.log('Generating test report...');
    // Process collected results and generate HTML/PDF report
    execSync('node generate_report.js');
  }
  
  recordError(error) {
    // Log error details
    fs.appendFileSync('test_errors.log', `${new Date().toISOString()} - ${error.message}\n`);
  }
  
  cleanup() {
    console.log('Cleaning up test environment...');
    execSync('node cleanup_test_data.js');
  }
}

// Usage
const runner = new LoadTestRunner({
  roomCount: 200,
  playersPerRoom: 8,
  testDuration: '1h'
});

runner.executeTest();
```

## Key Metrics to Monitor

### k6 Built-in Metrics
k6 automatically collects comprehensive metrics that map to our monitoring needs:

**HTTP Metrics**:
- `http_req_duration`: Request duration (ms)
- `http_reqs`: Total requests
- `http_req_failed`: Failed requests
- `http_req_receiving`: Time spent receiving response
- `http_req_sending`: Time spent sending request
- `http_req_waiting`: Time spent waiting for response

**System Metrics**:
- `vus`: Current virtual users
- `vus_max`: Maximum virtual users
- `iterations`: Completed iterations
- `data_received`: Data received (bytes)
- `data_sent`: Data sent (bytes)

### Custom Metrics
You can add custom metrics for game-specific monitoring:

```javascript
import { Trend, Rate, Counter } from 'k6/metrics';

// Custom metrics
const handCompletionTime = new Trend('hand_completion_time', true);
const actionProcessingTime = new Trend('action_processing_time', true);
const roomStateUpdates = new Counter('room_state_updates');
const playerJoinErrors = new Rate('player_join_errors');

// Usage in test
export default function () {
  const startTime = Date.now();
  
  // Perform game action
  const res = http.post('http://localhost:8080/api/game/action', payload);
  
  const duration = Date.now() - startTime;
  actionProcessingTime.add(duration);
  
  if (res.status !== 200) {
    playerJoinErrors.add(1);
  }
}
```

### Database Metrics (via k6)
Monitor database performance through API endpoints:

```javascript
// Database health check endpoint
export function checkDatabaseHealth() {
  const res = http.get('http://localhost:8080/api/db/health');
  
  check(res, {
    'Database healthy': (r) => r.json().status === 'healthy',
    'Connection pool OK': (r) => r.json().poolUsage < 0.9,
  });
}
```

### Game-Specific Metrics
- **Hand Completion Rate**: Hands processed per second
- **Action Processing Time**: Time to process player actions (ms)
- **Room State Updates**: Frequency and duration of state changes
- **Player Join/Leave Times**: Duration of join/leave operations (ms)
- **Concurrent Games**: Number of active games simultaneously

### Error Metrics
- **Failed Operations**: Count and types of failures
- **Timeouts**: Operations exceeding time limits
- **Deadlocks**: Thread contention issues detected
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

### Integration with CI/CD using k6

k6 integrates seamlessly with CI/CD pipelines for automated load testing:

**GitHub Actions Example**:
```yaml
name: Load Testing
on: [push, pull_request]

jobs:
  smoke-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Install k6
        run: sudo apt-get install -y k6
      - name: Run smoke test
        run: k6 run --vus 10 --duration 1m tests/smoke_test.js

  nightly-test:
    runs-on: ubuntu-latest
    if: github.event_name == 'schedule'
    steps:
      - uses: actions/checkout@v2
      - name: Install k6
        run: sudo apt-get install -y k6
      - name: Run nightly load test
        run: k6 run --stage 2m:50 --stage 5m:50 tests/nightly_test.js
```

**Jenkins Pipeline Example**:
```groovy
pipeline {
    agent any
    
    stages {
        stage('Smoke Test') {
            steps {
                sh 'k6 run --vus 20 --duration 2m tests/smoke_test.js'
            }
        }
        
        stage('Load Test') {
            when {
                branch 'main'
            }
            steps {
                sh 'k6 cloud --stage 5m:100 --stage 10m:100 tests/load_test.js'
            }
        }
    }
    
    post {
        always {
            junit '**/test-results/*.xml'
            archiveArtifacts artifacts: 'results/**', fingerprint: true
        }
    }
}
```

### Automated Test Suite with k6

```javascript
// tests/automated_suite.js
import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomIntBetween, randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

// Test configuration
const testConfig = {
    smoke: { vus: 10, duration: '1m', rooms: 5, players: 3, hands: 3 },
    nightly: { vus: 50, duration: '5m', rooms: 20, players: 6, hands: 20 },
    preprod: { vus: 100, duration: '10m', rooms: 50, players: 8, hands: 50 }
};

// Get test type from environment variable
const testType = __ENV.TEST_TYPE || 'smoke';
const config = testConfig[testType];

export const options = {
  vus: config.vus,
  duration: config.duration,
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<800'],
  }
};

// Pre-create test rooms
const roomIds = [];
for (let i = 0; i < config.rooms; i++) {
    roomIds.push(`test_room_${randomString(4)}`);
}

export function setup() {
    // Create rooms
    roomIds.forEach(roomId => {
        const payload = JSON.stringify({
            name: roomId,
            roomType: 'TEXAS_HOLDEM',
            maxPlayers: config.players
        });
        
        http.post('http://localhost:8080/api/rooms', payload, {
            headers: { 'Content-Type': 'application/json' }
        });
    });
}

export default function () {
    const roomId = roomIds[randomIntBetween(0, config.rooms - 1)];
    
    // Simulate player joining and gameplay
    const playerName = `Bot_${randomString(6)}`;
    
    // Join room
    const joinPayload = JSON.stringify({
        roomId: roomId,
        playerName: playerName,
        startingChips: 1000,
        botStrategy: 'ALWAYS_CALL'
    });
    
    const joinRes = http.post('http://localhost:8080/api/rooms/join', joinPayload, {
        headers: { 'Content-Type': 'application/json' }
    });
    
    check(joinRes, {
        'Player joined successfully': (r) => r.status === 200,
    });
    
    // Simulate gameplay actions
    if (joinRes.status === 200) {
        const playerId = joinRes.json().playerId;
        
        for (let hand = 0; hand < config.hands; hand++) {
            const actionType = randomIntBetween(1, 10) <= 7 ? 'CALL' : 'RAISE';
            const actionPayload = JSON.stringify({
                playerId: playerId,
                action: actionType,
                amount: actionType === 'RAISE' ? randomIntBetween(20, 50) : 0
            });
            
            const actionRes = http.post(`http://localhost:8080/api/rooms/${roomId}/actions`, actionPayload, {
                headers: { 'Content-Type': 'application/json' }
            });
            
            check(actionRes, {
                'Action processed successfully': (r) => r.status === 200,
            });
            
            sleep(0.5);
        }
    }
}

export function teardown(data) {
    // Cleanup - remove test rooms
    roomIds.forEach(roomId => {
        http.delete(`http://localhost:8080/api/rooms/${roomId}`);
    });
}
```

**Running Different Test Types**:
```bash
# Smoke test
k6 run --env TEST_TYPE=smoke tests/automated_suite.js

# Nightly test
k6 run --env TEST_TYPE=nightly tests/automated_suite.js

# Pre-production test
k6 run --env TEST_TYPE=preprod tests/automated_suite.js
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

### k6 Installation and Setup

**Install k6**:
```bash
# macOS
brew install k6

# Linux (Debian/Ubuntu)
sudo apt-get install -y k6

# Windows (via Chocolatey)
choco install k6
```

**Verify Installation**:
```bash
k6 version
```

### Test Configuration Examples with k6

**Basic Room Creation Test**:
```javascript
import http from 'k6/http';
import { check } from 'k6';

export const options = {
  vus: 10,
  duration: '30s',
  thresholds: {
    http_req_duration: ['p(95)<500'],
  }
};

export default function () {
  const payload = JSON.stringify({
    name: 'TestRoom_' + Math.random().toString(36).substring(2, 8),
    roomType: 'TEXAS_HOLDEM',
    maxPlayers: 8
  });
  
  const res = http.post('http://localhost:8080/api/rooms', payload, {
    headers: { 'Content-Type': 'application/json' }
  });
  
  check(res, {
    'Room created': (r) => r.status === 201,
  });
}
```

**Advanced Gameplay Simulation**:
```javascript
import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export const options = {
  stages: [
    { duration: '2m', target: 50 },
    { duration: '5m', target: 50 },
    { duration: '2m', target: 0 }
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<800'],
  }
};

// Game state simulation
const gameState = {
  rooms: new Map(),
  players: new Map()
};

export function setup() {
  // Initialize game state
  for (let i = 0; i < 20; i++) {
    const roomId = `room_${i}`;
    gameState.rooms.set(roomId, { players: [] });
    
    // Create room
    http.post('http://localhost:8080/api/rooms', JSON.stringify({
      name: roomId,
      roomType: 'TEXAS_HOLDEM',
      maxPlayers: 8
    }), { headers: { 'Content-Type': 'application/json' } });
  }
}

export default function () {
  const roomId = `room_${randomIntBetween(0, 19)}`;
  const room = gameState.rooms.get(roomId);
  
  if (room.players.length < 8) {
    // Join player
    const playerName = `player_${Math.random().toString(36).substring(2, 8)}`;
    const joinRes = http.post('http://localhost:8080/api/rooms/join', JSON.stringify({
      roomId: roomId,
      playerName: playerName,
      startingChips: 1000
    }), { headers: { 'Content-Type': 'application/json' } });
    
    if (joinRes.status === 200) {
      const playerId = joinRes.json().playerId;
      room.players.push(playerId);
      gameState.players.set(playerId, { roomId: roomId });
    }
  } else {
    // Perform game action
    const playerId = room.players[randomIntBetween(0, 7)];
    const actionType = randomIntBetween(1, 10) <= 7 ? 'CALL' : 'RAISE';
    
    const actionRes = http.post(`http://localhost:8080/api/rooms/${roomId}/actions`, JSON.stringify({
      playerId: playerId,
      action: actionType,
      amount: actionType === 'RAISE' ? randomIntBetween(20, 100) : 0
    }), { headers: { 'Content-Type': 'application/json' } });
    
    check(actionRes, {
      'Action successful': (r) => r.status === 200,
    });
  }
  
  sleep(1);
}

export function teardown(data) {
  // Cleanup
  gameState.rooms.forEach((room, roomId) => {
    http.delete(`http://localhost:8080/api/rooms/${roomId}`);
  });
}
```

### Useful Tools and Resources

**Load Generation**:
- **k6**: Modern load testing tool (recommended)
- **JMeter**: Traditional load testing tool
- **Gatling**: Scala-based load testing
- **Locust**: Python-based load testing

**Monitoring**:
- **Prometheus**: Time-series monitoring
- **Grafana**: Visualization dashboard
- **Datadog**: Cloud monitoring platform
- **k6 Cloud**: Built-in k6 results analysis

**Profiling**:
- **VisualVM**: Java profiling tool
- **YourKit**: Commercial Java profiler
- **JProfiler**: Advanced Java profiler
- **Async Profiler**: Low-overhead profiler

**Database Tools**:
- **pgAdmin**: PostgreSQL management
- **MySQL Workbench**: MySQL management
- **DBeaver**: Universal database tool
- **TablePlus**: Modern database GUI

**k6 Resources**:
- **Documentation**: https://k6.io/docs/
- **Examples**: https://k6.io/docs/examples/
- **GitHub**: https://github.com/grafana/k6
- **Community**: https://community.k6.io/

This comprehensive load testing approach will help ensure the Puker game server can handle production-scale traffic while maintaining performance and stability.