import { ref, onMounted } from 'vue'
import { useApi } from './useApi'

/**
 * Authentication composable for managing user session and profile
 */
export function useAuth() {
  const user = ref(null)
  const loading = ref(false)
  const error = ref(null)
  const { getUserProfile } = useApi()

  /**
   * Load user from localStorage
   */
  const loadUserFromStorage = () => {
    try {
      const storedUser = localStorage.getItem('user')
      if (storedUser) {
        user.value = JSON.parse(storedUser)
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
   * Clear authentication data
   */
  const clearAuth = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    user.value = null
  }

  /**
   * Get authentication token
   */
  const getToken = () => {
    return localStorage.getItem('token')
  }

  // Load user from storage on mount
  onMounted(() => {
    loadUserFromStorage()
  })

  return {
    user,
    loading,
    error,
    getCurrentUser,
    isAuthenticated,
    clearAuth,
    getToken,
    loadUserFromStorage
  }
}