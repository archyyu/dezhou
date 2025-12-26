const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const jwt = require('jsonwebtoken');

const app = express();
const PORT = 8081;

// Middleware
app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// Mock database
const mockUsers = [
  { id: 1, account: 'testuser', password: 'password', name: 'Test User', email: 'test@example.com' },
  { id: 2, account: 'admin', password: 'admin123', name: 'Admin User', email: 'admin@example.com' }
];

// JWT configuration
const JWT_SECRET = 'dezhou-secret-key-1234567890-abcdefghijklmnopqrstuvwxyz';
const JWT_EXPIRATION = '24h';

// Mock register endpoint
app.post('/api/v1/user/register', (req, res) => {
  const { name, password, email, gendar, birthday } = req.query;
  
  console.log('Registration attempt:', { name, password, email });
  
  if (!name || !password) {
    return res.status(400).json({
      success: false,
      message: 'Name and password are required',
      data: null
    });
  }
  
  // Check if user already exists
  const existingUser = mockUsers.find(u => u.account === name);
  
  if (existingUser) {
    return res.status(400).json({
      success: false,
      message: 'User already exists',
      data: null
    });
  }
  
  // Create new user
  const newUser = {
    id: mockUsers.length + 1,
    account: name,
    password: password,
    name: name,
    email: email || `${name}@example.com`
  };
  mockUsers.push(newUser);
  
  // Generate JWT token
  const token = jwt.sign(
    {
      uid: newUser.id,
      account: newUser.account,
      name: newUser.name
    },
    JWT_SECRET,
    { expiresIn: JWT_EXPIRATION }
  );
  
  res.json({
    success: true,
    message: 'Registration successful',
    data: {
      token: token,
      user: {
        uid: newUser.id,
        account: newUser.account,
        name: newUser.name,
        email: newUser.email,
        roomMoney: 1000,
        allMoney: 10000
      }
    }
  });
});

// Mock login endpoint
app.post('/api/v1/user/login', (req, res) => {
  const { name, password } = req.query;
  
  console.log('Login attempt:', { name, password });
  
  if (!name || !password) {
    return res.status(400).json({
      success: false,
      message: 'Name and password are required',
      data: null
    });
  }
  
  // Find user in mock database
  const user = mockUsers.find(u => u.account === name && u.password === password);
  
  if (!user) {
    // Auto-register if user doesn't exist (matching backend behavior)
    const newUser = {
      id: mockUsers.length + 1,
      account: name,
      password: password,
      name: name,
      email: `${name}@example.com`
    };
    mockUsers.push(newUser);
    
    // Generate JWT token
    const token = jwt.sign(
      {
        uid: newUser.id,
        account: newUser.account,
        name: newUser.name
      },
      JWT_SECRET,
      { expiresIn: JWT_EXPIRATION }
    );
    
    return res.json({
      success: true,
      message: 'Auto-registered and logged in successfully',
      data: {
        token: token,
        user: {
          uid: newUser.id,
          account: newUser.account,
          name: newUser.name,
          email: newUser.email,
          roomMoney: 1000,
          allMoney: 10000
        }
      }
    });
  }
  
  // Generate JWT token for existing user
  const token = jwt.sign(
    {
      uid: user.id,
      account: user.account,
      name: user.name
    },
    JWT_SECRET,
    { expiresIn: JWT_EXPIRATION }
  );
  
  res.json({
    success: true,
    message: 'Login successful',
    data: {
      token: token,
      user: {
        uid: user.id,
        account: user.account,
        name: user.name,
        email: user.email,
        roomMoney: 1000,
        allMoney: 10000
      }
    }
  });
});

