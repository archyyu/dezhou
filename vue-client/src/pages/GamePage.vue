<template>
  <div class="game-page container-fluid mobile-game-page">
    <div v-if="loading" class="text-center my-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p class="mt-3">Loading game...</p>
    </div>
    
    <div v-if="error" class="alert alert-danger">
      {{ error }}
    </div>
    
    <div v-if="!loading && gameState" class="game-container">
      <!-- Mobile Header -->
      <div class="mobile-header">
        <div class="header-top">
          <h1 class="room-name">{{ gameState.roomName }}</h1>
          <button @click="leaveRoomNow" class="btn btn-danger btn-sm" :disabled="loading">
            Leave
          </button>
        </div>
        <div class="header-stats">
          <span class="stat-badge" title="Hand Number">
            <strong>Hand #</strong>{{ gameState?.roundNum || 0 }}
          </span>
          <span class="stat-badge" title="Current Round">
            <strong>{{ roundIndexName(gameState?.roundIndex) }}</strong>
          </span>
          <span class="stat-badge" title="Total Pot">
            <strong>Pot:</strong> {{ gameState?.potAmount || 0 }}
          </span>
          <span class="stat-badge" title="Highest bet in current round">
            <strong>Round Max:</strong> {{ gameState?.currentBetAmount || 0 }}
          </span>
          <span class="stat-badge" v-if="currentPlayer" title="Your bets in the current round">
            <strong>My Round:</strong> {{ currentPlayer?.currentBet || 0 }}
          </span>
          <span class="stat-badge" v-if="currentPlayer" title="Your total bets in this hand">
            <strong>My Total:</strong> {{ currentPlayer?.totalBet || 0 }}
          </span>
          <span class="stat-badge">
            <strong>Chips:</strong> {{ currentPlayer?.chips || player.allMoney || 0 }}
          </span>
          <span class="stat-badge phase-badge">
            {{ gamePhaseText(gameState?.gamePhase || 'LOBBY') }}
          </span>
          <span class="stat-badge ws-badge" :title="isConnected ? 'WebSocket connected - real-time updates' : 'WebSocket disconnected - using polling'">
            <i class="bi" :class="isConnected ? 'bi-wifi' : 'bi-wifi-off'"></i>
            {{ isConnected ? 'Live' : 'Polling' }}
          </span>
        </div>
      </div>
      
      <!-- Main Table Area with Visual Poker Table -->
      <div class="poker-table-container">
        <div class="poker-table">
          <!-- 8 Seats evenly distributed around the table -->
      <!-- Seat positioning uses absolute positioning with explicit coordinates -->
      <!-- top/left percentages position seats around the table edge -->
          
          <!-- Seat 1 - Top (12 o'clock) -->
          <div class="seat-wrapper" style="top: 10%; left: 50%;">
            <div class="seat-card" :class="{
              'occupied': isSeatOccupied(1), 
              'selected': playerSeatId === 1,
              'current-turn': gameState?.currentPlayerSeat === 1
            }" @click="!isSeatOccupied(1) && selectSeat(1)">
              <div class="seat-number">1</div>
              <div v-if="isSeatOccupied(1)" class="seat-info">
                <div class="player-name">{{ getPlayerName(1) }}</div>
                <div class="player-chips">{{ getPlayerChips(1) }}</div>
                <div class="player-round-bet" v-if="getPlayerCurrentBet(1) > 0">
                  💰 {{ getPlayerCurrentBet(1) }}
                </div>
                <div v-if="gameState?.currentPlayerSeat === 1" class="turn-timer">
                  {{ localCountDown }}s
                </div>
              </div>
            </div>
          </div>
          
          <!-- Seat 2 - Top Right (1:30) -->
          <div class="seat-wrapper" style="top: 20%; left: 75%;">
            <div class="seat-card" :class="{
              'occupied': isSeatOccupied(2), 
              'selected': playerSeatId === 2,
              'current-turn': gameState?.currentPlayerSeat === 2
            }" @click="!isSeatOccupied(2) && selectSeat(2)">
              <div class="seat-number">2</div>
              <div v-if="isSeatOccupied(2)" class="seat-info">
                <div class="player-name">{{ getPlayerName(2) }}</div>
                <div class="player-chips">{{ getPlayerChips(2) }}</div>
                <div class="player-round-bet" v-if="getPlayerCurrentBet(2) > 0">
                  💰 {{ getPlayerCurrentBet(2) }}
                </div>
                <div v-if="gameState?.currentPlayerSeat === 2" class="turn-timer">
                  {{ localCountDown }}s
                </div>
              </div>
            </div>
          </div>
          
          <!-- Seat 3 - Right (3 o'clock) -->
          <div class="seat-wrapper" style="top: 50%; left: 90%;">
            <div class="seat-card" :class="{
              'occupied': isSeatOccupied(3), 
              'selected': playerSeatId === 3,
              'current-turn': gameState?.currentPlayerSeat === 3
            }" @click="!isSeatOccupied(3) && selectSeat(3)">
              <div class="seat-number">3</div>
              <div v-if="isSeatOccupied(3)" class="seat-info">
                <div class="player-name">{{ getPlayerName(3) }}</div>
                <div class="player-chips">{{ getPlayerChips(3) }}</div>
                <div class="player-round-bet" v-if="getPlayerCurrentBet(3) > 0">
                  💰 {{ getPlayerCurrentBet(3) }}
                </div>
                <div v-if="gameState?.currentPlayerSeat === 3" class="turn-timer">
                  {{ localCountDown }}s
                </div>
              </div>
            </div>
          </div>
          
          <!-- Seat 4 - Bottom Right (4:30) -->
          <div class="seat-wrapper" style="top: 75%; left: 75%;">
            <div class="seat-card" :class="{
              'occupied': isSeatOccupied(4), 
              'selected': playerSeatId === 4,
              'current-turn': gameState?.currentPlayerSeat === 4
            }" @click="!isSeatOccupied(4) && selectSeat(4)">
              <div class="seat-number">4</div>
              <div v-if="isSeatOccupied(4)" class="seat-info">
                <div class="player-name">{{ getPlayerName(4) }}</div>
                <div class="player-chips">{{ getPlayerChips(4) }}</div>
                <div class="player-round-bet" v-if="getPlayerCurrentBet(4) > 0">
                  💰 {{ getPlayerCurrentBet(4) }}
                </div>
                <div v-if="gameState?.currentPlayerSeat === 4" class="turn-timer">
                  {{ localCountDown }}s
                </div>
              </div>
            </div>
          </div>
          
          <!-- Seat 5 - Bottom (6 o'clock) -->
          <div class="seat-wrapper" style="top: 90%; left: 50%;">
            <div class="seat-card" :class="{
              'occupied': isSeatOccupied(5), 
              'selected': playerSeatId === 5,
              'current-turn': gameState?.currentPlayerSeat === 5
            }" @click="!isSeatOccupied(5) && selectSeat(5)">
              <div class="seat-number">5</div>
              <div v-if="isSeatOccupied(5)" class="seat-info">
                <div class="player-name">{{ getPlayerName(5) }}</div>
                <div class="player-chips">{{ getPlayerChips(5) }}</div>
                <div class="player-round-bet" v-if="getPlayerCurrentBet(5) > 0">
                  💰 {{ getPlayerCurrentBet(5) }}
                </div>
                <div v-if="gameState?.currentPlayerSeat === 5" class="turn-timer">
                  {{ localCountDown }}s
                </div>
              </div>
            </div>
          </div>
          
          <!-- Seat 6 - Bottom Left (7:30) -->
          <div class="seat-wrapper" style="top: 75%; left: 25%;">
            <div class="seat-card" :class="{
              'occupied': isSeatOccupied(6), 
              'selected': playerSeatId === 6,
              'current-turn': gameState?.currentPlayerSeat === 6
            }" @click="!isSeatOccupied(6) && selectSeat(6)">
              <div class="seat-number">6</div>
              <div v-if="isSeatOccupied(6)" class="seat-info">
                <div class="player-name">{{ getPlayerName(6) }}</div>
                <div class="player-chips">{{ getPlayerChips(6) }}</div>
                <div class="player-round-bet" v-if="getPlayerCurrentBet(6) > 0">
                  💰 {{ getPlayerCurrentBet(6) }}
                </div>
                <div v-if="gameState?.currentPlayerSeat === 6" class="turn-timer">
                  {{ localCountDown }}s
                </div>
              </div>
            </div>
          </div>
          
          <!-- Seat 7 - Left (9 o'clock) -->
          <div class="seat-wrapper" style="top: 50%; left: 10%;">
            <div class="seat-card" :class="{
              'occupied': isSeatOccupied(7), 
              'selected': playerSeatId === 7,
              'current-turn': gameState?.currentPlayerSeat === 7
            }" @click="!isSeatOccupied(7) && selectSeat(7)">
              <div class="seat-number">7</div>
              <div v-if="isSeatOccupied(7)" class="seat-info">
                <div class="player-name">{{ getPlayerName(7) }}</div>
                <div class="player-chips">{{ getPlayerChips(7) }}</div>
                <div class="player-round-bet" v-if="getPlayerCurrentBet(7) > 0">
                  💰 {{ getPlayerCurrentBet(7) }}
                </div>
                <div v-if="gameState?.currentPlayerSeat === 7" class="turn-timer">
                  {{ localCountDown }}s
                </div>
              </div>
            </div>
          </div>
          
          <!-- Seat 8 - Top Left (10:30) -->
          <div class="seat-wrapper" style="top: 20%; left: 25%;">
            <div class="seat-card" :class="{
              'occupied': isSeatOccupied(8), 
              'selected': playerSeatId === 8,
              'current-turn': gameState?.currentPlayerSeat === 8
            }" @click="!isSeatOccupied(8) && selectSeat(8)">
              <div class="seat-number">8</div>
              <div v-if="isSeatOccupied(8)" class="seat-info">
                <div class="player-name">{{ getPlayerName(8) }}</div>
                <div class="player-chips">{{ getPlayerChips(8) }}</div>
                <div class="player-round-bet" v-if="getPlayerCurrentBet(8) > 0">
                  💰 {{ getPlayerCurrentBet(8) }}
                </div>
                <div v-if="gameState?.currentPlayerSeat === 8" class="turn-timer">
                  {{ localCountDown }}s
                </div>
              </div>
            </div>
          </div>

          <!-- Individual Seat Win Badges -->
          <template v-for="seatId in 8" :key="'win-' + seatId">
            <div v-if="getWinAmount(seatId) > 0" 
                 class="seat-win-badge-wrapper" 
                 :style="getSeatPosition(seatId)">
              <div class="seat-win-badge">
                <span class="win-plus">+</span>{{ getWinAmount(seatId) }}
              </div>
            </div>
          </template>
          
          <!-- Table Center (Community Cards) -->
          <div class="table-center">
            <div class="community-cards-display">
              <div class="community-cards-grid">
                <div v-for="(card, index) in gameState?.publicPukers" :key="index" class="playing-card">
                  <div class="card-content" :class="{'red-suit': isRedSuit(card.tag)}">
                    <div class="card-rank">{{ formatCardRank(card.num) }}</div>
                    <div class="card-suit">{{ formatCardSuit(card.tag) }}</div>
                  </div>
                </div>
                <div v-for="i in (5 - (gameState?.publicPukers?.length || 0))" :key="'empty-' + i" class="playing-card empty">
                  <div class="card-content"></div>
                </div>
              </div>
            </div>

            <!-- Winner Announcement Overlay -->
            <div v-if="winners.length > 0" class="winner-announcement shadow-lg">
              <div class="winner-title">
                <span class="crown">👑</span> Winners
              </div>
              <div class="winner-list">
                <div v-for="winner in winners" :key="winner.seatId" class="winner-item">
                  <span class="winner-name">{{ winner.playerName }}</span>
                  <span class="winner-amount">+{{ winner.winAmount }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Your Hand -->
      <div class="player-hand-section">
        <h6>Your Hand</h6>
        <div class="hand-cards">
          <div v-if="currentPlayer?.cards && currentPlayer.cards.length > 0" class="cards-grid">
            <div v-for="(card, index) in currentPlayer.cards" :key="index" class="playing-card">
              <div class="card-content" :class="{'red-suit': isRedSuit(card.tag)}">
                <div class="card-rank">{{ formatCardRank(card.num) }}</div>
                <div class="card-suit">{{ formatCardSuit(card.tag) }}</div>
              </div>
            </div>
          </div>
          <div v-else class="no-cards">
            <small>Cards not revealed - tap "Look Cards"</small>
          </div>
        </div>
      </div>
      
      <!-- Action Buttons -->
      <div class="action-section">
        <div v-if="!isGameInProgress" class="lobby-actions">
          <button 
            @click="showSeatSelection = true"
            class="btn btn-primary btn-lg" 
            :disabled="!canSitDown"
          >
            <span>🪑</span> Sit Down
          </button>
          
          <button 
            @click="performAction('STANDUP')"
            class="btn btn-warning btn-lg" 
            :disabled="loading || !canStandUp"
          >
            Stand Up
          </button>
          
          <button 
            @click="startGame"
            class="btn btn-success btn-lg" 
            :disabled="loading || !canJoinGame"
          >
            <span>🎮</span> Start Game
          </button>
        </div>
        
        <div v-if="isGameInProgress && isPlayerTurn" class="game-actions">
          <div class="action-row">
            <button @click="performAction('FOLD')" class="btn btn-danger">
              Fold
            </button>
            <button 
              v-if="gameState.currentBetAmount === 0"
              @click="performAction('CHECK')" 
              class="btn btn-secondary"
            >
              Check
            </button>
            <button @click="performAction('CALL')" class="btn btn-primary">
              Call {{ gameState.currentBetAmount }}
            </button>
          </div>
          <div class="action-row">
            <button @click="performAction('RAISE')" class="btn btn-success">
              Raise
            </button>
            <button @click="performAction('ALL_IN')" class="btn btn-warning">
              All In
            </button>
            <button @click="performAction('LOOK')" class="btn btn-info">
              <span>👀</span> Look Cards
            </button>
          </div>
        </div>
        
        <div v-if="isGameInProgress && !isPlayerTurn" class="waiting-message">
          <p>⏳ Waiting for Player {{ gameState.currentPlayerId }} (Seat {{ gameState.currentPlayerSeat }})</p>
        </div>
      </div>
      
      <!-- Raise Amount Input -->
      <div v-if="showRaiseInput" class="raise-modal">
        <div class="raise-content">
          <h6>Enter Raise Amount</h6>
          <input 
            v-model="raiseAmount" 
            type="number" 
            class="form-control" 
            placeholder="Amount" 
            min="0"
          >
          <div class="raise-buttons">
            <button @click="confirmRaise" class="btn btn-success">Confirm</button>
            <button @click="cancelRaise" class="btn btn-secondary">Cancel</button>
          </div>
        </div>
      </div>
      
      <!-- Game Log -->
      <div class="game-log-section">
        <div class="log-header">
          <h6>Game Log</h6>
        </div>
        <div class="log-content" ref="logContainer">
          <div v-for="(log, index) in gameLog" :key="index" class="log-entry">
            {{ log }}
          </div>
        </div>
      </div>
    </div>
    
    <!-- Seat Selection Modal -->
    <div v-if="showSeatSelection" class="modal-overlay" @click="showSeatSelection = false">
      <div class="modal-card" @click.stop>
        <div class="modal-header">
          <h5>🎰 Confirm Seat Selection</h5>
          <button class="btn-close" @click="showSeatSelection = false">×</button>
        </div>
        
        <div class="modal-body" v-if="playerSeatId !== null">
          <div class="seat-confirmation">
            <div class="selected-seat-display">
              <div class="seat-icon">{{ playerSeatId }}</div>
              <p>You selected <strong>Seat {{ playerSeatId }}</strong></p>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="showSeatSelection = false">Cancel</button>
          <button class="btn btn-primary" @click="confirmSeatSelection" :disabled="playerSeatId === null || playerSeatId < 1">
            Confirm Seat {{ playerSeatId }}
          </button>
        </div>
      </div>
    </div>
    
    <!-- Buy-In Modal -->
    <div v-if="showBuyInModal" class="modal-overlay" @click="showBuyInModal = false">
      <div class="modal-card" @click.stop>
        <div class="modal-header">
          <h5 v-if="playerSeatId !== null">Buy-In for Seat {{ playerSeatId }}</h5>
          <h5 v-else>Buy-In</h5>
          <button class="btn-close" @click="showBuyInModal = false">×</button>
        </div>
        
        <div class="modal-body">
          <label class="form-label">Amount ({{ gameStore.minBuyIn }}-{{ gameStore.maxBuyIn }})</label>
          <input 
            v-model="buyInAmount" 
            type="number" 
            class="form-control" 
            :min="gameStore.minBuyIn" 
            :max="gameStore.maxBuyIn"
          >
          <p class="text-muted mt-2">
            <small>SB: {{ gameStore.smallBlind }} | BB: {{ gameStore.bigBlind }}</small>
          </p>
        </div>
        
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="showBuyInModal = false">Cancel</button>
          <button class="btn btn-primary" @click="performAction('SITDOWN')" :disabled="loading || playerSeatId === null">
            Confirm
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useApi } from '@/composables/useApi'
import { useAuth } from '@/composables/useAuth'
import { useGameStore } from '@/stores/gameStore'
import { useWebSocket } from '@/composables/useWebSocket'

