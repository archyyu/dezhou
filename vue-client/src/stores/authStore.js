import { defineStore } from 'pinia'

/**
 * Auth store for managing authentication state
 * Pure Pinia implementation - no localStorage dependency for user data
 */
export const useAuthStore = defineStore('auth', {
  state: () => ({
    // Authentication state
    token: null,
    
    // User information
    user: null,
    
    // Loading and error states
    loading: false,
    error: null,
    
    // Authentication status
    isAuthenticated: false
  }),
  
  actions: {
    /**
     * Set authentication token
     */
    setToken(token) {
      this.token = token
      this.isAuthenticated = !!token
    },
    
    /**
     * Set user information
     */
    setUser(user) {
      this.user = user
      this.isAuthenticated = !!user
    },
    
    /**
     * Set loading state
     */
    setLoading(loading) {
      this.loading = loading
    },
    
    /**
     * Set error state
     */
    setError(error) {
      this.error = error
    },
    
    /**
     * Clear authentication data
     */
    clearAuth() {
      this.token = null
      this.user = null
      this.isAuthenticated = false
      this.error = null
    },
    
    /**
     * Initialize auth store from existing session
     */
    initializeFromSession(token, user) {
      this.setToken(token)
      this.setUser(user)
    },
    
    /**
     * Update user profile
     */
    updateUserProfile(profileData) {
      if (this.user) {
        this.user = { ...this.user, ...profileData }
      }
    }
  },
  
  getters: {
    /**
     * Get current user
     */
    currentUser: (state) => state.user,
    
    /**
     * Get authentication token
     */
    authToken: (state) => state.token,
    
    /**
     * Check if user is authenticated
     */
    isAuth: (state) => state.isAuthenticated,
    
    /**
     * Get user ID
     */
    userId: (state) => state.user?.uid || state.user?.id || null,
    
    /**
     * Get user name
     */
    username: (state) => state.user?.name || state.user?.username || 'Guest'
  }
})