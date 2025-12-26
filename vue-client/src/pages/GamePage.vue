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
              <h2 class="mb-0">{{ gameState.name }}</h2>
              <p class="text-muted mb-0">
                {{ gameState.roomTypeName }} | 
                Players: {{ gameState.players.length }} / {{ gameState.maxPlayers }}
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
              :disabled="false"
            >
              Sit Down
            </button>
            
            <!-- Debug info -->
            <div class="debug-info text-muted small ms-3">
              Status: {{ playerStatus.value }} | Can Sit: {{ canSitDown }} | Game: {{ gameState.value?.gamePhase || 'N/A' }}
            </div>
            
            <!-- Debug button to force show seat selection -->
            <button @click="showSeatSelection = true" class="btn btn-sm btn-outline-danger ms-2">
              🔧 Debug: Show Seats
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
    
    <!-- Seat Selection Modal - Visual Poker Table -->
    <div v-if="showSeatSelection" class="modal fade show" style="display: block;" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">🎰 Select Your Seat at the Poker Table</h5>
            <button type="button" class="btn-close" @click="showSeatSelection = false"></button>
          </div>
          
          <div class="modal-body">
            <!-- Debug: Show current selections -->
            <div class="alert alert-info mb-3">
              <strong>Debug Info:</strong> 
              Selected Seat: {{ playerSeatId }} | 
              Current User: {{ user.value?.name || 'None' }} | 
              Game Phase: {{ 'N/A' }}
            </div>
            
            <div class="poker-table-container">
              <div class="poker-table">
                <!-- Dealer Position (Top) -->
                <div class="seat-position dealer-position">
                  <div class="seat-card" @click="sitDown(1)" :class="{ 'occupied': isSeatOccupied(1), 'selected': playerSeatId === 1 }">
                    <div class="seat-number">1</div>
                    <div class="seat-label">Dealer</div>
                    <div v-if="isSeatOccupied(1)" class="seat-occupied">👤 Occupied</div>
                  </div>
                </div>
                
                <!-- Top Right Seat -->
                <div class="seat-position top-right">
                  <div class="seat-card" @click="sitDown(2)" :class="{ 'occupied': isSeatOccupied(2), 'selected': playerSeatId === 2 }">
                    <div class="seat-number">2</div>
                    <div class="seat-label">Player</div>
                    <div v-if="isSeatOccupied(2)" class="seat-occupied">👤 Occupied</div>
                  </div>
                </div>
                
                <!-- Right Side Seats -->
                <div class="seat-position right-top">
                  <div class="seat-card" @click="sitDown(3)" :class="{ 'occupied': isSeatOccupied(3), 'selected': playerSeatId === 3 }">
                    <div class="seat-number">3</div>
                    <div class="seat-label">Player</div>
                    <div v-if="isSeatOccupied(3)" class="seat-occupied">👤 Occupied</div>
                  </div>
                </div>
                
                <div class="seat-position right-bottom">
                  <div class="seat-card" @click="sitDown(4)" :class="{ 'occupied': isSeatOccupied(4), 'selected': playerSeatId === 4 }">
                    <div class="seat-number">4</div>
                    <div class="seat-label">Player</div>
                    <div v-if="isSeatOccupied(4)" class="seat-occupied">👤 Occupied</div>
                  </div>
                </div>
                
                <!-- Bottom Right Seat -->
                <div class="seat-position bottom-right">
                  <div class="seat-card" @click="sitDown(5)" :class="{ 'occupied': isSeatOccupied(5), 'selected': playerSeatId === 5 }">
                    <div class="seat-number">5</div>
                    <div class="seat-label">Player</div>
                    <div v-if="isSeatOccupied(5)" class="seat-occupied">👤 Occupied</div>
                  </div>
                </div>
                
                <!-- Bottom Left Seat -->
                <div class="seat-position bottom-left">
                  <div class="seat-card" @click="sitDown(6)" :class="{ 'occupied': isSeatOccupied(6), 'selected': playerSeatId === 6 }">
                    <div class="seat-number">6</div>
                    <div class="seat-label">Player</div>
                    <div v-if="isSeatOccupied(6)" class="seat-occupied">👤 Occupied</div>
                  </div>
                </div>
                
                <!-- Left Side Seats -->
                <div class="seat-position left-bottom">
                  <div class="seat-card" @click="sitDown(7)" :class="{ 'occupied': isSeatOccupied(7), 'selected': playerSeatId === 7 }">
                    <div class="seat-number">7</div>
                    <div class="seat-label">Player</div>
                    <div v-if="isSeatOccupied(7)" class="seat-occupied">👤 Occupied</div>
                  </div>
                </div>
                
                <div class="seat-position left-top">
                  <div class="seat-card" @click="sitDown(8)" :class="{ 'occupied': isSeatOccupied(8), 'selected': playerSeatId === 8 }">
                    <div class="seat-number">8</div>
                    <div class="seat-label">Player</div>
                    <div v-if="isSeatOccupied(8)" class="seat-occupied">👤 Occupied</div>
                  </div>
                </div>
                
                <!-- Table Center (Your Position) -->
                <div class="table-center">
                  <div class="your-position">
                    👤 YOUR POSITION
                  </div>
                </div>
              </div>
            </div>
            
            <!-- Seat Legend -->
            <div class="seat-legend mt-4">
              <div class="legend-item">
                <div class="legend-color available"></div>
                <span>Available</span>
              </div>
              <div class="legend-item">
                <div class="legend-color occupied"></div>
                <span>Occupied</span>
              </div>
              <div class="legend-item">
                <div class="legend-color selected"></div>
                <span>Your Selection</span>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showSeatSelection = false">Cancel</button>
            <button type="button" class="btn btn-primary" @click="confirmSeatSelection" :disabled="playerSeatId === -1">
              Confirm Seat {{ playerSeatId > 0 ? playerSeatId : '' }}
            </button>
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
const { getCurrentUser, user } = useAuth()
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
const playerSeatId = ref(-1)
const playerStatus = ref('spectator') // spectator, sitting, playing
const buyInAmount = ref(1000)
const showBuyInModal = ref(false)
const showSeatSelection = ref(true)
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
  console.log(user.uid)
  gameState.value.spectaclors.forEach( item => {
    if (item.playerid == user.uid) {
      return true;
    }
  });
  return false;
  //return playerStatus.value === 'spectator' && !isGameInProgress.value
})