const { getUserProfile, gameAction, leaveRoom, getRoomInfo } = useApi()
const { getCurrentUser, user } = useAuth()
const route = useRoute()
const router = useRouter()
const gameStore = useGameStore()
const { 
  isConnected, 
  connectionError, 
  connect, 
  disconnect, 
  subscribeToGameRoom, 
  subscribeToGameState,
  unsubscribe 
} = useWebSocket()

const loading = ref(false)
const error = ref('')
const gameState = ref(null)
const player = ref({})
const showRaiseInput = ref(false)
const raiseAmount = ref(0)
const gameLog = ref([])
const logContainer = ref(null)
const playerSeatId = ref(null)
const playerStatus = ref('spectator')
const buyInAmount = ref(1000)
const showBuyInModal = ref(false)
const showSeatSelection = ref(false)
const wsEventsSubId = ref(null)
const wsStateSubId = ref(null)
const localCountDown = ref(0)
let localTimerInterval = null

const startLocalTimer = (initialSeconds) => {
  if (localTimerInterval) clearInterval(localTimerInterval)
  
  localCountDown.value = initialSeconds
  
  localTimerInterval = setInterval(() => {
    if (localCountDown.value > 0) {
      localCountDown.value--
    } else {
      clearInterval(localTimerInterval)
    }
  }, 1000)
}

