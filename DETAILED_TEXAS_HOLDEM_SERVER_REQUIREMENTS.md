# Texas Hold'em Server - Detailed Requirements Documentation

## 🎯 Project Overview

**Project Name**: TexasHolder Poker Server (Texas Hold'em Implementation)
**Type**: Backend Server Application
**Technology Stack**: 
- Spring Boot 3.2.0
- MySQL 8.0+
- MyBatis 3.0.3
- Java 17+
- Maven build system
- RESTful API architecture

**Current Status**: 
- ✅ Core game logic implemented
- ✅ REST API endpoints functional
- ✅ Database integration complete
- ✅ Basic authentication system
- ✅ Room management system
- ✅ Game state management

## 🃏 Core Features - Detailed

### 1. User Management System

#### 1.1 User Registration
- **Endpoint**: `POST /api/v1/auth/register`
- **Requirements**:
  - Username: 4-20 characters, alphanumeric
  - Password: 6-20 characters, with complexity requirements
  - Email validation
  - Unique username enforcement
  - Password hashing (BCrypt recommended)

#### 1.2 User Authentication
- **Endpoint**: `POST /api/v1/auth/login`
- **Requirements**:
  - JWT token generation
  - Session management
  - Token expiration (30-60 minutes)
  - Refresh token support
  - Rate limiting for login attempts

#### 1.3 User Profile Management
- **Endpoints**:
  - `GET /api/v1/user/{id}` - Get profile
  - `PUT /api/v1/user/{id}` - Update profile
  - `GET /api/v1/user/{id}/stats` - Get statistics

**Profile Fields**:
```json
{
  "uid": 123,
  "account": "username",
  "email": "user@example.com",
  "allmoney": 100000,
  "roommoney": 5000,
  "level": 5,
  "exprience": 2500,
  "gamesPlayed": 150,
  "gamesWon": 45,
  "winRate": 30.0,
  "biggestWin": 15000,
  "biggestLoss": 5000
}
```

### 2. Room Management System

#### 2.1 Room Types
- **Public Rooms**: Open to all players
- **Private Rooms**: Password protected
- **Tournament Rooms**: Scheduled events
- **VIP Rooms**: High-stakes games

#### 2.2 Room Configuration
```json
{
  "name": "beginner",
  "showname": "Beginner Table",
  "bbet": 20,        // Big blind amount
  "sbet": 10,        // Small blind amount
  "maxbuy": 2000,    // Maximum buy-in
  "minbuy": 200,     // Minimum buy-in
  "roomtype": "public",
  "maxPlayers": 9,   // Maximum players per table
  "timeout": 30      // Action timeout in seconds
}
```

#### 2.3 Room States
- `WAITING`: Waiting for players
- `READY`: Enough players, game about to start
- `PLAYING`: Game in progress
- `FINISHED`: Game completed
- `CLOSED`: Room closed

### 3. Game Logic - Detailed

#### 3.1 Texas Hold'em Rules
- **Deck**: Standard 52-card deck
- **Players**: 2-9 players per table
- **Objective**: Best 5-card hand using 2 hole cards + 5 community cards
- **Betting Structure**: No-limit Texas Hold'em

#### 3.2 Hand Rankings (High to Low)
1. **Royal Flush**: A, K, Q, J, 10 of same suit
2. **Straight Flush**: 5 consecutive cards of same suit
3. **Four of a Kind**: 4 cards of same rank
4. **Full House**: 3 of a kind + pair
5. **Flush**: 5 cards of same suit
6. **Straight**: 5 consecutive cards
7. **Three of a Kind**: 3 cards of same rank
8. **Two Pair**: Two different pairs
9. **One Pair**: 2 cards of same rank
10. **High Card**: Highest card in hand

#### 3.3 Betting Rounds
1. **Pre-Flop**: After hole cards dealt
2. **Flop**: After first 3 community cards
3. **Turn**: After 4th community card
4. **River**: After 5th community card

#### 3.4 Game State Object
```json
{
  "roomId": "beginner",
  "gameId": "game123",
  "status": "PLAYING",
  "currentHand": 45,
  "currentRound": "FLOP",
  "currentPlayer": "player123",
  "pot": 1500,
  "communityCards": ["Ah", "Kd", "Qc"],
  "players": [
    {
      "userId": "player123",
      "name": "John",
      "chips": 5000,
      "currentBet": 200,
      "cards": ["As", "Ks"],
      "status": "ACTIVE",
      "action": "RAISE",
      "isTurn": true
    }
  ],
  "lastAction": {
    "playerId": "player456",
    "action": "CALL",
    "amount": 200,
    "timestamp": 1234567890
  },
  "history": [
    {"player": "player123", "action": "RAISE", "amount": 200},
    {"player": "player456", "action": "CALL", "amount": 200}
  ]
}
```

### 4. API Endpoints - Complete Specification

#### 4.1 Authentication API

**POST /api/v1/auth/register**
```json
// Request
{
  "username": "newplayer",
  "password": "secure123",
  "email": "player@example.com",
  "name": "John Doe"
}

// Response
{
  "success": true,
  "data": {
    "userId": 123,
    "username": "newplayer",
    "token": "jwt.token.here",
    "expiresIn": 3600
  }
}
```

**POST /api/v1/auth/login**
```json
// Request
{
  "username": "existingplayer",
  "password": "password123"
}

// Response
{
  "success": true,
  "data": {
    "userId": 456,
    "username": "existingplayer",
    "token": "jwt.token.here",
    "expiresIn": 3600,
    "refreshToken": "refresh.token.here"
  }
}
```

#### 4.2 Room API

**GET /api/v1/room/list**
```json
// Response
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "beginner",
      "showname": "Beginner Table",
      "bbet": 20,
      "sbet": 10,
      "maxbuy": 2000,
      "minbuy": 200,
      "roomtype": "public",
      "playerCount": 4,
      "maxPlayers": 9,
      "status": "PLAYING"
    }
  ]
}
```

**POST /api/v1/room/{roomId}/join**
```json
// Request
{
  "userId": 123,
  "buyIn": 1000
}

// Response
{
  "success": true,
  "data": {
    "roomId": "beginner",
    "userId": 123,
    "seat": 1,
    "chips": 1000,
    "status": "JOINED"
  }
}
```

#### 4.3 Game API

**GET /api/v1/game/{roomId}/state**
```json
// Response
{
  "success": true,
  "data": {
    "roomId": "beginner",
    "gameId": "game123",
    "status": "PLAYING",
    "currentHand": 45,
    "currentRound": "FLOP",
    "currentPlayer": "player123",
    "pot": 1500,
    "communityCards": ["Ah", "Kd", "Qc"],
    "players": [
      {
        "userId": "player123",
        "name": "John",
        "chips": 5000,
        "currentBet": 200,
        "hasCards": true,
        "isActive": true,
        "isTurn": true,
        "lastAction": "RAISE"
      }
    ]
  }
}
```

**POST /api/v1/game/{roomId}/actions**
```json
// Request
{
  "userId": 123,
  "action": "RAISE",
  "amount": 400
}

// Response
{
  "success": true,
  "data": {
    "action": "RAISE",
    "amount": 400,
    "newPot": 1900,
    "nextPlayer": "player456",
    "currentRound": "FLOP"
  }
}
```

### 5. Database Schema - Complete

#### 5.1 Users Table (`texasholder_user`)
```sql
CREATE TABLE texasholder_user (
    uid INT AUTO_INCREMENT PRIMARY KEY,
    account VARCHAR(50) DEFAULT '' COMMENT 'Username',
    password VARCHAR(20) DEFAULT '' COMMENT 'Hashed password',
    roommoney INT DEFAULT 0 COMMENT 'Money in current room',
    allmoney INT DEFAULT 100000 COMMENT 'Total money',
    exprience INT DEFAULT 0 COMMENT 'Experience points',
    gold INT DEFAULT 0 COMMENT 'Gold currency',
    mobile VARCHAR(20) COMMENT 'Mobile number',
    level INT DEFAULT 0 COMMENT 'Player level',
    sex VARCHAR(10) DEFAULT 'male' COMMENT 'Gender',
    address VARCHAR(50) DEFAULT '' COMMENT 'Address',
    regtime VARCHAR(30) DEFAULT '' COMMENT 'Registration time',
    birthday VARCHAR(30) DEFAULT '' COMMENT 'Birthday',
    logintime VARCHAR(20) DEFAULT '' COMMENT 'Last login time',
    lastUpdateTime BIGINT DEFAULT 0 COMMENT 'Last update timestamp',
    status TINYINT DEFAULT 1 COMMENT '1=active, 0=inactive',
    avatarUrl VARCHAR(255) COMMENT 'Avatar URL',
    UNIQUE KEY uidx_account (account),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Player information';
```

#### 5.2 Rooms Table (`texasholder_room`)
```sql
CREATE TABLE texasholder_room (
    id INT AUTO_INCREMENT PRIMARY KEY,
    showname VARCHAR(50) DEFAULT '' COMMENT 'Display name',
    name VARCHAR(50) DEFAULT '' COMMENT 'Internal name',
    bbet INT DEFAULT 0 COMMENT 'Big blind amount',
    sbet INT DEFAULT 0 COMMENT 'Small blind amount',
    maxbuy INT DEFAULT 0 COMMENT 'Maximum buy-in',
    minbuy INT DEFAULT 0 COMMENT 'Minimum buy-in',
    roomtype VARCHAR(10) DEFAULT 'public' COMMENT 'Room type',
    maxPlayers TINYINT DEFAULT 9 COMMENT 'Maximum players',
    status VARCHAR(20) DEFAULT 'WAITING' COMMENT 'Room status',
    createdAt BIGINT DEFAULT 0 COMMENT 'Creation timestamp',
    updatedAt BIGINT DEFAULT 0 COMMENT 'Last update timestamp',
    creatorId INT COMMENT 'User ID who created room',
    password VARCHAR(50) COMMENT 'Password for private rooms',
    INDEX idx_name (name),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Game room information';
```

#### 5.3 Game History Table (`texasholder_game_history`)
```sql
CREATE TABLE texasholder_game_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    gameId VARCHAR(50) COMMENT 'Game ID',
    roomId VARCHAR(50) COMMENT 'Room ID',
    handNumber INT COMMENT 'Hand number',
    startTime BIGINT COMMENT 'Start timestamp',
    endTime BIGINT COMMENT 'End timestamp',
    winners TEXT COMMENT 'Winning player IDs',
    potAmount INT COMMENT 'Total pot amount',
    communityCards VARCHAR(50) COMMENT 'Community cards',
    playerCount TINYINT COMMENT 'Number of players',
    createdAt BIGINT DEFAULT 0 COMMENT 'Creation timestamp',
    INDEX idx_gameId (gameId),
    INDEX idx_roomId (roomId),
    INDEX idx_startTime (startTime)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Game history records';
```

### 6. Game Flow - Detailed Process

#### 6.1 Room Creation Process
```
1. Player selects room type (public/private)
2. Player chooses buy-in amount (within min/max limits)
3. Server validates player balance
4. Server creates room record in database
5. Player is seated at the table
6. Room status set to WAITING
7. Room appears in room list
```

#### 6.2 Hand Flow Process
```
PRE-FLOP:
1. Shuffle deck
2. Deal 2 hole cards to each player
3. Post small blind and big blind
4. First betting round (starting with player after big blind)

FLOP:
1. Burn 1 card
2. Deal 3 community cards
3. Second betting round

TURN:
1. Burn 1 card
2. Deal 1 community card
3. Third betting round

RIVER:
1. Burn 1 card
2. Deal 1 community card
3. Final betting round

SHOWDOWN:
1. Remaining players show cards
2. Determine best hand
3. Award pot to winner(s)
4. Update player statistics
5. Record game history
```

#### 6.3 Betting Round Process
```
1. Determine first player to act
2. Set current bet to big blind amount
3. For each player in turn:
   a. Player chooses action (fold, call, raise)
   b. Validate action against game rules
   c. Update pot and player chips
   d. Record action in history
4. Continue until all players have:
   - Matched the highest bet
   - Folded
5. Proceed to next round or showdown
```

### 7. Error Handling

#### 7.1 Common Error Codes
```json
{
  "400": "Bad Request",
  "401": "Unauthorized",
  "403": "Forbidden",
  "404": "Not Found",
  "409": "Conflict",
  "500": "Internal Server Error"
}
```

#### 7.2 Specific Error Messages
```json
{
  "UserNotFound": "User not found",
  "RoomNotFound": "Room not found",
  "GameNotFound": "Game not found",
  "InsufficientFunds": "Insufficient funds",
  "InvalidAction": "Invalid game action",
  "RoomFull": "Room is full",
  "GameInProgress": "Game already in progress",
  "NotYourTurn": "Not your turn",
  "InvalidBetAmount": "Invalid bet amount"
}
```

### 8. Security Requirements

#### 8.1 Authentication
- JWT token-based authentication
- Token expiration: 30 minutes
- Refresh token support
- Secure password storage (BCrypt)

#### 8.2 Authorization
- Role-based access control
- Player can only access their own games
- Admin privileges for room management

#### 8.3 Data Protection
- HTTPS for all communications
- Input validation
- SQL injection prevention
- CSRF protection

### 9. Performance Requirements

#### 9.1 Response Times
- API responses: < 500ms
- Game state updates: < 200ms
- Database queries: < 100ms

#### 9.2 Scalability
- Support 100+ concurrent players
- Handle 10+ simultaneous games
- Database connection pooling

#### 9.3 Resource Limits
- Max players per room: 9
- Max rooms per server: 100
- Max concurrent connections: 500

### 10. Monitoring and Logging

#### 10.1 Log Levels
- ERROR: Critical failures
- WARN: Potential issues
- INFO: Normal operations
- DEBUG: Detailed debugging

#### 10.2 Log Format
```
[TIMESTAMP] [LEVEL] [CLASS] - MESSAGE [DETAILS]
```

#### 10.3 Monitoring Metrics
- Active players
- Active games
- API response times
- Database query times
- Error rates

### 11. Testing Strategy

#### 11.1 Unit Tests
- Game logic validation
- API endpoint testing
- Database operation testing
- Error handling verification

#### 11.2 Integration Tests
- Room creation → joining → game flow
- Multiple players scenarios
- Edge cases (disconnections, timeouts)

#### 11.3 Load Tests
- Concurrent players (100+)
- Simultaneous games (10+)
- Stress testing

### 12. Deployment Architecture

#### 12.1 Production Environment
```
Load Balancer
   ↓
App Servers (x3)
   ↓
Database (MySQL)
   ↓
Monitoring (Prometheus/Grafana)
```

#### 12.2 Development Environment
```
Local Machine
   ↓
Docker Containers
   ↓
MySQL Database
```

### 13. Client Implementation Guide

#### 13.1 Authentication Flow
```
1. Login Screen → POST /auth/login
2. Receive JWT token
3. Store token securely
4. Include token in all subsequent requests
5. Handle token expiration and refresh
```

#### 13.2 Game Flow Implementation
```javascript
// Connect to game
const gameState = await fetchGameState(roomId);

// Handle player turn
if (gameState.currentPlayer === userId) {
    showBettingOptions();
    
    // Player makes action
    const action = await getPlayerAction();
    const response = await sendGameAction(roomId, action);
    
    // Update UI
    updateGameState(response);
}

// Poll for updates
setInterval(async () => {
    const updatedState = await fetchGameState(roomId);
    updateUI(updatedState);
}, 2000);
```

#### 13.3 Room Management
```javascript
// Get room list
const rooms = await fetchRoomList();
displayRooms(rooms);

// Join room
const joinResponse = await joinRoom(roomId, buyInAmount);
if (joinResponse.success) {
    navigateToGame(roomId);
}

// Create room
const createResponse = await createRoom(roomConfig);
if (createResponse.success) {
    joinRoom(createResponse.roomId, buyInAmount);
}
```

### 14. Future Roadmap

#### 14.1 Short-Term (1-3 months)
- WebSocket integration for real-time updates
- Tournament support
- Basic chat functionality
- Improved error handling

#### 14.2 Medium-Term (3-6 months)
- Mobile app integration
- Multi-table support
- Player statistics dashboard
- Admin interface

#### 14.3 Long-Term (6-12 months)
- AI opponents
- Social features
- Virtual gifts
- VIP program

---

**Last Updated**: 2024-12-14
**Version**: 2.0
**Status**: Active Development
**Documentation Level**: Detailed Technical Specification