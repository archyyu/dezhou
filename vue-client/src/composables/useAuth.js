import { useAuthStore } from '@/stores/authStore'
import { useApi } from './useApi'

/**
 * Authentication composable for managing user session and profile
 * Pure Pinia implementation - uses authStore for all state management
 */
export function useAuth() {
  const authStore = useAuthStore()
  const { getUserProfile } = useApi()

  /**
   * Get current user from Pinia store, fetch from API if needed
   */
  const getCurrentUser = async (forceRefresh = false) => {
    // If we have a user in store and don't need to refresh, return it
    if (!forceRefresh && authStore.user) {
      return authStore.user
    }

    // If we have a token but no user, try to fetch user profile
    if (authStore.token) {
      try {
        authStore.setLoading(true)
        authStore.setError(null)
        
        const response = await getUserProfile()
        if (response.data) {
          authStore.setUser(response.data)
          return response.data
        }
      } catch (err) {
        authStore.setError(err.response?.data?.message || err.message || 'Failed to fetch user')
        console.error('Failed to get current user:', err)
        return null
      } finally {
        authStore.setLoading(false)
      }
    }
    
    return authStore.user
  }

  /**
   * Initialize authentication from existing session (token + user)
   */
  const initializeAuth = (token, user) => {
    authStore.initializeFromSession(token, user)
  }

  /**
   * Check if user is authenticated
   */
  const isAuthenticated = () => {
    return authStore.isAuthenticated
  }

  /**
   * Clear authentication data
   */
  const clearAuth = () => {
    authStore.clearAuth()
  }

  /**
   * Get authentication token
   */
  const getToken = () => {
    return authStore.token
  }

  /**
   * Update user profile
   */
  const updateProfile = (profileData) => {
    authStore.updateUserProfile(profileData)
  }

  return {
    // Direct access to auth store state
    user: authStore.currentUser,
    token: authStore.authToken,
    loading: authStore.loading,
    error: authStore.error,
    isAuthenticated: authStore.isAuth,
    
    // Auth actions
    getCurrentUser,
    initializeAuth,
    clearAuth,
    getToken,
    updateProfile,
    
    // Direct store access for advanced use cases
    authStore
  }
}