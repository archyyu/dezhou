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
          <h1 class="room-name">{{ gameState.name }}</h1>
          <button @click="leaveRoomNow" class="btn btn-danger btn-sm" :disabled="loading">
            Leave
          </button>
        </div>
        <div class="header-stats">
          <span class="stat-badge">
            <strong>Pot:</strong> {{ gameState?.pot || 0 }}
          </span>
          <span class="stat-badge">
            <strong>Bet:</strong> {{ gameState?.currentBet || 0 }}
          </span>
          <span class="stat-badge">
            <strong>Chips:</strong> {{ currentPlayer?.chips || player.allMoney || 0 }}
          </span>
          <span class="stat-badge phase-badge">
            {{ gamePhaseText(gameState?.gamePhase || 'LOBBY') }}
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
              'current-turn': isPlayerTurn && currentPlayer?.seatId === 1
            }" @click="!isSeatOccupied(1) && selectSeat(1)">
              <div class="seat-number">1</div>
              <div v-if="isSeatOccupied(1)" class="seat-info">
                <div class="player-name">{{ getPlayerName(1) }}</div>
                <div class="player-chips">{{ getPlayerChips(1) }}</div>
              </div>
            </div>
          </div>
          
          <!-- Seat 2 - Top Right (1:30) -->
          <div class="seat-wrapper" style="top: 20%; left: 75%;">
            <div class="seat-card" :class="{
              'occupied': isSeatOccupied(2), 
              'selected': playerSeatId === 2,
              'current-turn': isPlayerTurn && currentPlayer?.seatId === 2
            }" @click="!isSeatOccupied(2) && selectSeat(2)">
              <div class="seat-number">2</div>
              <div v-if="isSeatOccupied(2)" class="seat-info">
                <div class="player-name">{{ getPlayerName(2) }}</div>
                <div class="player-chips">{{ getPlayerChips(2) }}</div>
              </div>
            </div>
          </div>
          
          <!-- Seat 3 - Right (3 o'clock) -->
          <div class="seat-wrapper" style="top: 50%; left: 90%;">
            <div class="seat-card" :class="{
              'occupied': isSeatOccupied(3), 
              'selected': playerSeatId === 3,
              'current-turn': isPlayerTurn && currentPlayer?.seatId === 3
            }" @click="!isSeatOccupied(3) && selectSeat(3)">
              <div class="seat-number">3</div>
              <div v-if="isSeatOccupied(3)" class="seat-info">
                <div class="player-name">{{ getPlayerName(3) }}</div>
                <div class="player-chips">{{ getPlayerChips(3) }}</div>
              </div>
            </div>
          </div>
          
          <!-- Seat 4 - Bottom Right (4:30) -->
          <div class="seat-wrapper" style="top: 75%; left: 75%;">
            <div class="seat-card" :class="{
              'occupied': isSeatOccupied(4), 
              'selected': playerSeatId === 4,
              'current-turn': isPlayerTurn && currentPlayer?.seatId === 4
            }" @click="!isSeatOccupied(4) && selectSeat(4)">
              <div class="seat-number">4</div>
              <div v-if="isSeatOccupied(4)" class="seat-info">
                <div class="player-name">{{ getPlayerName(4) }}</div>
                <div class="player-chips">{{ getPlayerChips(4) }}</div>
              </div>
            </div>
          </div>
          
          <!-- Seat 5 - Bottom (6 o'clock) -->
          <div class="seat-wrapper" style="top: 90%; left: 50%;">
            <div class="seat-card" :class="{
              'occupied': isSeatOccupied(5), 
              'selected': playerSeatId === 5,
              'current-turn': isPlayerTurn && currentPlayer?.seatId === 5
            }" @click="!isSeatOccupied(5) && selectSeat(5)">
              <div class="seat-number">5</div>
              <div v-if="isSeatOccupied(5)" class="seat-info">
                <div class="player-name">{{ getPlayerName(5) }}</div>
                <div class="player-chips">{{ getPlayerChips(5) }}</div>
              </div>
            </div>
          </div>
          
          <!-- Seat 6 - Bottom Left (7:30) -->
          <div class="seat-wrapper" style="top: 75%; left: 25%;">
            <div class="seat-card" :class="{
              'occupied': isSeatOccupied(6), 
              'selected': playerSeatId === 6,
              'current-turn': isPlayerTurn && currentPlayer?.seatId === 6
            }" @click="!isSeatOccupied(6) && selectSeat(6)">
              <div class="seat-number">6</div>
              <div v-if="isSeatOccupied(6)" class="seat-info">
                <div class="player-name">{{ getPlayerName(6) }}</div>
                <div class="player-chips">{{ getPlayerChips(6) }}</div>
              </div>
            </div>
          </div>
          
          <!-- Seat 7 - Left (9 o'clock) -->
          <div class="seat-wrapper" style="top: 50%; left: 10%;">
            <div class="seat-card" :class="{
              'occupied': isSeatOccupied(7), 
              'selected': playerSeatId === 7,
              'current-turn': isPlayerTurn && currentPlayer?.seatId === 7
            }" @click="!isSeatOccupied(7) && selectSeat(7)">
              <div class="seat-number">7</div>
              <div v-if="isSeatOccupied(7)" class="seat-info">
                <div class="player-name">{{ getPlayerName(7) }}</div>
                <div class="player-chips">{{ getPlayerChips(7) }}</div>
              </div>
            </div>
          </div>
          
          <!-- Seat 8 - Top Left (10:30) -->
          <div class="seat-wrapper" style="top: 20%; left: 25%;">
            <div class="seat-card" :class="{
              'occupied': isSeatOccupied(8), 
              'selected': playerSeatId === 8,
              'current-turn': isPlayerTurn && currentPlayer?.seatId === 8
            }" @click="!isSeatOccupied(8) && selectSeat(8)">
              <div class="seat-number">8</div>
              <div v-if="isSeatOccupied(8)" class="seat-info">
                <div class="player-name">{{ getPlayerName(8) }}</div>
                <div class="player-chips">{{ getPlayerChips(8) }}</div>
              </div>
            </div>
          </div>
          
          <!-- Table Center (Community Cards) -->
          <div class="table-center">
            <div class="community-cards-display">
              <div class="community-cards-grid">
                <div v-for="(card, index) in gameState?.communityCards" :key="index" class="playing-card">
                  <div class="card-content" :class="{'red-suit': card.suit === '♥' || card.suit === '♦'}">
                    <div class="card-rank">{{ card.rank }}</div>
                    <div class="card-suit">{{ card.suit }}</div>
                  </div>
                </div>
                <div v-for="i in (5 - (gameState?.communityCards?.length || 0))" :key="'empty-' + i" class="playing-card empty">
                  <div class="card-content"></div>
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
          <div v-if="currentPlayer?.hand && currentPlayer.hand.length > 0" class="cards-grid">
            <div v-for="(card, index) in currentPlayer.hand" :key="index" class="playing-card">
              <div class="card-content" :class="{'red-suit': card.suit === '♥' || card.suit === '♦'}">
                <div class="card-rank">{{ card.rank }}</div>
                <div class="card-suit">{{ card.suit }}</div>
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
              v-if="gameState.currentBet === 0"
              @click="performAction('CHECK')" 
              class="btn btn-secondary"
            >
              Check
            </button>
            <button @click="performAction('CALL')" class="btn btn-primary">
              Call {{ gameState.currentBet }}
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
          <p>⏳ Waiting for Player {{ gameState.currentTurnPlayerId }}</p>
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
        
        <div class="modal-body">
          <div class="seat-confirmation">
            <div class="selected-seat-display">
              <div class="seat-icon">{{ playerSeatId }}</div>
              <p>You selected <strong>Seat {{ playerSeatId }}</strong></p>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="showSeatSelection = false">Cancel</button>
          <button class="btn btn-primary" @click="confirmSeatSelection" :disabled="!playerSeatId || playerSeatId < 1">
            Confirm Seat {{ playerSeatId }}
          </button>
        </div>
      </div>
    </div>
    
    <!-- Buy-In Modal -->
    <div v-if="showBuyInModal" class="modal-overlay" @click="showBuyInModal = false">
      <div class="modal-card" @click.stop>
        <div class="modal-header">
          <h5>Buy-In for Seat {{ playerSeatId }}</h5>
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
          <button class="btn btn-primary" @click="performAction('SITDOWN')" :disabled="loading">
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
const playerSeatId = ref(null)
const playerStatus = ref('spectator')
const buyInAmount = ref(1000)
const showBuyInModal = ref(false)
const showSeatSelection = ref(false)

