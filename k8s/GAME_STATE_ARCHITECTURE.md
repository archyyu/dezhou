# 🏗️ TexasHolder Poker - Game State Architecture

## 🎯 Problem Analysis

### Current Issues
1. **In-Memory State Loss**: Game info and player info stored in memory → lost on server restart
2. **Performance Concern**: Storing all game state in MySQL would be slow for real-time gameplay
3. **Session Persistence**: Users lose game state if server restarts
4. **No Game History**: Cannot track completed games or player statistics

### Current State
- ✅ User accounts persist in MySQL
- ✅ Room types persist in MySQL
- ❌ Active games lost on restart
- ❌ Player sessions lost on restart
- ❌ No game history tracking

## 🏗️ Proposed Hybrid Architecture

### Hybrid Storage Strategy

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    TexasHolder Poker - Hybrid Architecture                   │
├─────────────────────────┬─────────────────────────┬─────────────────────────┤
│  MySQL (Persistent)     │  Redis (In-Memory)      │  Application Logic      │
├─────────────────────────┼─────────────────────────┼─────────────────────────┤
│  • User Accounts        │  • Active Games         │  • Game Engine          │
│  • Room Types           │  • Player Sessions      │  • Real-time Processing │
│  • Game History         │  • Chat Messages        │  • WebSocket Connections│
│  • Player Stats         │  • Current Hands        │  • Business Logic       │
│  • Transactions         │  • Active Bets          │                         │
└─────────────────────────┴─────────────────────────┴─────────────────────────┘
```

## 📊 Data Storage Matrix

| Data Type | Storage | Persistence | Read/Write | Purpose |
|-----------|---------|-------------|------------|---------|
| User Accounts | MySQL | ✅ Permanent | Low | Authentication, profiles |
| Room Types | MySQL | ✅ Permanent | Low | Game configuration |
| Active Games | Redis | ❌ Temporary | High | Real-time game state |
| Game History | MySQL | ✅ Permanent | Medium | Audit, analytics |
| Player Sessions | Redis | ❌ Temporary | High | Active player tracking |
| Chat Messages | Redis | ❌ Temporary | High | Real-time communication |
| Player Stats | MySQL | ✅ Permanent | Medium | Leaderboards, achievements |
| Transactions | MySQL | ✅ Permanent | Medium | Financial tracking |

## 🗃️ Database Schema

### 1. Existing Tables (Persistent)

#### dezhou_user
```sql
CREATE TABLE dezhou_user (
    uid int(11) auto_increment,
    account varchar(50) default '',
    password varchar(20) default '',
    roommoney int(11) default 0,
    allmoney int(11) default 100000,
    exprience int default 0,
    gold int default 0,
    mobile varchar(20),
    level int default 0,
    sex varchar(10) default 'male',
    address varchar(50) default '',
    regtime varchar(30) default '',
    birthday varchar(30) default '',
    logintime varchar(20) default '',
    primary key(uid),
    UNIQUE KEY uidx_account (account)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

#### dezhou_room
```sql
CREATE TABLE dezhou_room (
    id int(11) auto_increment,
    showname varchar(50) default '',
    name varchar(50) default '',
    bbet int(11) default 0,
    sbet int(11) default 0,
    maxbuy int(11) default 0,
    minbuy int(11) default 0,
    roomtype varchar(10) default '',
    primary key(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

### 2. New Tables (Persistent)

#### dezhou_game_history
```sql
CREATE TABLE dezhou_game_history (
    id BIGINT auto_increment,
    room_id VARCHAR(50) NOT NULL,
    game_start TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    game_end TIMESTAMP NULL,
    players JSON,
    winner_id INT,
    pot_amount INT,
    hands JSON,
    status VARCHAR(20) DEFAULT 'active',
    PRIMARY KEY(id),
    INDEX idx_room_id (room_id),
    INDEX idx_status (status)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

#### dezhou_player_session
```sql
CREATE TABLE dezhou_player_session (
    id BIGINT auto_increment,
    user_id INT NOT NULL,
    session_id VARCHAR(100) NOT NULL,
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    logout_time TIMESTAMP NULL,
    ip_address VARCHAR(50),
    device_info VARCHAR(255),
    status VARCHAR(20) DEFAULT 'active',
    PRIMARY KEY(id),
    INDEX idx_user_id (user_id),
    INDEX idx_session_id (session_id),
    INDEX idx_status (status)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

#### dezhou_active_game
```sql
CREATE TABLE dezhou_active_game (
    id BIGINT auto_increment,
    room_id VARCHAR(50) NOT NULL UNIQUE,
    room_type VARCHAR(50) NOT NULL,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    current_players INT DEFAULT 0,
    max_players INT DEFAULT 9,
    status VARCHAR(20) DEFAULT 'waiting',
    current_pot INT DEFAULT 0,
    current_bet INT DEFAULT 0,
    PRIMARY KEY(id),
    INDEX idx_room_type (room_type),
    INDEX idx_status (status)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

## 🔧 Redis Data Structures

### 1. Active Game State
```
Key: game:{room_id}
Type: Hash
Fields:
  - status: "waiting"|"playing"|"completed"
  - current_player: user_id
  - current_bet: amount
  - pot: total_pot
  - community_cards: ["Ah", "Kd", "Qc"]
  - player_actions: JSON
  - last_action_time: timestamp
```

### 2. Player Hands
```
Key: game:{room_id}:player:{user_id}
Type: Hash
Fields:
  - hand: ["As", "Ks"]
  - chips: amount
  - status: "active"|"folded"|"all_in"
  - bet_amount: current_bet
  - position: "dealer"|"small_blind"|"big_blind"|"player"
```

### 3. Player Sessions
```
Key: session:{session_id}
Type: Hash
Fields:
  - user_id: user_id
  - username: username
  - login_time: timestamp
  - last_activity: timestamp
  - current_room: room_id
  - status: "online"|"away"|"offline"
  - ip_address: ip
```

### 4. Chat Messages
```
Key: chat:{room_id}
Type: List (capped at 100 messages)
Items: JSON messages with timestamp, user, content
```

## 🎮 Game State Management Flow

### 1. Game Creation
```
User → Frontend → Backend → Redis
                     ↓
               MySQL (dezhou_active_game)
```

### 2. Game Play
```
Redis (In-Memory) ←→ Backend ←→ Frontend ←→ User
       ↓
MySQL (Periodic Snapshots)
```

### 3. Game Completion
```
Redis → Backend → MySQL (dezhou_game_history)
       ↓
Redis (Cleanup)
```

### 4. Server Restart Recovery
```
MySQL (dezhou_active_game) → Backend → Redis (Reconstruct)
```

## 🚀 Implementation Strategy

### 1. Add Redis to Kubernetes

#### redis-deployment.yaml
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: redis
  namespace: texasholder
spec:
  replicas: 1
  selector:
    matchLabels:
      app: redis
  template:
    metadata:
      labels:
        app: redis
    spec:
      containers:
      - name: redis
        image: redis:7-alpine
        ports:
        - containerPort: 6379
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        livenessProbe:
          exec:
            command: ["redis-cli", "ping"]
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          exec:
            command: ["redis-cli", "ping"]
          initialDelaySeconds: 5
          periodSeconds: 5
```

#### redis-service.yaml
```yaml
apiVersion: v1
kind: Service
metadata:
  name: redis
  namespace: texasholder
spec:
  type: ClusterIP
  ports:
  - port: 6379
    targetPort: 6379
  selector:
    app: redis
```

### 2. Update Backend Configuration

#### application.yml additions
```yaml
spring:
  redis:
    host: redis
    port: 6379
    timeout: 5000ms
  
# Game state configuration
game:
  state:
    snapshot-interval: 300000 # 5 minutes
    max-inactive-time: 1800000 # 30 minutes
    redis-ttl: 3600 # 1 hour
```

### 3. Game State Service (Java)

```java
@Service
public class GameStateService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private GameHistoryRepository gameHistoryRepository;
    
    @Autowired
    private ActiveGameRepository activeGameRepository;
    
    // Save game state to Redis
    public void saveGameState(String roomId, GameState gameState) {
        String key = "game:" + roomId;
        redisTemplate.opsForHash().putAll(key, objectMapper.convertValue(gameState, Map.class));
        redisTemplate.expire(key, 1, TimeUnit.HOURS);
    }
    
    // Load game state from Redis
    public GameState loadGameState(String roomId) {
        String key = "game:" + roomId;
        Map<Object, Object> data = redisTemplate.opsForHash().entries(key);
        return objectMapper.convertValue(data, GameState.class);
    }
    
    // Periodic snapshot to MySQL
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void snapshotActiveGames() {
        Set<String> gameKeys = redisTemplate.keys("game:*");
        for (String key : gameKeys) {
            String roomId = key.replace("game:", "");
            GameState state = loadGameState(roomId);
            // Save snapshot to MySQL
            gameHistoryRepository.save(convertToHistory(state));
        }
    }
    
    // Recover from server restart
    public void recoverActiveGames() {
        List<ActiveGame> activeGames = activeGameRepository.findByStatus("active");
        for (ActiveGame game : activeGames) {
            // Reconstruct game state in Redis
            GameState state = reconstructGameState(game.getRoomId());
            saveGameState(game.getRoomId(), state);
        }
    }
}
```

### 4. Player Session Management

```java
@Service
public class PlayerSessionService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private PlayerSessionRepository playerSessionRepository;
    
    public void createSession(User user, String sessionId, String ipAddress) {
        // Save to Redis
        String key = "session:" + sessionId;
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("user_id", user.getUid());
        sessionData.put("username", user.getAccount());
        sessionData.put("login_time", System.currentTimeMillis());
        sessionData.put("status", "online");
        sessionData.put("ip_address", ipAddress);
        redisTemplate.opsForHash().putAll(key, sessionData);
        redisTemplate.expire(key, 24, TimeUnit.HOURS);
        
        // Save to MySQL
        PlayerSession session = new PlayerSession();
        session.setUserId(user.getUid());
        session.setSessionId(sessionId);
        session.setIpAddress(ipAddress);
        session.setStatus("active");
        playerSessionRepository.save(session);
    }
    
    public void updateSessionActivity(String sessionId) {
        String key = "session:" + sessionId;
        redisTemplate.opsForHash().put(key, "last_activity", System.currentTimeMillis());
        redisTemplate.expire(key, 24, TimeUnit.HOURS);
    }
    
    public void endSession(String sessionId) {
        String key = "session:" + sessionId;
        redisTemplate.opsForHash().put(key, "status", "offline");
        redisTemplate.opsForHash().put(key, "logout_time", System.currentTimeMillis());
        
        // Update MySQL
        playerSessionRepository.findBySessionId(sessionId)
            .ifPresent(session -> {
                session.setLogoutTime(new Timestamp(System.currentTimeMillis()));
                session.setStatus("inactive");
                playerSessionRepository.save(session);
            });
    }
}
```

## 🔄 Server Restart Recovery Process

### 1. Application Startup Sequence
```
1. Spring Boot starts
2. Database connections initialized
3. Redis connections initialized
4. GameStateService.recoverActiveGames() called
5. PlayerSessionService.recoverActiveSessions() called
6. WebSocket connections re-established
7. Gameplay resumes
```

### 2. Recovery Algorithm
```java
@PostConstruct
public void init() {
    // Recover active games
    gameStateService.recoverActiveGames();
    
    // Recover active sessions
    playerSessionService.recoverActiveSessions();
    
    // Notify players to reconnect
    notificationService.broadcastReconnectRequest();
}
```

### 3. Player Reconnection Flow
```
Frontend → WebSocket Reconnect → Backend → Session Validation
       ↓
Redis (Session Check) → MySQL (User Validation)
       ↓
Game State Recovery → Frontend Update
       ↓
Gameplay Resumes
```

## 📊 Performance Optimization

### 1. Redis Configuration
```yaml
# redis.conf optimizations
maxmemory 512mb
maxmemory-policy allkeys-lru
appendonly yes
appendfsync everysec
```

### 2. Caching Strategy
```
• Active Games: Redis (TTL: 1 hour)
• Player Sessions: Redis (TTL: 24 hours)
• Chat Messages: Redis (List, capped at 100)
• Game History: MySQL (permanent)
• User Data: MySQL (permanent)
```

### 3. Read/Write Patterns
```
High Frequency (Redis):
- Game state updates
- Player actions
- Chat messages
- Session tracking

Medium Frequency (MySQL):
- Game snapshots (every 5 minutes)
- Session logs
- Game history

Low Frequency (MySQL):
- User profile updates
- Room configuration changes
- Financial transactions
```

## 🛡️ Data Consistency Strategy

### 1. Transaction Management
```java
@Transactional
public void completeGame(String roomId, GameResult result) {
    // 1. Save to MySQL (persistent)
    gameHistoryRepository.save(convertToHistory(result));
    
    // 2. Update user balances
    for (PlayerResult player : result.getPlayers()) {
        userRepository.updateBalance(player.getUserId(), 
                                    player.getFinalBalance());
    }
    
    // 3. Clean up Redis
    redisTemplate.delete("game:" + roomId);
    redisTemplate.delete("game:" + roomId + ":*");
    
    // 4. Update active game status
    activeGameRepository.updateStatus(roomId, "completed");
}
```

### 2. Error Handling
```java
try {
    // Save to MySQL
    gameHistoryRepository.save(history);
    
    // Save to Redis
    saveGameState(roomId, gameState);
    
    // Commit
    transactionManager.commit(status);
} catch (Exception e) {
    transactionManager.rollback(status);
    logger.error("Failed to save game state", e);
    // Implement compensation logic
    compensationService.handleGameStateFailure(roomId, gameState);
}
```

### 3. Compensation Mechanisms
```java
@Service
public class CompensationService {
    
    public void handleGameStateFailure(String roomId, GameState state) {
        // 1. Notify players
        notificationService.sendSystemMessage(
            roomId, 
            "Game state save failed. Please reconnect."
        );
        
        // 2. Retry with exponential backoff
        retryService.retry(() -> saveGameState(roomId, state), 
                          3, 1000);
        
        // 3. Fallback to emergency save
        if (!retrySuccessful) {
            emergencySaveToFile(roomId, state);
        }
    }
}
```

## 🎯 Implementation Roadmap

### Phase 1: Infrastructure Setup (Current)
- ✅ MySQL with persistent storage
- ✅ Complete database schema
- ✅ Test data populated
- ❌ Redis deployment (needed)
- ❌ Redis service (needed)

### Phase 2: Backend Integration
- ❌ Redis client configuration
- ❌ Game state service implementation
- ❌ Player session service implementation
- ❌ WebSocket integration
- ❌ Recovery mechanisms

### Phase 3: Frontend Integration
- ❌ WebSocket reconnection logic
- ❌ Session recovery UI
- ❌ Game state synchronization
- ❌ Error handling UI

### Phase 4: Testing & Optimization
- ❌ Server restart testing
- ❌ Load testing
- ❌ Failure scenario testing
- ❌ Performance tuning

## 🧪 Testing Strategy

### 1. Server Restart Test
```bash
# Simulate server restart
kubectl rollout restart deployment backend -n texasholder

# Verify recovery
kubectl logs -l app=backend -n texasholder --tail=50

# Check active games
curl http://localhost:30081/api/v1/game/active
```

### 2. Failure Scenario Test
```bash
# Kill MySQL pod
kubectl delete pod mysql-<pod-id> -n texasholder

# Verify backend handles gracefully
kubectl logs -l app=backend -n texasholder --tail=50

# Check MySQL recovery
kubectl wait --for=condition=ready pod -l app=mysql -n texasholder
```

### 3. Load Test
```bash
# Test with multiple concurrent players
k6 run --vus 50 --duration 30s load-test.js

# Monitor performance
kubectl top pods -n texasholder
```

## 📈 Monitoring & Observability

### 1. Key Metrics
```
• Redis Memory Usage
• MySQL Query Performance
• Game State Save Latency
• Player Session Count
• WebSocket Connection Count
• Error Rates
```

### 2. Alerting Rules
```
• Redis memory > 80%
• MySQL query latency > 100ms
• Game state save failures
• WebSocket disconnections > 10%
• Session recovery failures
```

### 3. Logging Strategy
```
• Game state changes (DEBUG)
• Player actions (INFO)
• Errors and failures (ERROR)
• Recovery events (WARN)
• Performance metrics (INFO)
```

## 🔒 Security Considerations

### 1. Data Protection
```
• Redis password authentication
• MySQL SSL connections
• Session token encryption
• Sensitive data masking
```

### 2. Access Control
```
• Redis network isolation (ClusterIP)
• MySQL firewall rules
• Role-based access control
• Audit logging
```

### 3. Compliance
```
• GDPR data retention policies
• Financial transaction auditing
• Player privacy protection
• Anti-cheating measures
```

## 🎉 Benefits of This Architecture

### 1. Performance
```
✅ Real-time game state in Redis (sub-millisecond response)
✅ Minimal MySQL load during gameplay
✅ Optimized for high concurrency
```

### 2. Reliability
```
✅ Automatic recovery from server restarts
✅ Periodic snapshots to MySQL
✅ Compensation mechanisms for failures
```

### 3. Scalability
```
✅ Horizontal scaling with Redis cluster
✅ Read replicas for MySQL
✅ Stateless backend services
```

### 4. Maintainability
```
✅ Clear separation of concerns
✅ Comprehensive monitoring
✅ Easy debugging and troubleshooting
```

## 🚀 Next Steps

### 1. Immediate Actions
```bash
# Deploy Redis
kubectl apply -f k8s/redis-deployment.yaml
kubectl apply -f k8s/redis-service.yaml

# Update backend configuration
# Implement game state service
# Add recovery mechanisms
```

### 2. Short-Term Development
```
• Implement Redis-based game state management
• Add WebSocket reconnection logic
• Create recovery UI in frontend
• Set up monitoring and alerts
```

### 3. Long-Term Enhancements
```
• Add Redis cluster for high availability
• Implement read replicas for MySQL
• Add caching layer for user data
• Implement distributed tracing
```

## 📚 Additional Resources

- Redis Documentation: https://redis.io/docs/
- Spring Data Redis: https://spring.io/projects/spring-data-redis
- Kubernetes Redis Patterns: https://kubernetes.io/docs/tasks/run-application/run-redis/
- Distributed Systems Design: https://www.oreilly.com/library/view/designing-data-intensive-applications/9781491903063/

## 🎯 Conclusion

This hybrid architecture provides the best of both worlds:
- **Performance**: Real-time gameplay with Redis
- **Reliability**: Persistent storage with MySQL
- **Recovery**: Automatic state reconstruction after restarts
- **Scalability**: Ready for growth and high traffic

The solution addresses all your concerns:
- ✅ No more data loss on server restart
- ✅ Optimal performance with in-memory storage
- ✅ Player sessions persist across restarts
- ✅ Complete game history tracking

Let's implement this architecture to make TexasHolder Poker production-ready! 🚀