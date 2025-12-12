import axios from 'axios'

/**
 * API composable for making HTTP requests to the backend
 */
export function useApi() {
  // Create axios instance
  const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
    headers: {
      'Content-Type': 'application/json'
    }
  })

  // Add request interceptor for authentication
  api.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  })

  // Add response interceptor for error handling
  api.interceptors.response.use(
    response => response,
    error => {
      if (error.response?.status === 401) {
        // Handle unauthorized access
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        window.location.href = '/login'
      }
      return Promise.reject(error)
    }
  )

  return {
    // Authentication
    login: (credentials) => api.post('/auth/login', credentials),
    register: (userData) => api.post('/auth/register', userData),
    
    // Game
    getRooms: () => api.get('/rooms'),
    joinRoom: (roomId) => api.post(`/rooms/${roomId}/join`),
    leaveRoom: (roomId) => api.post(`/rooms/${roomId}/leave`),
    
    // Game actions
    gameAction: (action) => api.post('/game/action', action),
    
    // User
    getUserProfile: () => api.get('/user/profile'),
    updateProfile: (profileData) => api.put('/user/profile', profileData),
    
    // Generic methods
    get: (url) => api.get(url),
    post: (url, data) => api.post(url, data),
    put: (url, data) => api.put(url, data),
    delete: (url) => api.delete(url)
  }
}