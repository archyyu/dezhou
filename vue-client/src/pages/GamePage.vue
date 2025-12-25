<template>
  <div class="game-page container mt-4">
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
      <!-- Game Header -->
      <div class="game-header card mb-4">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center">
            <div>
              <h2 class="mb-0">{{ gameState.room.name }}</h2>
              <p class="text-muted mb-0">
                {{ gameState.room.roomTypeName }} | 
                Players: {{ gameState.room.getPlayerCount() }} / {{ gameState.room.maxPlayers }}
              </p>
            </div>
            <div>
              <button @click="leaveRoomNow" class="btn btn-danger" :disabled="loading">
                Leave Room
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Game Status -->
      <div class="game-status card mb-4">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <div>
              <h5>Current Hand: {{ gameState?.currentHand || 'N/A' }}</h5>
              <p class="mb-0">Pot: {{ gameState?.pot || 0 }} chips</p>
              <p class="mb-0">Game Phase: {{ gameState?.gamePhase || 'LOBBY' }}</p>
            </div>
            <div>
              <h5>Current Bet: {{ gameState?.currentBet || 0 }} chips</h5>
              <p class="mb-0">Your Chips: {{ currentPlayer?.chips || player.allMoney || 0 }}</p>
              <p class="mb-0">Your Seat: {{ playerSeatId.value || 'Not seated' }}</p>
            </div>
            <div>
              <span class="badge bg-primary me-2">Status: {{ playerStatus.value }}</span>
              <span v-if="isPlayerTurn" class="badge bg-success">Your Turn!</span>
            </div>
          </div>
          
          <!-- Lobby Actions -->
          <div v-if="!isGameInProgress" class="lobby-actions d-flex gap-2">
            <button 
              @click="showSeatSelection = true"
              class="btn btn-primary" 
              :disabled="loading || !canSitDown"
            >
              Sit Down
            </button>
            
            <button 
              @click="performAction('STANDUP')"
              class="btn btn-warning" 
              :disabled="loading || !canStandUp"
            >
              Stand Up
            </button>
            
            <button 
              @click="startGame"
              class="btn btn-success" 
              :disabled="loading || !canJoinGame"
            >
              Start Game
            </button>
          </div>
        </div>
      </div>
      
      <!-- Player Hand -->
      <div class="player-hand card mb-4">
        <div class="card-body">
          <h5>Your Hand</h5>
          <div class="hand-cards d-flex justify-content-center">
            <div v-if="currentPlayer?.hand && currentPlayer.hand.length > 0" class="d-flex gap-3">
              <div v-for="(card, index) in currentPlayer.hand" :key="index" class="card" style="width: 80px;">
                <div class="card-body p-2 text-center">
                  <div class="card-suit" :class="{'text-danger': card.suit === '♥' || card.suit === '♦'}">{{ card.suit }}</div>
                  <div class="card-rank">{{ card.rank }}</div>
                </div>
              </div>
            </div>
            <div v-else class="text-muted">
              Cards not revealed yet - click "Look Cards" to reveal
            </div>
          </div>
        </div>
      </div>
      
      <!-- Community Cards -->
      <div class="community-cards card mb-4">
        <div class="card-body">
          <h5>Community Cards ({{ gameState?.communityCards?.length || 0 }}/5)</h5>
          <div class="community-cards-container d-flex justify-content-center">
            <div v-for="(card, index) in gameState?.communityCards" :key="index" class="card me-2">
              <div class="card-body p-2 text-center">
                <div class="card-suit" :class="{'text-danger': card.suit === '♥' || card.suit === '♦'}">{{ card.suit }}</div>
                <div class="card-rank">{{ card.rank }}</div>
              </div>
            </div>
            <div v-for="i in (5 - (gameState?.communityCards?.length || 0))" :key="'empty-' + i" class="card me-2 empty-card">
              <div class="card-body p-2"></div>
            </div>
          </div>
          <div v-if="gameState?.gamePhase" class="text-center mt-2">
            <span class="badge bg-info">
              {{ gamePhaseText(gameState.gamePhase) }}
            </span>
          </div>
        </div>
      </div>
      
      <!-- Player Actions -->
      <div class="player-actions card mb-4">
        <div class="card-body">
          <h5 v-if="isGameInProgress">
            {{ isPlayerTurn ? 'Your Turn' : `Waiting for Player ${gameState.currentTurnPlayerId}` }}
          </h5>
          <h5 v-else>Game Lobby</h5>
          
          <div v-if="isGameInProgress && currentPlayer" class="player-info mb-3">
            <p class="mb-0">Your chips: <strong>{{ currentPlayer.chips }}</strong></p>
            <p class="mb-0">Current bet: <strong>{{ gameState.currentBet }}</strong></p>
            <p class="mb-0">Pot: <strong>{{ gameState.pot }}</strong></p>
          </div>
          
          <div class="action-buttons d-flex justify-content-center gap-2">
            <!-- Game Actions (only when game is in progress) -->
            <button 
              v-if="isGameInProgress && isPlayerTurn"
              @click="performAction('FOLD')" 
              class="btn btn-outline-danger" 
              :disabled="loading"
            >
              Fold
            </button>
            
            <button 
              v-if="isGameInProgress && isPlayerTurn && gameState.currentBet === 0"
              @click="performAction('CHECK')" 
              class="btn btn-outline-primary" 
              :disabled="loading"
            >
              Check
            </button>
            
            <button 
              v-if="isGameInProgress && isPlayerTurn"
              @click="performAction('CALL')" 
              class="btn btn-primary" 
              :disabled="loading"
            >
              Call ({{ gameState.currentBet }} chips)
            </button>
            
            <button 
              v-if="isGameInProgress && isPlayerTurn"
              @click="performAction('RAISE')" 
              class="btn btn-success" 
              :disabled="loading"
            >
              Raise
            </button>
            
            <button 
              v-if="isGameInProgress && isPlayerTurn"
              @click="performAction('ALL_IN')" 
              class="btn btn-warning" 
              :disabled="loading"
            >
              All In
            </button>
            
            <button 
              v-if="isGameInProgress && isPlayerTurn"
              @click="performAction('LOOK')" 
              class="btn btn-info" 
              :disabled="loading"
            >
              Look Cards
            </button>
          </div>
          
          <!-- Raise Amount Input (shown when raise is clicked) -->
          <div v-if="showRaiseInput" class="mt-3">
            <div class="input-group">
              <input 
                v-model="raiseAmount" 
                type="number" 
                class="form-control" 
                placeholder="Enter raise amount" 
                min="0"
              >
              <button @click="confirmRaise" class="btn btn-success" :disabled="loading">
                Confirm Raise
              </button>
              <button @click="cancelRaise" class="btn btn-outline-secondary" :disabled="loading">
                Cancel
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Game Log -->
      <div class="game-log card">
        <div class="card-body">
          <h5>Game Log</h5>
          <div class="log-container" ref="logContainer">
            <div v-for="(log, index) in gameLog" :key="index" class="log-entry">
              {{ log }}
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Seat Selection Modal -->
    <div v-if="showSeatSelection" class="modal fade show" style="display: block;" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Select a Seat</h5>
            <button type="button" class="btn-close" @click="showSeatSelection = false"></button>
          </div>
          <div class="modal-body">
            <div class="seat-grid">
              <div class="row">
                <div v-for="seat in availableSeats" :key="seat" class="col-3 mb-3">
                  <button 
                    @click="sitDown(seat)" 
                    class="btn btn-outline-primary w-100 seat-button"
                    :disabled="loading"
                  >
                    Seat {{ seat }}
                  </button>
                </div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showSeatSelection = false">Cancel</button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Buy-In Modal -->
    <div v-if="showBuyInModal" class="modal fade show" style="display: block;" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Buy-In for Seat {{ playerSeatId }}</h5>
            <button type="button" class="btn-close" @click="showBuyInModal = false"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">Buy-In Amount ({{ gameStore.minBuyIn }}-{{ gameStore.maxBuyIn }} chips)</label>
              <input 
                v-model="buyInAmount" 
                type="number" 
                class="form-control" 
                :min="gameStore.minBuyIn" 
                :max="gameStore.maxBuyIn"
              >
            </div>
            <p class="text-muted">
              Small Blind: {{ gameStore.smallBlind }} chips | Big Blind: {{ gameStore.bigBlind }} chips
            </p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showBuyInModal = false">Cancel</button>
            <button type="button" class="btn btn-primary" @click="performAction('SITDOWN')" :disabled="loading">
              Confirm Buy-In
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Overlay for modals -->
    <div v-if="showSeatSelection || showBuyInModal" class="modal-backdrop fade show"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useApi } from '@/composables/useApi'
