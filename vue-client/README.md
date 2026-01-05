# Puker Game Vue.js Client

## 🎮 Overview

Modern Vue.js client for the Texas Holder Poker game, replacing the legacy Flash frontend with a responsive web application.

## 🚀 Quick Start

```bash
# Install dependencies
npm install

# Run development server
npm run dev

# Build for production
npm run build
```

## 📋 Features

- **Real-time gameplay** with WebSocket integration
- **Responsive design** for desktop and mobile
- **Modern UI** with Vue 3 and Pinia
- **TypeScript** for type safety
- **Axios** for API communication

## 🏗️ Project Structure

```
puker-client/
├── public/              # Static assets
├── src/
│   ├── assets/         # Images, styles
│   ├── components/     # Vue components
│   ├── composables/    # Composition API functions
│   ├── layouts/        # Layout components
│   ├── pages/          # Page components
│   ├── plugins/        # Vue plugins
│   ├── router/         # Vue Router
│   ├── stores/         # Pinia stores
│   ├── utils/          # Utility functions
│   └── App.vue         # Main app
├── package.json
└── vite.config.js
```

## 🔧 Configuration

### API Configuration

```javascript
// src/composables/useApi.js
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
});
```

### WebSocket Configuration

```javascript
// src/composables/useWebSocket.js
const { status, data, send } = useWebSocket(
  `ws://localhost:8080/ws/game/${roomId}`
);
```

## 📦 Dependencies

- **Vue 3** - Core framework
- **Pinia** - State management
- **Vue Router** - Navigation
- **Axios** - HTTP client
- **Bootstrap 5** - UI framework
- **TypeScript** - Type safety

## 🎯 Game Components

### GameTable.vue
```vue
<template>
  <div class="game-table">
    <CommunityCards :cards="communityCards" />
    <Players :players="players" />
    <Pot :amount="pot" />
  </div>
</template>
```

### PlayerHand.vue
```vue
<template>
  <div class="player-hand">
    <Card v-for="card in cards" :key="card.id" :card="card" />
  </div>
</template>
```

### GameControls.vue
```vue
<template>
  <div class="game-controls">
    <button @click="fold">Fold</button>
    <button @click="check">Check</button>
    <button @click="bet">Bet</button>
    <button @click="raise">Raise</button>
  </div>
</template>
```

## 🔄 Backend Integration

### API Endpoints

```javascript
// Authentication
POST /api/auth/login
POST /api/auth/register

// Game
GET /api/game/rooms
POST /api/game/join
POST /api/game/action

// WebSocket
ws://localhost:8080/ws/game/{roomId}
```

### Game State Management

```javascript
// src/stores/gameStore.js
export const useGameStore = defineStore('game', {
  state: () => ({
    currentUser: null,
    currentRoom: null,
    players: [],
    gameState: 'LOBBY'
  }),
  actions: {
    setUser(user) {
      this.currentUser = user;
    }
  }
});
```

## 🚀 Deployment

### Development
```bash
npm run dev
# http://localhost:5173
```

### Production
```bash
npm run build
# Creates dist/ directory
```

### Docker
```bash
docker build -t puker-client .
docker run -p 80:80 puker-client
```

## 📈 Future Enhancements

- **Mobile optimization** - Better touch controls
- **Animations** - Smooth card dealing
- **Sound effects** - Game audio
- **Multi-language** - Internationalization
- **Tutorial mode** - New player guide

The Vue.js client provides a modern, responsive interface for the Puker game!
