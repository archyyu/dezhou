import { defineStore } from 'pinia'

/**
 * Game store for managing game state
 */
export const useGameStore = defineStore('game', {
  state: () => ({
    // Current user information
    currentUser: null,
    
    // Current room information
    currentRoom: null,
    
    // Game state
    gameState: 'LOBBY', // LOBBY, PLAYING, FINISHED
    
    // Players in the room
    players: [],
    
    // Game data
    communityCards: [],
    playerCards: [],
    currentTurn: 0,
    pot: 0,
    
    // Game settings
    smallBlind: 0,
    bigBlind: 0,
    minBuyIn: 0,
    maxBuyIn: 0
  }),
  
  actions: {
    /**
     * Set current user
     */
    setUser(user) {
      this.currentUser = user
    },
    
    /**
     * Clear current user
     */
    clearUser() {
      this.currentUser = null
    },
    
    /**
     * Set current room
     */
    setRoom(room) {
      this.currentRoom = room
      this.smallBlind = room.sbet || 0
      this.bigBlind = room.bbet || 0
      this.minBuyIn = room.minbuy || 0
      this.maxBuyIn = room.maxbuy || 0
    },
    
    /**
     * Clear current room
     */
    clearRoom() {
      this.currentRoom = null
      this.players = []
      this.communityCards = []
      this.playerCards = []
      this.currentTurn = 0
      this.pot = 0
    },
    
    /**
     * Update game state
     */
    setGameState(state) {
      this.gameState = state
    },
    
    /**
     * Add or update player
     */
    updatePlayer(player) {
      const index = this.players.findIndex(p => p.uid === player.uid)
      if (index >= 0) {
        this.players[index] = player
      } else {
        this.players.push(player)
      }
    },
    
    /**
     * Remove player
     */
    removePlayer(uid) {
      this.players = this.players.filter(p => p.uid !== uid)
    },
    
    /**
     * Set community cards
     */
    setCommunityCards(cards) {
      this.communityCards = cards
    },
    
    /**
     * Add community card
     */
    addCommunityCard(card) {
      this.communityCards.push(card)
    },
    
    /**
     * Set player cards
     */
    setPlayerCards(cards) {
      this.playerCards = cards
    },
    
    /**
     * Update turn
     */
    setTurn(turn) {
      this.currentTurn = turn
    },
    
    /**
     * Update pot
     */
    setPot(amount) {
      this.pot = amount
    },
    
    /**
     * Reset game state
     */
    resetGame() {
      this.gameState = 'LOBBY'
      this.communityCards = []
      this.playerCards = []
      this.currentTurn = 0
      this.pot = 0
    }
  },
  
  getters: {
    /**
     * Check if it's current player's turn
     */
    isPlayerTurn: (state) => {
      return state.currentTurn === state.currentUser?.uid
    },
    
    /**
     * Get current player
     */
    currentPlayer: (state) => {
      return state.players.find(p => p.uid === state.currentUser?.uid)
    },
    
    /**
     * Check if user is in a room
     */
    isInRoom: (state) => state.currentRoom !== null,
    
    /**
     * Check if game is in progress
     */
    isGameInProgress: (state) => state.gameState === 'PLAYING'
  }
})