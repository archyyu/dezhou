# Texas Hold'em Server - Requirements Documentation

## 🎯 Project Overview

**Project Name**: Dezhou Poker Server (Texas Hold'em Implementation)
**Type**: Backend Server Application
**Technology Stack**: Spring Boot, MySQL, MyBatis, Java 17+
**Current Status**: Functional server with core game logic and API endpoints

## 🃏 Core Features

### User Management
- ✅ User registration and authentication
- ✅ User profile management
- ✅ User session management
- ✅ User statistics and game history

### Room Management
- ✅ Room creation and configuration
- ✅ Room listing (public/private rooms)
- ✅ Room joining/leaving
- ✅ Room state management

### Game Logic
- ✅ Texas Hold'em game rules implementation
- ✅ Betting rounds (Pre-flop, Flop, Turn, River)
- ✅ Hand evaluation and ranking
- ✅ Pot management and side pots
- ✅ All-in scenarios
- ✅ Showdown and winner determination

## 🔌 API Endpoints

### Authentication & User API
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/register` - User registration
- `GET /api/v1/user/{id}` - Get user profile
- `PUT /api/v1/user/{id}` - Update user profile

### Room API
- `GET /api/v1/room/list` - List available rooms
- `GET /api/v1/room/{roomName}` - Get room details
- `POST /api/v1/room/create` - Create new room
- `POST /api/v1/room/{roomId}/join` - Join a room
- `POST /api/v1/room/{roomId}/leave` - Leave a room

### Game API
- `GET /api/v1/game/{roomId}/state` - Get current game state
- `POST /api/v1/game/{roomId}/actions` - Perform game actions
- `GET /api/v1/game/{roomId}/players/{userId}/status` - Get player status

### Game Actions Supported
- ✅ Check
- ✅ Bet
- ✅ Raise
- ✅ Call
- ✅ Fold
- ✅ All-in
- ✅ Sit down
- ✅ Stand up
- ✅ Look at cards

## 🗃️ Database Schema

### Users Table (`dezhou_user`)
```sql
CREATE TABLE dezhou_user (
    uid INT AUTO_INCREMENT PRIMARY KEY,
    account VARCHAR(50) DEFAULT '',
    password VARCHAR(20) DEFAULT '',
    roommoney INT DEFAULT 0,
    allmoney INT DEFAULT 100000,
    exprience INT DEFAULT 0,
    gold INT DEFAULT 0,
    mobile VARCHAR(20),
    level INT DEFAULT 0,
    sex VARCHAR(10) DEFAULT 'male',
    address VARCHAR(50) DEFAULT '',
    regtime VARCHAR(30) DEFAULT '',
    birthday VARCHAR(30) DEFAULT '',
    logintime VARCHAR(20) DEFAULT '',
    UNIQUE KEY uidx_account (account)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

### Rooms Table (`dezhou_room`)
```sql
CREATE TABLE dezhou_room (
    id INT AUTO_INCREMENT PRIMARY KEY,
    showname VARCHAR(50) DEFAULT '',
    name VARCHAR(50) DEFAULT '',
    bbet INT DEFAULT 0,       -- Big blind
    sbet INT DEFAULT 0,       -- Small blind
    maxbuy INT DEFAULT 0,     -- Maximum buy-in
    minbuy INT DEFAULT 0,     -- Minimum buy-in
    roomtype VARCHAR(10) DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

## 🎮 Game Flow

### Room Creation & Joining
1. Player creates or joins a room
2. Player buys in with chips
3. Room waits for minimum players (usually 2)
4. Game starts when conditions are met

### Hand Flow
1. **Pre-flop**: Players receive 2 hole cards, betting round
2. **Flop**: 3 community cards dealt, betting round
3. **Turn**: 1 community card dealt, betting round
4. **River**: 1 community card dealt, final betting round
5. **Showdown**: Remaining players show cards, winner determined

### Betting Rules
- Small blind and big blind posted before cards are dealt
- Betting proceeds clockwise from the dealer
- Players can check, bet, raise, call, or fold
- Betting rounds continue until all players have matched the highest bet or folded

## 📡 API Response Format

All API responses follow this standard format:
```json
{
  "success": true,
  "status": "success",
  "code": "200",
  "message": "Success",
  "data": {...},
  "timestamp": 1234567890
}
```

Error responses:
```json
{
  "success": false,
  "status": "error",
  "code": "400",
  "message": "Error message",
  "data": null,
  "timestamp": 1234567890
}
```

## 💻 Client Requirements

### Authentication
- Implement login/registration UI
- Handle session management
- Store authentication tokens

### Room Management
- Display available rooms list
- Show room details (blinds, buy-in, player count)
- Implement room creation UI
- Handle room joining/leaving

### Game Interface
- Display player cards and community cards
- Show current pot size and player stacks
- Implement betting controls (check, bet, raise, call, fold)
- Display player actions and game history
- Show current player turn indicator

### Game State Management
- Poll game state regularly
- Handle real-time updates (WebSocket recommended)
- Manage player actions and responses
- Display winner and hand results

## 🧪 Testing Requirements

### Unit Tests
- ✅ Game logic tests (hand evaluation, betting rules)
- ✅ API endpoint tests
- ✅ Database operation tests
- ✅ Error handling tests

### Integration Tests
- Room creation and joining flow
- Game lifecycle testing
- Multiple players scenarios
- Edge cases (disconnections, timeouts)

### Load Testing
- Concurrent players testing
- Performance under load
- Stress testing

## 🚀 Deployment Requirements

### Server
- Java 17+ runtime
- MySQL 8.0+ database
- Spring Boot application server
- Docker containerization (optional)

### Client
- Web browser (for web client)
- Mobile app (for native client)
- Desktop app (for desktop client)

## 🔮 Future Enhancements

### Server-Side
- WebSocket support for real-time updates
- Tournament support
- Chat functionality
- Advanced statistics and analytics
- Admin dashboard

### Client-Side
- Real-time game updates
- Animations and sound effects
- Multi-table support
- Player chat
- Hand history and replays

## 📚 Technical Documentation

### API Documentation
- Swagger/OpenAPI documentation
- API endpoint specifications
- Request/response examples
- Error codes and handling

### Database Documentation
- ER diagrams
- Schema documentation
- Indexing strategy
- Query optimization

### Architecture Documentation
- System architecture diagrams
- Component interactions
- Data flow diagrams
- Deployment architecture

## 🎯 Next Steps for Client Development

### 1. API Integration
- Implement API client for all endpoints
- Handle authentication and session management
- Implement error handling and retries

### 2. Game Logic
- Implement Texas Hold'em rules
- Handle betting rounds and player actions
- Manage game state and updates

### 3. UI/UX
- Design game interface
- Implement responsive design
- Add animations and visual feedback

### 4. Real-time Updates
- Implement WebSocket support
- Handle real-time game events
- Manage connection state

### 5. Testing
- Write integration tests
- Test edge cases
- Performance testing

---

**Last Updated**: 2024-12-14
**Version**: 1.0
**Status**: Active Development