// Mock user info endpoint
app.get('/api/v1/user/info', (req, res) => {
  const authHeader = req.headers['authorization'];
  
  if (!authHeader) {
    return res.status(401).json({
      success: false,
      message: 'Authorization header missing',
      data: null
    });
  }
  
  const token = authHeader.split(' ')[1];
  
  try {
    const decoded = jwt.verify(token, JWT_SECRET);
    
    const user = mockUsers.find(u => u.id === decoded.uid);
    
    if (!user) {
      return res.status(404).json({
        success: false,
        message: 'User not found',
        data: null
      });
    }
    
    res.json({
      success: true,
      message: 'User info retrieved successfully',
      data: {
        userId: user.id,
        username: user.account,
        email: user.email,
        mobile: '13800000050',
        gender: 'male',
        birthday: '1990-01-01',
        address: 'Test Address',
        registrationDate: '2023-01-01',
        lastLogin: new Date().toISOString(),
        roomMoney: 1000,
        allMoney: 10000,
        experience: 0,
        gold: 0,
        level: 1,
        status: 'online',
        vipLevel: 'VIP1',
        avatarUrl: 'https://example.com/avatar.jpg'
      }
    });
  } catch (err) {
    res.status(401).json({
      success: false,
      message: 'Invalid token',
      data: null
    });
  }
});

// Mock room endpoints
app.get('/api/v1/room/roomTypeList', (req, res) => {
  res.json({
    success: true,
    message: 'Room types retrieved successfully',
    data: [
      { id: 1, name: 'Texas Holdem', description: 'Classic Texas Holdem poker' },
      { id: 2, name: 'Omaha', description: 'Omaha poker variant' },
      { id: 3, name: 'Tourney', description: 'Tournament style poker' }
    ]
  });
});

app.get('/api/v1/room/:roomTypeId/list', (req, res) => {
  const { roomTypeId } = req.params;
  
  const mockRooms = [
    { id: 101, name: 'Beginner Table', roomTypeId: 1, players: 4, maxPlayers: 6, minBuy: 100, maxBuy: 1000 },
    { id: 102, name: 'Intermediate Table', roomTypeId: 1, players: 5, maxPlayers: 6, minBuy: 500, maxBuy: 5000 },
    { id: 103, name: 'High Stakes', roomTypeId: 1, players: 3, maxPlayers: 6, minBuy: 2000, maxBuy: 20000 }
  ];
  
  const rooms = mockRooms.filter(room => room.roomTypeId == roomTypeId);
  
  res.json({
    success: true,
    message: 'Rooms retrieved successfully',
    data: rooms
  });
});

app.get('/api/v1/room/list', (req, res) => {
  const mockRooms = [
    { id: 101, name: 'Beginner Table', roomTypeId: 1, players: 4, maxPlayers: 6, minBuy: 100, maxBuy: 1000 },
    { id: 102, name: 'Intermediate Table', roomTypeId: 1, players: 5, maxPlayers: 6, minBuy: 500, maxBuy: 5000 },
    { id: 103, name: 'High Stakes', roomTypeId: 1, players: 3, maxPlayers: 6, minBuy: 2000, maxBuy: 20000 },
    { id: 201, name: 'Omaha Table', roomTypeId: 2, players: 4, maxPlayers: 6, minBuy: 100, maxBuy: 1000 },
    { id: 301, name: 'Tournament', roomTypeId: 3, players: 8, maxPlayers: 10, minBuy: 500, maxBuy: 2000 }
  ];
  
  res.json({
    success: true,
    message: 'All rooms retrieved successfully',
    data: mockRooms
  });
});

app.post('/api/v1/room/:roomId/join', (req, res) => {
  const { roomId } = req.params;
  const authHeader = req.headers['authorization'];
  
  if (!authHeader) {
    return res.status(401).json({
      success: false,
      message: 'Authorization required',
      data: null
    });
  }
  
  res.json({
    success: true,
    message: `Joined room ${roomId} successfully`,
    data: {
      roomId: parseInt(roomId),
      joined: true
    }
  });
});

app.post('/api/v1/room/:roomId/leave', (req, res) => {
  const { roomId } = req.params;
  const authHeader = req.headers['authorization'];
  
  if (!authHeader) {
    return res.status(401).json({
      success: false,
      message: 'Authorization required',
      data: null
    });
  }
  
  res.json({
    success: true,
    message: `Left room ${roomId} successfully`,
    data: {
      roomId: parseInt(roomId),
      left: true
    }
  });
});