import { useAuth } from '@/composables/useAuth'
import { useGameStore } from '@/stores/gameStore'

const { getUserProfile, gameAction, leaveRoom, getRoomInfo } = useApi()
const { getCurrentUser } = useAuth()
const route = useRoute()
const router = useRouter()
const gameStore = useGameStore()

const loading = ref(false)
const error = ref('')
const gameState = ref(null)
const player = ref({})
const showRaiseInput = ref(false)
const raiseAmount = ref(0)
const gameLog = ref([])
const logContainer = ref(null)
const playerSeatId = ref(null)
const playerStatus = ref('spectator') // spectator, sitting, playing
const buyInAmount = ref(1000)
const showBuyInModal = ref(false)
const showSeatSelection = ref(false)
const availableSeats = ref([1, 2, 3, 4, 5, 6, 7, 8])

// Game state polling
let gameStateInterval = null

// Computed properties
const isPlayerTurn = computed(() => {
  return gameState.value && gameState.value.currentTurnPlayerId === player.value.uid
})

const currentPlayer = computed(() => {
  return gameState.value?.players?.find(p => p.id === player.value.uid)
})

const isGameInProgress = computed(() => {
  return gameState.value && gameState.value.gamePhase && gameState.value.gamePhase !== 'LOBBY'
})