const canStandUp = computed(() => {
  return playerStatus.value === 'sitting' && !isGameInProgress.value
})

const canJoinGame = computed(() => {
  return playerStatus.value === 'sitting' && !isGameInProgress.value
})

onMounted(async () => {
  await loadGameState()
  
  // Start polling for game state updates (less frequent to reduce load)
  gameStateInterval = setInterval(loadGameState, 10000)
})

onUnmounted(() => {
  // Clean up polling
  if (gameStateInterval) {
    clearInterval(gameStateInterval)
  }
})

const loadGameState = async () => {
  try {
    // Skip loading if we're already in the middle of loading
    if (loading.value) {
      console.log('Skipping loadGameState - already loading')
      return
    }
    
    // Only show loading for initial load, not for polling
    if (!gameState.value) {
      loading.value = true
    }
    error.value = ''
    
    // Get current user using our pure Pinia auth system (only if not already loaded)
    if (!user.value) {
      await getCurrentUser()
      console.log('Current user from auth store:', user.value)
    }
    
    // Use the reactive user directly from our auth composable
    if (user.value) {
      player.value = user.value
      gameStore.setUser(user.value) // Also sync with gameStore for consistency
      console.log('Loaded user from Pinia auth store:', player.value)
    } else {
      // Fall back to direct API call if needed
      // Try to get user profile - if we have a token, this should work
      try {
        const userResponse = await getUserProfile()
        if (userResponse.data && userResponse.data.success) {
          player.value = userResponse.data.data
          gameStore.setUser(userResponse.data.data)
          console.log('Loaded user from API fallback:', player.value)
        } else {
          console.error('Failed to load user from API:', userResponse.data)
          throw new Error('Could not load user data')
        }
      } catch (err) {
        console.error('API fallback failed:', err)
        throw new Error('Not authenticated - please login first')
      }
    }
    
    // Fetch actual game state from server
    const stateResponse = await useApi().get(`/api/v1/game/${route.params.roomId}/state`)
    
    if (stateResponse.data && stateResponse.data.success) {
      gameState.value = stateResponse.data.data

      console.log(gameState.value)
      
      // Update game store
      gameStore.setRoom({
        roomid: gameState.value.roomid,
        name: gameState.value.roomName,
        sbet: 50, // small blind
        bbet: 100, // big blind
        minbuy: 500,
        maxbuy: 5000
      })
      
      // Find current player and update status
      const currentPlayer = gameState.value.players.find(p => p.id === player.value.uid)
      if (currentPlayer) {
        player.value = { ...player.value, ...currentPlayer }
        console.log(currentPlayer)
        playerSeatId.value = currentPlayer.seatId
        console.log("seatId:" + playerSeatId)
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
}

/**
 * Check if a seat is occupied by another player
 */
const isSeatOccupied = (seatId) => {
  if (!gameState.value?.players) return false
  
  // Check if any player is sitting at this seat (excluding current player)
  return gameState.value.players.some(player => 
    player.seatId === seatId && player.id !== user.value?.uid
  )
}

/**
 * Confirm seat selection and proceed to buy-in
 */
const confirmSeatSelection = () => {
  if (playerSeatId.value > 0) {
    showSeatSelection.value = false
    showBuyInModal.value = true
  }
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

/* Poker Table Styles */
.poker-table-container {
  position: relative;
  width: 100%;
  height: 400px;
  margin: 0 auto;
}

.poker-table {
  position: relative;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #1a5f3e 0%, #2d8f5f 100%);
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 0 0 30px rgba(0, 0, 0, 0.5);
}

.poker-table::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, #0a3d24 0%, #1a5f3e 100%);
  border-radius: 50%;
  z-index: 1;
}

.seat-position {
  position: absolute;
  z-index: 10;
}

/* Dealer Position - Top */
.dealer-position {
  top: 10%;
  left: 50%;
  transform: translateX(-50%);
}

/* Top Right */
.top-right {
  top: 20%;
  right: 15%;
}

/* Right Side */
.right-top {
  top: 35%;
  right: 5%;
}

.right-bottom {
  bottom: 35%;
  right: 5%;
}

/* Bottom Right */
.bottom-right {
  bottom: 20%;
  right: 15%;
}

/* Bottom Left */
.bottom-left {
  bottom: 20%;
  left: 15%;
}

/* Left Side */
.left-bottom {
  bottom: 35%;
  left: 5%;
}

.left-top {
  top: 35%;
  left: 5%;
}

/* Table Center */
.table-center {
  position: absolute;
  bottom: 10%;
  left: 50%;
  transform: translateX(-50%);
  z-index: 5;
}

.seat-card {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border: 3px solid #dee2e6;
  border-radius: 15px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  position: relative;
}

.seat-card:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3);
}

.seat-card.occupied {
  background: linear-gradient(135deg, #f8d7da 0%, #f5c6cb 100%);
  border-color: #f5c6cb;
  cursor: not-allowed;
  transform: none !important;
}

.seat-card.selected {
  background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
  border-color: #c3e6cb;
  box-shadow: 0 0 15px rgba(40, 167, 69, 0.5);
}

.seat-number {
  font-size: 1.5rem;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 2px;
}

.seat-label {
  font-size: 0.7rem;
  color: #6c757d;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.seat-occupied {
  position: absolute;
  bottom: -25px;
  left: 50%;
  transform: translateX(-50%);
  background: #dc3545;
  color: white;
  padding: 3px 8px;
  border-radius: 10px;
  font-size: 0.6rem;
  white-space: nowrap;
}

.your-position {
  background: rgba(255, 255, 255, 0.9);
  padding: 15px 25px;
  border-radius: 20px;
  font-weight: bold;
  color: #2c3e50;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.seat-legend {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.9rem;
}

.legend-color {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 2px solid #dee2e6;
}

.legend-color.available {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}

.legend-color.occupied {
  background: linear-gradient(135deg, #f8d7da 0%, #f5c6cb 100%);
}

.legend-color.selected {
  background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
}

.lobby-actions button {
  min-width: 120px;
}

.debug-info {
  margin-top: 5px;
  padding: 2px 6px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
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