app.post('/api/v1/room/create/:roomTypeId', (req, res) => {
  const { roomTypeId } = req.params;
  const authHeader = req.headers['authorization'];
  
  if (!authHeader) {
    return res.status(401).json({
      success: false,
      message: 'Authorization required',
      data: null
    });
  }
  
  const newRoomId = 400 + Math.floor(Math.random() * 100);
  
  res.json({
    success: true,
    message: 'Room created successfully',
    data: {
      roomId: newRoomId,
      roomTypeId: parseInt(roomTypeId),
      name: `New Room ${newRoomId}`,
      players: 1,
      maxPlayers: 6
    }
  });
});

// Mock game state endpoint
app.get('/api/v1/game/:roomId/state', (req, res) => {
  const { roomId } = req.params;
  const authHeader = req.headers['authorization'];
  
  if (!authHeader) {
    return res.status(401).json({
      success: false,
      message: 'Authorization required',
      data: null
    });
  }
  
  // Mock game state
  const mockGameState = {
    room: {
      roomid: parseInt(roomId),
      name: `Poker Room #${roomId}`,
      roomTypeName: 'Texas Holdem',
      maxPlayers: 8,
      currentPlayers: 3
    },
    currentHand: 42,
    pot: 1500,
    currentBet: 100,
    communityCards: [
      { suit: '♥', rank: 'A' },
      { suit: '♠', rank: 'K' },
      { suit: '♦', rank: 'Q' }
    ],
    players: [
      { id: 1, name: 'Player 1', chips: 1500, hand: [{ suit: '♣', rank: '10' }, { suit: '♣', rank: 'J' }], seatId: 1, isActive: true, isAllIn: false },
      { id: 2, name: 'Player 2', chips: 1200, hand: [{ suit: '♥', rank: '7' }, { suit: '♠', rank: '7' }], seatId: 2, isActive: true, isAllIn: false },
      { id: 3, name: 'Current Player', chips: 1800, hand: [{ suit: '♠', rank: 'A' }, { suit: '♠', rank: 'Q' }], seatId: 3, isActive: true, isAllIn: false, isCurrentTurn: true }
    ],
    currentTurnPlayerId: 3,
    gamePhase: 'FLOP'
  };
  
  res.json({
    success: true,
    message: 'Game state retrieved successfully',
    data: mockGameState
  });
});

// Mock game actions endpoint
app.post('/api/v1/game/:roomId/actions', (req, res) => {
  const { roomId } = req.params;
  const { cmd, uid } = req.query;
  const authHeader = req.headers['authorization'];
  const body = req.body;
  
  if (!authHeader) {
    return res.status(401).json({
      success: false,
      message: 'Authorization required',
      data: null
    });
  }
  
  // Handle different game commands
  const commandHandlers = {
    '1': handleLookCard,      // CMD_LOOK_CARD
    '2': handleAddBet,        // CMD_ADD_BET
    '3': handleFollowBet,     // CMD_FOLLOW_BET
    '4': handleDropCard,      // CMD_DROP_CARD
    '5': handleAllIn,         // CMD_ALL_IN
    '6': handleSitDown,       // CMD_SITDOWN
    '7': handleStandUp,       // CMD_STANDUP
    '9': handleRoomInfo,      // CMD_ROOMINFO
    'wt': handleWhoTurn,      // CMD_WHO_TURN
    'sbot': handleGameStart,  // CMD_SBOT
    'dbt': handleDistribute,  // CMD_DBT
    'rach': handleFlushAch    // CMD_FLUSHACH
  };
  
  const handler = commandHandlers[cmd];
  if (handler) {
    const result = handler(roomId, uid, body);
    res.json({
      success: true,
      message: `Game action ${cmd} executed successfully`,
      data: result
    });
  } else {
    res.status(400).json({
      success: false,
      message: `Unknown command: ${cmd}`,
      data: null
    });
  }
});

// Game command handlers
function handleLookCard(roomId, uid, params) {
  return {
    action: 'LOOK_CARD',
    result: 'Cards revealed',
    cards: [{ suit: '♠', rank: 'A' }, { suit: '♠', rank: 'Q' }]
  };
}

