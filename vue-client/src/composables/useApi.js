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
    login: (credentials) => api.post('/api/v1/user/login', null, { params: credentials }),
    register: (userData) => api.post('/api/v1/user/register', null, { params: userData }),
    
    // Game
    getRooms: () => api.get('/api/v1/room/list'),
    joinRoom: (roomName, uid) => api.post(`/api/v1/room/${roomName}/join`, null, { params: { uid } }),
    leaveRoom: (roomName, uid) => api.post(`/api/v1/room/${roomName}/leave`, null, { params: { uid } }),
    
    // Game actions
    gameAction: (roomId, uid, cmd, additionalParams) => api.post(`/api/v1/game/${roomId}/actions`, additionalParams, { 
      params: { uid, cmd } 
    }),
    
    // User
    getUserProfile: (uid) => api.get('/api/v1/user/info', { params: { uid } }),
    updateProfile: (uid, profileData) => api.put('/api/v1/user/profile', null, { 
      params: { uid, ...profileData } 
    }),
    
    // Generic methods
    get: (url) => api.get(url),
    post: (url, data) => api.post(url, data),
    put: (url, data) => api.put(url, data),
    delete: (url) => api.delete(url)
  }
}