const canSitDown = computed(() => {
  return playerStatus.value === 'spectator' && !isGameInProgress.value
})

const canStandUp = computed(() => {
  return playerStatus.value === 'sitting' && !isGameInProgress.value
})

const canJoinGame = computed(() => {
  return playerStatus.value === 'sitting' && !isGameInProgress.value
})

onMounted(async () => {
  await loadGameState()
  
  // Start polling for game state updates
  gameStateInterval = setInterval(loadGameState, 5000)
})

onUnmounted(() => {
  // Clean up polling
  if (gameStateInterval) {
    clearInterval(gameStateInterval)
  }
})

const loadGameState = async () => {
  try {
    loading.value = true
    error.value = ''
    
    // Get current user using our auth composable (which syncs with Pinia)
    const currentUser = await getCurrentUser()
    if (currentUser) {
      // Use the reactive user from Pinia store
      player.value = gameStore.currentUser
      console.log('Loaded user from Pinia store:', player.value)
    } else {
      // Fall back to direct API call if auth composable fails
      const userResponse = await getUserProfile()
      player.value = userResponse.data
      gameStore.setUser(userResponse.data) // Sync with Pinia
    }
    
    // Fetch actual game state from server
    const stateResponse = await useApi().get(`/api/v1/game/${route.params.roomId}/state`)
    
    if (stateResponse.data && stateResponse.data.success) {
      gameState.value = stateResponse.data.data
      
      // Update game store
      gameStore.setRoom({
        roomid: gameState.value.room.roomid,
        name: gameState.value.room.name,
        sbet: 50, // small blind
        bbet: 100, // big blind
        minbuy: 500,
        maxbuy: 5000
      })
      
      // Find current player and update status
      const currentPlayer = gameState.value.players.find(p => p.id === player.value.uid)
      if (currentPlayer) {
        player.value = { ...player.value, ...currentPlayer }
        playerSeatId.value = currentPlayer.seatId
        playerStatus.value = currentPlayer.seatId >= 0 ? 'sitting' : 'spectator'
        
        // If player is sitting and game is in progress, they're playing
        if (playerStatus.value === 'sitting' && isGameInProgress.value) {
          playerStatus.value = 'playing'
        }
      }
      
      // Add game log entry
      addGameLog(`Game state updated - ${gameState.value.gamePhase || 'LOBBY'} phase`)
    }
    
  } catch (err) {
    error.value = 'Failed to load game state: ' + (err.response?.data?.message || err.message)
  } finally {
    loading.value = false
  }
}

const addGameLog = (message) => {
  gameLog.value.push(message)
  
  // Keep log size manageable
  if (gameLog.value.length > 50) {
    gameLog.value = gameLog.value.slice(-50)
  }
  
  // Scroll to bottom of log
  nextTick(() => {
    if (logContainer.value) {
      logContainer.value.scrollTop = logContainer.value.scrollHeight
    }
  })
}