function handleAddBet(roomId, uid, params) {
  const amount = params.amount || 100;
  return {
    action: 'ADD_BET',
    result: 'Bet added',
    amount: parseInt(amount),
    newPot: 1600
  };
}

function handleFollowBet(roomId, uid, params) {
  return {
    action: 'FOLLOW_BET',
    result: 'Bet followed',
    amount: 100,
    newPot: 1600
  };
}

function handleDropCard(roomId, uid, params) {
  return {
    action: 'DROP_CARD',
    result: 'Cards dropped',
    playerFolded: true
  };
}

function handleAllIn(roomId, uid, params) {
  return {
    action: 'ALL_IN',
    result: 'All in',
    amount: 1800,
    newPot: 3300
  };
}

function handleSitDown(roomId, uid, params) {
  const seatId = params.sid || 1;
  const bet = params.cb || 100;
  return {
    action: 'SITDOWN',
    result: 'Player sat down',
    seatId: parseInt(seatId),
    bet: parseInt(bet)
  };
}

function handleStandUp(roomId, uid, params) {
  return {
    action: 'STANDUP',
    result: 'Player stood up',
    seatId: -1
  };
}

function handleRoomInfo(roomId, uid, params) {
  return {
    action: 'ROOMINFO',
    result: 'Room information',
    room: {
      id: parseInt(roomId),
      name: `Poker Room #${roomId}`,
      players: 3,
      maxPlayers: 8
    }
  };
}

function handleWhoTurn(roomId, uid, params) {
  return {
    action: 'WHO_TURN',
    result: 'Current turn information',
    currentTurnPlayerId: 3
  };
}

function handleGameStart(roomId, uid, params) {
  return {
    action: 'GAME_START',
    result: 'Game started',
    communityCards: []
  };
}

function handleDistribute(roomId, uid, params) {
  return {
    action: 'DISTRIBUTE',
    result: 'Chips distributed',
    winners: [3]
  };
}

function handleFlushAch(roomId, uid, params) {
  return {
    action: 'FLUSH_ACHIEVEMENTS',
    result: 'Achievements flushed'
  };
}

// Mock message endpoints
app.get('/api/v1/user/messages', (req, res) => {
  const authHeader = req.headers['authorization'];
  
  if (!authHeader) {
    return res.status(401).json({
      success: false,
      message: 'Authorization required',
      data: null
    });
  }
  
  res.json({
    success: true,
    message: 'Messages retrieved successfully',
    data: [
      { id: 1, content: 'Welcome to Dezhou Poker!', timestamp: new Date().toISOString() },
      { id: 2, content: 'Your account has been created successfully.', timestamp: new Date().toISOString() }
    ]
  });
});

app.post('/api/v1/user/messages', (req, res) => {
  const authHeader = req.headers['authorization'];
  
  if (!authHeader) {
    return res.status(401).json({
      success: false,
      message: 'Authorization required',
      data: null
    });
  }
  
  res.json({
    success: true,
    message: 'Message sent successfully',
    data: {
      sent: true,
      timestamp: new Date().toISOString()
    }
  });
});

// Mock profile update endpoint
app.put('/api/v1/user/profile', (req, res) => {
  const authHeader = req.headers['authorization'];
  
  if (!authHeader) {
    return res.status(401).json({
      success: false,
      message: 'Authorization required',
      data: null
    });
  }
  
  res.json({
    success: true,
    message: 'Profile updated successfully',
    data: {
      updated: true
    }
  });
});

// Health check endpoint
app.get('/health', (req, res) => {
  res.json({ status: 'UP', message: 'Mock server is running' });
});

// Start server
app.listen(PORT, () => {
  console.log(`Mock server running on http://localhost:${PORT}`);
  console.log('Available endpoints:');
  console.log('- POST /api/v1/user/login - Mock login endpoint');
  console.log('- GET /api/v1/user/info - Mock user info endpoint');
  console.log('- GET /health - Health check endpoint');
});