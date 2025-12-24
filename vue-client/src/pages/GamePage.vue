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
              <button @click="leaveRoom" class="btn btn-danger" :disabled="loading">
                Leave Room
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Game Status -->
      <div class="game-status card mb-4">
        <div class="card-body">
          <div class="d-flex justify-content-between">
            <div>
              <h5>Current Hand: {{ gameState.currentHand }}</h5>
              <p class="mb-0">Pot: {{ gameState.pot }} chips</p>
            </div>
            <div>
              <h5>Current Bet: {{ gameState.currentBet }} chips</h5>
              <p class="mb-0">Your Chips: {{ player.chips }}</p>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Player Hand -->
      <div class="player-hand card mb-4">
        <div class="card-body">
          <h5>Your Hand</h5>
          <div class="hand-cards d-flex justify-content-center">
            <div v-for="(card, index) in player.hand" :key="index" class="card me-2">
              <div class="card-body p-2 text-center">
                <div class="card-suit">{{ card.suit }}</div>
                <div class="card-rank">{{ card.rank }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Community Cards -->
      <div class="community-cards card mb-4">
        <div class="card-body">
          <h5>Community Cards</h5>
          <div class="community-cards-container d-flex justify-content-center">
            <div v-for="(card, index) in gameState.communityCards" :key="index" class="card me-2">
              <div class="card-body p-2 text-center">
                <div class="card-suit">{{ card.suit }}</div>
                <div class="card-rank">{{ card.rank }}</div>
              </div>
            </div>
            <div v-for="i in (5 - gameState.communityCards.length)" :key="'empty-' + i" class="card me-2 empty-card">
              <div class="card-body p-2"></div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Player Actions -->
      <div class="player-actions card mb-4">
        <div class="card-body">
          <h5>Your Turn</h5>
          <div class="action-buttons d-flex justify-content-center gap-2">
            <button 
              @click="performAction('FOLD')" 
              class="btn btn-outline-secondary" 
              :disabled="loading || !isPlayerTurn"
            >
              Fold
            </button>
            <button 
              @click="performAction('CHECK')" 
              class="btn btn-outline-primary" 
              :disabled="loading || !isPlayerTurn || gameState.currentBet > 0"
            >
              Check
            </button>
            <button 
              @click="performAction('CALL')" 
              class="btn btn-primary" 
              :disabled="loading || !isPlayerTurn"
            >
              Call ({{ gameState.currentBet }} chips)
            </button>
            <button 
              @click="performAction('RAISE')" 
              class="btn btn-success" 
              :disabled="loading || !isPlayerTurn"
            >
              Raise
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
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useApi } from '@/composables/useApi'

const { getUserProfile, gameAction, leaveRoom } = useApi()
const route = useRoute()
const router = useRouter()

const loading = ref(false)
const error = ref('')
const gameState = ref(null)
const player = ref({})
const isPlayerTurn = ref(false)
const showRaiseInput = ref(false)
const raiseAmount = ref(0)
const gameLog = ref([])
const logContainer = ref(null)

// Game state polling
let gameStateInterval = null

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
    
    // Get current user profile
    const userResponse = await getUserProfile()
    player.value = userResponse.data
    
    // In a real implementation, you would fetch the actual game state
    // For now, we'll simulate it
    gameState.value = {
      room: {
        roomid: route.params.roomId,
        name: 'Poker Room #1',
        roomTypeName: 'Texas Holdem',
        maxPlayers: 8,
        getPlayerCount: () => 3
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
        { id: 1, name: 'Player 1', chips: 1500, hand: [{ suit: '♣', rank: '10' }, { suit: '♣', rank: 'J' }] },
        { id: 2, name: 'Player 2', chips: 1200, hand: [{ suit: '♥', rank: '7' }, { suit: '♠', rank: '7' }] },
        { id: 3, name: 'You', chips: 1800, hand: [{ suit: '♠', rank: 'A' }, { suit: '♠', rank: 'Q' }] }
      ]
    }
    
    // Find current player
    const currentPlayer = gameState.value.players.find(p => p.id === player.value.uid)
    if (currentPlayer) {
      player.value = { ...player.value, ...currentPlayer }
    }
    
    // Simulate game log
    gameLog.value = [
      'Game started',
      'Player 1 posted small blind (50 chips)',
      'Player 2 posted big blind (100 chips)',
      'Cards dealt',
      'Player 1 calls (100 chips)',
      'Your turn...'
    ]
    
    // Scroll to bottom of log
    await nextTick()
    if (logContainer.value) {
      logContainer.value.scrollTop = logContainer.value.scrollHeight
    }
    
  } catch (err) {
    error.value = 'Failed to load game state: ' + (err.response?.data?.message || err.message)
  } finally {
    loading.value = false
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
    
    // Send game action to server
    const response = await gameAction(route.params.roomId, action, {})
    
    // Add to game log
    const actionText = {
      'FOLD': 'folded',
      'CHECK': 'checked',
      'CALL': `called (${gameState.value.currentBet} chips)`
    }[action]
    
    gameLog.value.push(`You ${actionText}`)
    
    // Scroll to bottom of log
    await nextTick()
    if (logContainer.value) {
      logContainer.value.scrollTop = logContainer.value.scrollHeight
    }
    
  } catch (err) {
    error.value = 'Failed to perform action: ' + (err.response?.data?.message || err.message)
  } finally {
    loading.value = false
  }
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

const leaveRoom = async () => {
  try {
    loading.value = true
    error.value = ''
    
    const response = await leaveRoom(route.params.roomId)
    
    // Navigate back to rooms page
    router.push({ name: 'rooms' })
    
  } catch (err) {
    error.value = 'Failed to leave room: ' + (err.response?.data?.message || err.message)
  } finally {
    loading.value = false
  }
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
</style>