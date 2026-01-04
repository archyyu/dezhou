import { ref, onMounted, onUnmounted } from 'vue'
import { useAuthStore } from '@/stores/authStore'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

/**
 * WebSocket composable for real-time game notifications
 * 
 * This composable provides WebSocket connectivity to the Spring Boot backend
 * for receiving real-time game events and notifications.
 */
export function useWebSocket() {
  const authStore = useAuthStore()
  const stompClient = ref(null)
  const isConnected = ref(false)
  const connectionError = ref(null)
  const subscriptions = ref({})

  // WebSocket configuration
  const wsBaseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  const wsEndpoint = `${wsBaseURL}/ws`

  /**
   * Connect to WebSocket server
   */
  const connect = () => {
    return new Promise((resolve, reject) => {
      try {
        // Create SockJS connection (with fallback support)
        const socket = new SockJS(wsEndpoint)
        
        // Create STOMP client using the modern Client class
        stompClient.value = new Client({
          webSocketFactory: () => socket,
          reconnectDelay: 5000,
          heartbeatIncoming: 4000,
          heartbeatOutgoing: 4000
        })
        
        // Configure STOMP client
        const headers = {}
        const token = authStore.token
        if (token) {
          headers.Authorization = `Bearer ${token}`
        }
        
        // Connect to server using modern STOMP client
        stompClient.value.onConnect = (frame) => {
          isConnected.value = true
          connectionError.value = null
          console.log('WebSocket connected successfully:', frame)
          resolve(true)
        }
        
        stompClient.value.onStompError = (frame) => {
          isConnected.value = false
          connectionError.value = frame
          console.error('WebSocket STOMP error:', frame)
          reject(frame)
        }
        
        stompClient.value.onWebSocketError = (error) => {
          isConnected.value = false
          connectionError.value = error
          console.error('WebSocket connection error:', error)
          reject(error)
        }
        
        // Start the connection
        stompClient.value.activate()
      } catch (error) {
        isConnected.value = false
        connectionError.value = error
        console.error('WebSocket initialization error:', error)
        reject(error)
      }
    })
  }

  /**
   * Disconnect from WebSocket server
   */
  const disconnect = () => {
    if (stompClient.value && isConnected.value) {
      // Unsubscribe from all topics
      Object.values(subscriptions.value).forEach(subscription => {
        if (subscription && subscription.unsubscribe) {
          subscription.unsubscribe()
        }
      })
      subscriptions.value = {}
      
      // Disconnect using modern STOMP client
      if (stompClient.value.deactivate) {
        stompClient.value.deactivate()
        isConnected.value = false
        console.log('WebSocket disconnected')
      }
    }
  }

  /**
   * Subscribe to a game room's events
   * 
   * @param {string} roomId - The room ID to subscribe to
   * @param {function} callback - Callback function to handle incoming messages
   * @returns {string} Subscription ID
   */
  const subscribeToGameRoom = (roomId, callback) => {
    if (!isConnected.value) {
      console.warn('Cannot subscribe: WebSocket is not connected')
      return null
    }
    
    const subscriptionId = `game-${roomId}-events`
    const destination = `/topic/game.${roomId}.events`
    
    try {
      const subscription = stompClient.value.subscribe(
        destination,
        (message) => {
          try {
            const parsedMessage = JSON.parse(message.body)
            callback(parsedMessage)
          } catch (error) {
            console.error('Error parsing WebSocket message:', error)
          }
        }
      )
      
      subscriptions.value[subscriptionId] = subscription
      console.log(`Subscribed to game room ${roomId} events`)
      return subscriptionId
    } catch (error) {
      console.error(`Failed to subscribe to game room ${roomId}:`, error)
      return null
    }
  }

  /**
   * Unsubscribe from a game room's events
   * 
   * @param {string} subscriptionId - The subscription ID to unsubscribe from
   */
  const unsubscribeFromGameRoom = (subscriptionId) => {
    if (subscriptions.value[subscriptionId]) {
      subscriptions.value[subscriptionId].unsubscribe()
      delete subscriptions.value[subscriptionId]
      console.log(`Unsubscribed from ${subscriptionId}`)
    }
  }

  /**
   * Send a message via WebSocket
   * 
   * @param {string} destination - The destination to send the message to
   * @param {object} message - The message to send
   */
  const sendMessage = (destination, message) => {
    if (isConnected.value && stompClient.value) {
      stompClient.value.publish({
        destination: destination,
        body: JSON.stringify(message)
      })
    } else {
      console.warn('Cannot send message: WebSocket is not connected')
    }
  }

  // Auto-connect when composable is used
  onMounted(() => {
    if (authStore.token) {
      connect().catch(error => {
        console.error('Auto-connect failed:', error)
      })
    }
  })

  // Auto-disconnect when composable is unmounted
  onUnmounted(() => {
    disconnect()
  })

  return {
    stompClient,
    isConnected,
    connectionError,
    connect,
    disconnect,
    subscribeToGameRoom,
    unsubscribeFromGameRoom,
    sendMessage
  }
}