// Seat to Player State Mapping
// Maps seatId (number) to player state object
const seatPlayerMap = ref(new Map())

// Helper to update seat player map from game state
const updateSeatPlayerMap = () => {
  seatPlayerMap.value.clear()
  if (gameState.value?.players) {
    gameState.value.players.forEach(p => {
      if (p.seatId !== undefined && p.seatId !== null) {
        seatPlayerMap.value.set(p.seatId, { ...p })
      }
    })
  }
}

let gameStateInterval = null

const isPlayerTurn = computed(() => {
  return gameState.value && player.value && gameState.value.currentPlayerId === player.value.uid
})

const currentPlayer = computed(() => {
  if (!gameState.value || !player.value) return null
  return gameState.value.players?.find(p => p.playerId === player.value.uid) || null
})

const isGameInProgress = computed(() => {
  return gameState.value && gameState.value.gamePhase && gameState.value.gamePhase !== 'LOBBY'
})

const winners = computed(() => {
  if (!gameState.value || !gameState.value.winMap) return []
  
  return Object.entries(gameState.value.winMap)
    .filter(([_, amount]) => amount > 0)
    .map(([seatId, winAmount]) => {
      const sId = parseInt(seatId)
      const player = gameState.value.players?.find(p => p.seatId === sId)
      return {
        seatId: sId,
        winAmount: winAmount,
        playerName: player ? player.playerName : `Seat ${sId}`
      }
    }).sort((a, b) => b.winAmount - a.winAmount)
})