let gameStateInterval = null

const isPlayerTurn = computed(() => {
  return gameState.value && gameState.value.currentTurnPlayerId === player.value.uid
})

const currentPlayer = computed(() => {
  return gameState.value?.players?.find(p => p.id === player.value?.uid)
})

const isGameInProgress = computed(() => {
  return gameState.value && gameState.value.gamePhase && gameState.value.gamePhase !== 'LOBBY'
})

const canSitDown = computed(() => {
  // if (playerStatus === 'spectator' && !isGameInProgress) {
  //   return true
  // }
  // return false
  return true
})

const canStandUp = computed(() => {
  return playerStatus === 'sitting' && !isGameInProgress
})

const canJoinGame = computed(() => {
  return playerStatus === 'sitting' && !isGameInProgress
})

onMounted(async () => {
  await loadGameState()
  gameStateInterval = setInterval(loadGameState, 10000)
})

onUnmounted(() => {
  if (gameStateInterval) {
    clearInterval(gameStateInterval)
  }
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
    
    if (stateResponse.data && stateResponse.data.success) {
      gameState.value = stateResponse.data.data
      
      gameStore.setRoom({
        roomid: gameState.value.roomid,
        name: gameState.value.roomName,
        sbet: 50,
        bbet: 100,
        minbuy: 500,
        maxbuy: 5000
      })
      
      const currentPlayer = gameState.value.players.find(p => p.id === player.value?.uid)
      
      if (currentPlayer) {
        player.value = { ...player.value, ...currentPlayer }
        playerSeatId = currentPlayer.seatId
        playerStatus = currentPlayer.seatId >= 0 ? 'sitting' : 'spectator'
        
        if (playerStatus === 'sitting' && isGameInProgress) {
          playerStatus = 'playing'
        }
      } else {
        playerStatus = 'spectator'
      }
      
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
  
  if (gameLog.value.length > 50) {
    gameLog.value = gameLog.value.slice(-50)
  }
  
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
    
    const commandMap = {
      'FOLD': '4',
      'CHECK': '2',
      'CALL': '3',
      'RAISE': '2',
      'ALL_IN': '5',
      'LOOK': '1',
      'SITDOWN': '6',
      'STANDUP': '7',
      'START': 'sbot',
      'LEAVE': '8'
    }
    
    const cmd = commandMap[action]
    const params = {}
    
    if (action === 'SITDOWN') {
      params.sid = playerSeatId
      params.cb = buyInAmount
    }
    
    const response = await gameAction(route.params.roomId, cmd, params)
    
    if (response.data && response.data.success) {
      const actionTextMap = {
        'FOLD': 'folded',
        'CHECK': 'checked',
        'CALL': `called (${gameState.value.currentBet} chips)`,
        'ALL_IN': 'went all-in',
        'LOOK': 'looked at cards',
        'SITDOWN': `sat down at seat ${playerSeatId} with ${buyInAmount} chips`,
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
        playerSeatId = null
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
  if (!gameState.value?.players) return false
  return gameState.value.players.some(player => 
    player.seatId === seatId && player.id !== user.value?.uid
  )
}

const getPlayerName = (seatId) => {
  if (!gameState.value?.players) return ''
  const player = gameState.value.players.find(p => p.seatId === seatId)
  return player ? player.name : ''
}

const getPlayerChips = (seatId) => {
  if (!gameState.value?.players) return 0
  const player = gameState.value.players.find(p => p.seatId === seatId)
  return player ? player.chips : 0
}

const confirmSeatSelection = () => {
  if (playerSeatId.value && playerSeatId.value > 0) {
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
    'LOBBY': 'Lobby'
  }
  return phaseMap[phase] || phase
}

const confirmRaise = async () => {
  try {
    loading.value = true
    error.value = ''
    
    const amount = parseInt(raiseAmount)
    if (isNaN(amount) || amount <= 0) {
      error.value = 'Please enter a valid raise amount'
      return
    }
    
    const response = await gameAction(route.params.roomId, 'RAISE', { amount })
    gameLog.value.push(`You raised to ${amount} chips`)
    showRaiseInput.value = false
    // raiseAmount = 0
    
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
  raiseAmount = 0
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