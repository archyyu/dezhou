import { ref, onMounted, watch } from 'vue'
import { useApi } from './useApi'
import { useGameStore } from '@/stores/gameStore'

/**
 * Authentication composable for managing user session and profile
 * Uses a hybrid approach: Pinia for reactive state + localStorage for persistence
 */
export function useAuth() {
  const user = ref(null)
  const loading = ref(false)
  const error = ref(null)
  const { getUserProfile } = useApi()
  const gameStore = useGameStore()

  /**
   * Load user from localStorage and sync with Pinia store
   */
  const loadUserFromStorage = () => {
    try {
      const storedUser = localStorage.getItem('user')
      if (storedUser) {
        const parsedUser = JSON.parse(storedUser)
        user.value = parsedUser
        gameStore.setUser(parsedUser) // Sync with Pinia store
        return true
      }
      return false
    } catch (err) {
      console.error('Failed to parse user from localStorage:', err)
      return false
    }
  }

  /**
   * Get current user - tries localStorage first, then API
   * Always syncs with Pinia store for reactivity
   */
  const getCurrentUser = async (forceRefresh = false) => {
    if (!forceRefresh && loadUserFromStorage()) {
      return user.value
    }

    try {
      loading.value = true
      error.value = null
      
      const response = await getUserProfile()
      if (response.data) {
        user.value = response.data
        gameStore.setUser(response.data) // Sync with Pinia store
        // Update localStorage with fresh data
        localStorage.setItem('user', JSON.stringify(response.data))
        return response.data
      }
      return null
    } catch (err) {
      error.value = err.response?.data?.message || err.message || 'Failed to fetch user'
      console.error('Failed to get current user:', err)
      return null
    } finally {
      loading.value = false
    }
  }

  /**
   * Check if user is authenticated
   */
  const isAuthenticated = () => {
    return !!localStorage.getItem('token') && !!localStorage.getItem('user')
  }

  /**
   * Clear authentication data from both localStorage and Pinia
   */
  const clearAuth = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    user.value = null
    gameStore.clearUser()
  }

  /**
   * Get authentication token
   */
  const getToken = () => {
    return localStorage.getItem('token')
  }

  /**
   * Sync user changes to localStorage for persistence
   */
  const syncUserToStorage = () => {
    if (user.value) {
      localStorage.setItem('user', JSON.stringify(user.value))
    }
  }

  // Load user from storage on mount
  onMounted(() => {
    loadUserFromStorage()
  })

  // Watch for user changes and sync to localStorage
  watch(user, (newUser) => {
    if (newUser) {
      gameStore.setUser(newUser)
      syncUserToStorage()
    }
  }, { deep: true })

  return {
    user,
    loading,
    error,
    getCurrentUser,
    isAuthenticated,
    clearAuth,
    getToken,
    loadUserFromStorage,
    syncUserToStorage
  }
}