const canSitDown = computed(() => {
  // if (playerStatus === 'spectator' && !isGameInProgress) {
  //   return true
  // }
  // return false
  return true
})

const canStandUp = computed(() => {
  return playerStatus.value === 'sitting' && !isGameInProgress.value
})

const canJoinGame = computed(() => {
  return playerStatus.value === 'sitting' && !isGameInProgress.value
})

onMounted(async () => {
  await loadGameState()
  gameStateInterval = setInterval(loadGameState, 10000)
  
  // Setup WebSocket connection
  await setupWebSocket()
})

onUnmounted(() => {
  if (gameStateInterval) {
    clearInterval(gameStateInterval)
  }
  if (localTimerInterval) {
    clearInterval(localTimerInterval)
  }
  
  // Cleanup WebSocket connection
  cleanupWebSocket()
})

const loadGameState = async () => {
  try {
    if (loading.value) return
    
    if (!gameState.value) {
      loading.value = true
    }
    error.value = ''
    
    if (!user.value) {
      await getCurrentUser()
    }
    
    if (user.value) {
      player.value = user.value
      gameStore.setUser(user.value)
    } else {
      try {
        const userResponse = await getUserProfile()
        if (userResponse.data && userResponse.data.success) {
          player.value = userResponse.data.data
          gameStore.setUser(userResponse.data.data)
        } else {
          throw new Error('Could not load user data')
        }
      } catch (err) {
        throw new Error('Not authenticated - please login first')
      }
    }
    
    // const stateResponse = await get(`/api/v1/game/${route.params.roomId}/state`)
    const stateResponse = await getRoomInfo(route.params.roomId)
    
    if (stateResponse.data && stateResponse.data.success && stateResponse.data.data) {
      gameState.value = stateResponse.data.data
      
      // Update seat player map
      updateSeatPlayerMap()
      
      gameStore.setRoom({
        roomid: gameState.value.roomId,
        name: gameState.value.roomName,
        sbet: gameState.value.sbet || 5,
        bbet: gameState.value.bbet || 10,
        minbuy: gameState.value.minbuy || 1000,
        maxbuy: gameState.value.maxbuy || 5000
      })
      
      const cp = gameState.value.players?.find(p => p.playerId === player.value?.uid)
      
      if (cp) {
        player.value = { ...player.value, ...cp }
        playerSeatId.value = cp.seatId
        playerStatus.value = cp.seatId >= 0 ? 'sitting' : 'spectator'
        
        if (playerStatus.value === 'sitting' && isGameInProgress.value) {
          playerStatus.value = 'playing'
        }
      } else {
        playerStatus.value = 'spectator'
      }
      
      addGameLog(`Game state updated - ${gameState.value.gamePhase || 'LOBBY'} phase`)
      
      // Start turn timer
      if (gameState.value.currentPlayerId) {
        startLocalTimer(gameState.value.countDown || 0)
      }
    }
    
  } catch (err) {
    error.value = 'Failed to load game state: ' + (err.response?.data?.message || err.message)
  } finally {
    loading.value = false
  }
}

const addGameLog = (message) => {
  gameLog.value.push(message)
  
  if (gameLog.value.length > 50) {
    gameLog.value = gameLog.value.slice(-50)
  }
  
  nextTick(() => {
    if (logContainer.value) {
      logContainer.value.scrollTop = logContainer.value.scrollHeight
    }
  })
}