const performAction = async (action) => {
  try {
    loading.value = true
    error.value = ''
    
    if (action === 'RAISE') {
      showRaiseInput.value = true
      return
    }
    
    // Map action to command code
    const commandMap = {
      'FOLD': '4',      // CMD_DROP_CARD
      'CHECK': '2',     // CMD_ADD_BET with 0 amount
      'CALL': '3',      // CMD_FOLLOW_BET
      'RAISE': '2',     // CMD_ADD_BET
      'ALL_IN': '5',    // CMD_ALL_IN
      'LOOK': '1',      // CMD_LOOK_CARD
      'SITDOWN': '6',   // CMD_SITDOWN
      'STANDUP': '7',   // CMD_STANDUP
      'START': 'sbot',  // CMD_SBOT (start game)
      'LEAVE': '8'      // CMD_LEAVE
    }
    
    const cmd = commandMap[action]
    const params = {}
    
    // Add parameters for specific actions
    if (action === 'SITDOWN') {
      params.sid = playerSeatId.value
      params.cb = buyInAmount.value
    }
    
    // Send game action to server
    const response = await gameAction(route.params.roomId, cmd, params)
    
    if (response.data && response.data.success) {
      // Add to game log based on action
      const actionTextMap = {
        'FOLD': 'folded',
        'CHECK': 'checked',
        'CALL': `called (${gameState.value.currentBet} chips)`,
        'ALL_IN': 'went all-in',
        'LOOK': 'looked at cards',
        'SITDOWN': `sat down at seat ${playerSeatId.value} with ${buyInAmount.value} chips`,
        'STANDUP': 'stood up from the table',
        'START': 'started the game'
      }
      
      const actionText = actionTextMap[action] || action
      addGameLog(`You ${actionText}`)
      
      // Update player status for certain actions
      if (action === 'SITDOWN') {
        playerStatus.value = 'sitting'
        showBuyInModal.value = false
      } else if (action === 'STANDUP') {
        playerStatus.value = 'spectator'
        playerSeatId.value = null
      } else if (action === 'START') {
        playerStatus.value = 'playing'
      }
      
      // Refresh game state
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
  playerSeatId.value = seatId
  showSeatSelection.value = false
  showBuyInModal.value = true
}

const startGame = async () => {
  await performAction('START')
}

const leaveRoomNow = async () => {
  try {
    loading.value = true
    error.value = ''
    
    const response = await leaveRoom(route.params.roomId)
    
    // Clear game store
    gameStore.clearRoom()
    
    // Navigate back to rooms page
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
    'LOBBY': 'Lobby'
  }
  return phaseMap[phase] || phase
}

const confirmRaise = async () => {
  try {
    loading.value = true
    error.value = ''
    
    const amount = parseInt(raiseAmount.value)
    if (isNaN(amount) || amount <= 0) {
      error.value = 'Please enter a valid raise amount'
      return
    }
    
    // Send raise action to server
    const response = await gameAction(route.params.roomId, 'RAISE', { amount })
    
    // Add to game log
    gameLog.value.push(`You raised to ${amount} chips`)
    
    // Reset raise input
    showRaiseInput.value = false
    raiseAmount.value = 0
    
    // Scroll to bottom of log
    await nextTick()
    if (logContainer.value) {
      logContainer.value.scrollTop = logContainer.value.scrollHeight
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
.game-page {
  max-width: 1200px;
  margin: 0 auto;
}

.game-container {
  animation: fadeIn 0.5s ease-in;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.card {
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.card-suit, .card-rank {
  font-size: 1.5rem;
  font-weight: bold;
}

.card-suit {
  color: red;
}

.card-rank {
  color: black;
}

.empty-card {
  background-color: #f8f9fa;
  border: 1px dashed #dee2e6;
}

.log-container {
  max-height: 300px;
  overflow-y: auto;
  background-color: #f8f9fa;
  border-radius: 4px;
  padding: 10px;
}

.log-entry {
  padding: 5px 0;
  border-bottom: 1px solid #e9ecef;
}

.log-entry:last-child {
  border-bottom: none;
}

.action-buttons button {
  min-width: 100px;
}

.seat-button {
  height: 60px;
  font-size: 1.1rem;
  font-weight: bold;
}

.seat-button:disabled {
  opacity: 0.5;
}

.lobby-actions button {
  min-width: 120px;
}

.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 1040;
}

.modal.show {
  display: block;
  z-index: 1050;
}

.player-hand, .community-cards {
  background-color: #28a7451a;
  border-left: 4px solid #28a745;
}

.game-status {
  background-color: #007bff1a;
  border-left: 4px solid #007bff;
}

.game-log {
  background-color: #6c757d1a;
  border-left: 4px solid #6c757d;
}
</style>