/**
 * Setup WebSocket connection and subscribe to game room
 */
const setupWebSocket = async () => {
  try {
    // Only connect if user is authenticated
    if (!user.value) {
      console.log('WebSocket: User not authenticated, skipping connection')
      return
    }
    
    // Connect to WebSocket server
    await connect()
    
    // Subscribe to game room events
    wsEventsSubId.value = subscribeToGameRoom(route.params.roomId, handleWebSocketMessage)
    
    // Subscribe to game state updates
    wsStateSubId.value = subscribeToGameState(route.params.roomId, handleWebSocketStateUpdate)
    
    addGameLog('WebSocket connected - real-time updates enabled')
  } catch (error) {
    console.error('WebSocket setup failed:', error)
    addGameLog('WebSocket connection failed - using polling only')
  }
}

/**
 * Cleanup WebSocket connection
 */
const cleanupWebSocket = () => {
  if (wsEventsSubId.value) {
    unsubscribe(wsEventsSubId.value)
    wsEventsSubId.value = null
  }
  if (wsStateSubId.value) {
    unsubscribe(wsStateSubId.value)
    wsStateSubId.value = null
  }
  disconnect()
}

/**
 * Handle incoming WebSocket messages
 * 
 * @param {Object} message - The WebSocket message
 */
const handleWebSocketMessage = (message) => {
  try {
    console.log('WebSocket event received:', message)
    
    // Handle specific game event data if present
    if (message.type === 'GAME_STATE_UPDATE') {
        handleWebSocketStateUpdate(message);
        return;
    }

    // Handle different message types
    switch (message.eventType) {
      case 'PLAYER_ACTION_FOLD':
      case 'PLAYER_ACTION_CHECK':
      case 'PLAYER_ACTION_CALL':
      case 'PLAYER_ACTION_RAISE':
      case 'PLAYER_ACTION_ALL_IN':
      case 'PLAYER_ACTION_LOOK':
      case 'PLAYER_ACTION_SITDOWN':
      case 'PLAYER_ACTION_STANDUP':
      case 'PLAYER_ACTION_START':
        handlePlayerActionMessage(message)
        break
        
      case 'GAME_STARTED':
      case 'GAME_ENDED':
      case 'ROUND_STARTED':
      case 'ROUND_ENDED':
        handleGameStateMessage(message)
        break
        
      default:
        console.log('Unknown WebSocket message type:', message.eventType)
        if (message.eventType) {
          addGameLog(`Game event: ${message.eventType}`)
        }
        break
    }
    
  } catch (error) {
    console.error('Error handling WebSocket message:', error)
  }
}

/**
 * Handle direct Game State Updates from WebSocket
 * 
 * @param {Object} message - The WebSocket message containing full game state
 */
const handleWebSocketStateUpdate = (message) => {
  try {
    if (message && message.type === 'GAME_STATE_UPDATE' && message.data) {
      console.log('Direct Game State Update received')
      
      // Update local game state
      gameState.value = message.data
      
      // Update seat player map
      updateSeatPlayerMap()
      
      // Update current player info
      // Update current player info
      const cp = gameState.value?.players?.find(p => p.playerId === player.value?.uid)
      if (cp) {
        player.value = { ...player.value, ...cp }
        playerSeatId.value = cp.seatId
        playerStatus.value = cp.seatId >= 0 ? 'sitting' : 'spectator'
        
        if (playerStatus.value === 'sitting' && isGameInProgress.value) {
          playerStatus.value = 'playing'
        }
      }
      
      addGameLog(`Game state updated via WebSocket - ${gameState.value?.gamePhase || 'LOBBY'} phase`)

      // Start turn timer from WebSocket update
      if (gameState.value?.currentPlayerId) {
        startLocalTimer(gameState.value?.countDown || 0)
      }
    }
  } catch (error) {
    console.error('Error updating game state from WebSocket:', error)
  }
}

/**
 * Handle player action messages from WebSocket
 * 
 * @param {Object} message - The player action message
 */
const handlePlayerActionMessage = (message) => {
  try {
    if (!message || !message.data) {
      console.error('Invalid WebSocket message format:', message)
      return
    }
    
    const seatId = message.data.seatId
    const playerName = message.data.playerName || `Player ${seatId || 'unknown'}`
    const actionType = message.eventType.replace('PLAYER_ACTION_', '').toLowerCase()
    
    // Update player state in the map
    if (seatId) {
      const stateUpdates = {
        name: message.data.playerName,
        chips: message.data.chips,
        lastAction: actionType,
        lastActionTime: new Date().toISOString()
      }
      
      // For betting actions, update the chips
      if (['call', 'raise', 'all_in'].includes(actionType) && message.data.amount) {
        const pState = getPlayerState(seatId)
        if (pState) {
          stateUpdates.chips = pState.chips - message.data.amount
        }
      }
      
      updatePlayerState(seatId, stateUpdates)
    }
    
    let actionText = ''
    switch (actionType) {
      case 'fold':
        actionText = 'folded'
        break
      case 'check':
        actionText = 'checked'
        break
      case 'call':
        actionText = `called (${message.data.amount || gameState.value?.currentBet || 0} chips)`
        break
      case 'raise':
        actionText = `raised to ${message.data.amount || 0} chips`
        break
      case 'all_in':
        actionText = 'went all-in'
        break
      case 'look':
        actionText = 'looked at cards'
        break
      case 'sitdown':
        actionText = `sat down at seat ${seatId || 'unknown'}`
        // For sitdown, add the player to the map
        if (seatId && message.data.playerId) {
          updatePlayerState(seatId, {
            id: message.data.playerId,
            name: message.data.playerName,
            chips: message.data.chips || buyInAmount.value,
            seatId: seatId,
            status: 'sitting'
          })
        }
        break
      case 'standup':
        actionText = 'stood up from the table'
        // For standup, remove the player from the map
        if (seatId) {
          seatPlayerMap.value.delete(seatId)
        }
        break
      case 'start':
        actionText = 'started the game'
        break
      default:
        actionText = `performed action: ${actionType}`
    }
    
    addGameLog(`${playerName} ${actionText}`)
  } catch (error) {
    console.error('Error handling player action message:', error)
    addGameLog('Received a game event')
  }
}

/**
 * Handle game state messages from WebSocket
 * 
 * @param {Object} message - The game state message
 */
const handleGameStateMessage = (message) => {
  try {
    if (!message) {
      console.error('Invalid game state message:', message)
      return
    }
    
    switch (message.eventType) {
      case 'GAME_STARTED':
        addGameLog('Game started!')
        break
      case 'GAME_ENDED':
        addGameLog('Game ended!')
        break
      case 'ROUND_STARTED':
        addGameLog(`Round ${message.data?.roundNumber || 'new'} started`)
        break
      case 'ROUND_ENDED':
        addGameLog(`Round ${message.data?.roundNumber || 'current'} ended`)
        break
      default:
        addGameLog(`Game state event: ${message.eventType}`)
    }
  } catch (error) {
    console.error('Error handling game state message:', error)
  }
}

const performAction = async (action) => {
  try {
    loading.value = true
    error.value = ''
    
    if (action === 'RAISE') {
      showRaiseInput.value = true
      return
    }
    
    const commandMap = {
      'FOLD': '4',
      'CHECK': '8',
      'CALL': '3',
      'RAISE': '2',
      'ALL_IN': '5',
      'LOOK': '1',
      'SITDOWN': '6',
      'STANDUP': '7',
      'START': 'sbot',
      'LEAVE': '106'
    }
    
    const cmd = commandMap[action]
    const params = {}
    
    if (action === 'SITDOWN') {
      params.sid = playerSeatId.value
      params.cb = buyInAmount.value
    }
    
    const response = await gameAction(route.params.roomId, cmd, params)
    
    if (response.data && response.data.success) {
      const actionTextMap = {
        'FOLD': 'folded',
        'CHECK': 'checked',
        'CALL': `called (${gameState.value.currentBetAmount} chips)`,
        'ALL_IN': 'went all-in',
        'LOOK': 'looked at cards',
        'SITDOWN': `sat down at seat ${playerSeatId.value} with ${buyInAmount.value} chips`,
        'STANDUP': 'stood up from the table',
        'START': 'started the game'
      }
      
      const actionText = actionTextMap[action] || action
      addGameLog(`You ${actionText}`)
      
      if (action === 'SITDOWN') {
        playerStatus.value = 'sitting'
        showBuyInModal.value = false
      } else if (action === 'STANDUP') {
        playerStatus.value = 'spectator'
        playerSeatId.value = null
      } else if (action === 'START') {
        playerStatus.value = 'playing'
      }
      
      await loadGameState()
    } else {
      error.value = 'Failed to perform action: ' + (response.data?.message || 'Unknown error')
    }
    
  } catch (err) {
    error.value = 'Failed to perform action: ' + (err.response?.data?.message || err.message)
  } finally {
    loading.value = false
  }
}

const sitDown = (seatId) => {
  if (!isSeatOccupied(seatId)) {
    playerSeatId.value = seatId
  }
}

const selectSeat = (seatId) => {
  if (!isSeatOccupied(seatId) && canSitDown.value) {
    playerSeatId.value = seatId
    showSeatSelection.value = true
  }
}

const isSeatOccupied = (seatId) => {
  const map = seatPlayerMap.value
  if (!map) return false
  const p = map.get(seatId)
  return p !== undefined && p !== null && p.playerId !== (user.value?.uid || null)
}

const getPlayerName = (seatId) => {
  const map = seatPlayerMap.value
  if (!map) return ''
  const p = map.get(seatId)
  return p ? p.playerName : ''
}

const getPlayerChips = (seatId) => {
  const p = seatPlayerMap.value.get(seatId)
  return p ? p.chips : 0
}

const getPlayerCurrentBet = (seatId) => {
  const p = seatPlayerMap.value.get(seatId)
  return p ? p.currentBet : 0
}

const getPlayerTotalBet = (seatId) => {
  const p = seatPlayerMap.value.get(seatId)
  return p ? p.totalBet : 0
}

const getPlayerState = (seatId) => {
  const map = seatPlayerMap.value
  if (!map) return null
  return map.get(seatId) || null
}

const updatePlayerState = (seatId, stateUpdates) => {
  const p = seatPlayerMap.value.get(seatId)
  if (p) {
    seatPlayerMap.value.set(seatId, { ...p, ...stateUpdates })
  }
}

const formatPlayerAction = (playerState) => {
  if (!playerState || !playerState.lastAction) return ''
  
  const actionIcons = {
    'fold': '❌',
    'check': '✅',
    'call': '💰',
    'raise': '📈',
    'all_in': '💣',
    'look': '👀',
    'sitdown': '🪑',
    'standup': '🚶',
    'start': '🚀'
  }
  
  const timeSince = playerState.lastActionTime 
    ? Math.floor((new Date() - new Date(playerState.lastActionTime)) / 1000)
    : 0
  
  return actionIcons[playerState.lastAction] || playerState.lastAction.substring(0, 3)
}

const confirmSeatSelection = () => {
  if (playerSeatId.value && playerSeatId.value > 0) {
    showSeatSelection.value = false
    showBuyInModal.value = true

    

  }
}

const getWinAmount = (seatId) => {
  if (!gameState.value || !gameState.value.winMap) return 0
  // Jackson Map keys are strings in JSON
  return gameState.value.winMap[seatId.toString()] || gameState.value.winMap[seatId] || 0
}

const getSeatPosition = (seatId) => {
  const positions = {
    1: { top: '10%', left: '50%' },
    2: { top: '20%', left: '75%' },
    3: { top: '50%', left: '90%' },
    4: { top: '75%', left: '75%' },
    5: { top: '90%', left: '50%' },
    6: { top: '75%', left: '25%' },
    7: { top: '50%', left: '10%' },
    8: { top: '20%', left: '25%' }
  }
  return positions[seatId] || { top: '0%', left: '0%' }
}

const startGame = async () => {
  await performAction('START')
}

const leaveRoomNow = async () => {
  try {
    loading.value = true
    error.value = ''
    
    const response = await leaveRoom(route.params.roomId)
    gameStore.clearRoom()
    router.push({ name: 'rooms' })
    
  } catch (err) {
    error.value = 'Failed to leave room: ' + (err.response?.data?.message || err.message)
  } finally {
    loading.value = false
  }
}

const gamePhaseText = (phase) => {
  const phaseMap = {
    'PRE_FLOP': 'Pre-Flop',
    'FLOP': 'Flop',
    'TURN': 'Turn',
    'RIVER': 'River',
    'SHOWDOWN': 'Showdown',
    'LOBBY': 'Lobby',
    'BETTING': 'Betting'
  }
  return phaseMap[phase] || phase
}

const roundIndexName = (index) => {
  const roundMap = {
    1: 'Pre-Flop',
    2: 'Flop',
    3: 'Turn',
    4: 'River'
  }
  return roundMap[index] || 'Lobby'
}

const formatCardSuit = (tag) => {
  const suitMap = {
    'D': '♠', // 黑桃
    'C': '♥', // 红桃
    'B': '♣', // 梅花
    'A': '♦'  // 方块
  }
  return suitMap[tag] || tag
}

const isRedSuit = (tag) => {
  return tag === 'C' || tag === 'A'
}

const formatCardRank = (num) => {
  if (num === 1) return 'A'
  if (num === 11) return 'J'
  if (num === 12) return 'Q'
  if (num === 13) return 'K'
  return num
}

const confirmRaise = async () => {
  try {
    loading.value = true
    error.value = ''
    
    const amount = parseInt(raiseAmount.value)
    if (isNaN(amount) || amount <= 0) {
      error.value = 'Please enter a valid raise amount'
      loading.value = false
      return
    }
    
    const response = await gameAction(route.params.roomId, '2', { cb: amount.toString() })
    
    if (response.data && response.data.success) {
        addGameLog(`You raised to ${amount} chips`)
        showRaiseInput.value = false
        raiseAmount.value = 0
    } else {
        error.value = 'Failed to raise: ' + (response.data?.message || 'Unknown error')
    }
    
  } catch (err) {
    error.value = 'Failed to raise: ' + (err.response?.data?.message || err.message)
  } finally {
    loading.value = false
  }
}

const cancelRaise = () => {
  showRaiseInput.value = false
  raiseAmount.value = 0
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.mobile-game-page {
  padding: 0;
  margin: 0;
  max-width: 100%;
  min-height: 100vh;
  background: #f5f5f5;
}

.game-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 10px;
}

/* Mobile Header */
.mobile-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 15px;
  border-radius: 12px;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.room-name {
  font-size: 1.3rem;
  margin: 0;
  font-weight: 700;
}

.header-stats {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.stat-badge {
  background: rgba(255, 255, 255, 0.2);
  padding: 5px 10px;
  border-radius: 15px;
  font-size: 0.85rem;
  backdrop-filter: blur(10px);
}

.phase-badge {
  background: rgba(255, 255, 255, 0.3);
  font-weight: 600;
}

.ws-badge {
  background: rgba(255, 255, 255, 0.3);
}

.ws-badge i.bi-wifi {
  color: #4CAF50;
}

.ws-badge i.bi-wifi-off {
  color: #F44336;
}

.player-action-badge {
  position: absolute;
  top: -8px;
  right: -8px;
  background: #FFD700;
  color: #000;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  animation: pulse 1s ease-in-out;
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.2); }
  100% { transform: scale(1); }
}

.seat-win-badge-wrapper {
  position: absolute;
  z-index: 100;
  transform: translate(-50%, -100%);
  pointer-events: none;
}

.seat-win-badge {
  background: linear-gradient(135deg, #ffd700 0%, #ffa500 100%);
  color: #000;
  padding: 4px 12px;
  border-radius: 20px;
  font-weight: 800;
  font-size: 0.9rem;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.4);
  border: 2px solid white;
  white-space: nowrap;
  animation: bounceIn 0.8s cubic-bezier(0.36, 0, 0.66, -0.56) infinite alternate;
}

.win-plus {
  font-size: 0.7rem;
  margin-right: 2px;
}

@keyframes bounceIn {
  from { transform: translateY(0); }
  to { transform: translateY(-5px); }
}

/* Winner Announcement */
.winner-announcement {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(0, 0, 0, 0.9);
  color: #ffd700;
  padding: 25px 40px;
  border-radius: 30px;
  z-index: 200;
  text-align: center;
  border: 3px solid #ffd700;
  min-width: 280px;
  backdrop-filter: blur(10px);
  box-shadow: 0 0 40px rgba(255, 215, 0, 0.4);
  animation: winnerAppear 1s ease-out;
}

.winner-title {
  font-size: 1.8rem;
  font-weight: 900;
  margin-bottom: 20px;
  text-transform: uppercase;
  letter-spacing: 3px;
  text-shadow: 0 0 10px rgba(255, 215, 0, 0.5);
}

.crown {
  font-size: 2.5rem;
  display: block;
  margin-bottom: 10px;
  filter: drop-shadow(0 0 5px rgba(255, 215, 0, 0.8));
}

.winner-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.winner-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 1.4rem;
  background: rgba(255, 255, 255, 0.1);
  padding: 10px 20px;
  border-radius: 15px;
  border: 1px solid rgba(255, 215, 0, 0.3);
}

.winner-name {
  color: white;
  font-weight: 600;
}

.winner-amount {
  font-weight: 900;
  color: #4CAF50;
}

@keyframes winnerAppear {
  0% {
    opacity: 0;
    transform: translate(-50%, -50%) scale(0.5);
  }
  70% {
    transform: translate(-50%, -50%) scale(1.1);
  }
  100% {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1);
  }
}

/* Poker Table */
.poker-table-container {
  width: 100%;
  aspect-ratio: 1;
  max-height: 60vh;
  position: relative;
  margin: 0 auto;
}

.poker-table {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #1a5f3e 0%, #2d8f5f 100%);
  border-radius: 50%;
  position: relative;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}

.poker-table::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 35%;
  height: 35%;
  background: radial-gradient(circle, #0a3d24 0%, #1a5f3e 100%);
  border-radius: 50%;
  box-shadow: inset 0 4px 12px rgba(0, 0, 0, 0.3);
}

/* Seat Wrapper - Using CSS custom properties for positioning */
.seat-wrapper {
  position: absolute;
  z-index: 10;
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  transform: translate(-50%, -50%);
}

/* Ensure the poker table has proper positioning context */
.poker-table {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #1a5f3e 0%, #2d8f5f 100%);
  border-radius: 50%;
  position: relative;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
  overflow: visible; /* Ensure seats can extend beyond table edge */
  margin: 0 auto;
}

/* Table center area for community cards */
.table-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 60%;
  height: 60%;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 5; /* Ensure community cards are below seats */
}

.seat-confirmation {
  text-align: center;
  padding: 20px 0;
}

.selected-seat-display {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.seat-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #51cf66 0%, #37b24d 100%);
  border: 4px solid #2f9e44;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  font-weight: bold;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.selected-seat-display p {
  font-size: 1.1rem;
  margin: 0;
  color: #333;
}

.seat-card {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #fff 0%, #f0f0f0 100%);
  border: 3px solid #ddd;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative; /* Fixed for turn-timer positioning */
}

.turn-timer {
  position: absolute;
  bottom: -15px;
  left: 50%;
  transform: translateX(-50%);
  background: #e74c3c;
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 0.75rem;
  font-weight: bold;
  z-index: 20;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  border: 1px solid white;
}

.seat-card.occupied {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
  border-color: #c92a2a;
  cursor: not-allowed;
}

.seat-card.selected {
  background: linear-gradient(135deg, #51cf66 0%, #37b24d 100%);
  border-color: #2f9e44;
  box-shadow: 0 0 20px rgba(81, 207, 102, 0.5);
}

.seat-card.current-turn {
  background: linear-gradient(135deg, #ffd93d 0%, #f7b731 100%);
  border-color: #d69e00;
  box-shadow: 0 0 20px rgba(255, 217, 61, 0.5);
}

.seat-number {
  font-size: 1.2rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 2px;
}

.seat-info {
  font-size: 0.7rem;
  text-align: center;
  color: #666;
}

.player-name {
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
}

.player-chips {
  font-size: 0.6rem;
  opacity: 0.8;
}

.player-round-bet {
  font-size: 0.7rem;
  font-weight: bold;
  color: #fff;
  background: rgba(0, 0, 0, 0.4);
  padding: 2px 6px;
  border-radius: 8px;
  margin-top: 2px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.table-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 60%;
  height: 60%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.community-cards-display {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.community-cards-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8px;
  width: 90%;
  height: 60%;
}

.playing-card {
  width: 100%;
  height: 100%;
  background: white;
  border: 2px solid #ddd;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
  position: relative;
}

.playing-card.empty {
  background: rgba(255, 255, 255, 0.1);
  border: 2px dashed #aaa;
}

.card-content {
  font-size: 1.5rem;
  font-weight: bold;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.card-content.red-suit {
  color: #e74c3c;
}

.card-rank {
  font-size: 1.2rem;
  margin-bottom: -5px;
}

.card-suit {
  font-size: 1.8rem;
}

.player-hand-section {
  background: white;
  padding: 15px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-top: 15px;
}

.player-hand-section h6 {
  margin-bottom: 10px;
  color: #333;
  font-weight: 600;
}

.hand-cards {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.cards-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  width: 100%;
}

.no-cards {
  padding: 15px;
  text-align: center;
  color: #999;
  font-style: italic;
}

.action-section {
  background: white;
  padding: 15px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-top: 15px;
}

.action-row {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.action-row:last-child {
  margin-bottom: 0;
}

.action-section button {
  flex: 1;
  min-width: 100px;
  padding: 10px;
  font-weight: 600;
}

.lobby-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.lobby-actions button {
  padding: 15px;
  font-size: 1.1rem;
}

.waiting-message {
  text-align: center;
  padding: 15px;
  color: #666;
  font-style: italic;
}

.raise-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.raise-content {
  background: white;
  padding: 25px;
  border-radius: 15px;
  width: 85%;
  max-width: 400px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.raise-content h6 {
  margin-bottom: 15px;
  text-align: center;
}

.raise-buttons {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

.raise-buttons button {
  flex: 1;
}

.game-log-section {
  background: white;
  padding: 15px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-top: 15px;
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.log-header {
  margin-bottom: 10px;
}

.log-header h6 {
  margin: 0;
  font-weight: 600;
}

.log-content {
  flex: 1;
  overflow-y: auto;
  font-size: 0.85rem;
  line-height: 1.4;
}

.log-entry {
  padding: 5px 0;
  border-bottom: 1px solid #eee;
}

.log-entry:last-child {
  border-bottom: none;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-card {
  background: white;
  padding: 25px;
  border-radius: 15px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.modal-header h5 {
  margin: 0;
  font-weight: 700;
}

.btn-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #999;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-body {
  margin-bottom: 20px;
}

.modal-footer {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.modal-footer button {
  padding: 10px 20px;
}

@media (min-width: 768px) {
  .mobile-game-page {
    max-width: 800px;
    margin: 0 auto;
  }

  .poker-table-container {
    max-height: 70vh;
  }

  .seat-card {
    width: 70px;
    height: 70px;
  }

  .seat-number {
    font-size: 1.4rem;
  }

  .seat-info {
    font-size: 0.8rem;
  }
}
</style>