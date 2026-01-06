import axios from 'axios'
import { useAuthStore } from '@/stores/authStore'

/**
 * API composable for making HTTP requests to the backend
 */
export function useApi() {
  // Create axios instance
  // Use empty string as default to allow Nginx proxy to handle the request (avoiding CORS)
  const apiBaseURL = import.meta.env.VITE_API_BASE_URL || ''
  console.log('API Base URL:', apiBaseURL) // Debug logging
  
  const api = axios.create({
    baseURL: apiBaseURL,
    headers: {
      'Content-Type': 'application/json'
    }
  })

  // Add request interceptor for authentication using Pinia store
  api.interceptors.request.use(config => {
    const authStore = useAuthStore()
    const token = authStore.token
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
    
    // Room management
    getRoomTypeList: () => api.get('/api/v1/room/roomTypeList'),
    getRoomsByType: (roomTypeId) => api.get(`/api/v1/room/${roomTypeId}/list`),
    getAllRooms: () => api.get('/api/v1/room/list'),
    joinRoom: (roomId) => api.post(`/api/v1/room/${roomId}/join`),
    leaveRoom: (roomId) => api.post(`/api/v1/room/${roomId}/action/106`),
    createRoom: (roomTypeId, roomName) => api.post(`/api/v1/room/create/${roomTypeId}/${roomName}`),
    getRoomInfo: (roomId) => api.get(`/api/v1/room/info/${roomId}`),
    
    // Game actions
    gameAction: (roomId, cmd, additionalParams) => api.post(`/api/v1/game/${roomId}/action/${cmd}`, additionalParams),
    
    // User
    getUserProfile: (uid) => api.get(`/api/v1/user/info/${uid}`),
    updateProfile: (profileData) => api.put('/api/v1/user/profile', null, { 
      params: { ...profileData }
    }),
    
    // Message handling
    getMessages: () => api.get('/api/v1/user/messages'),
    sendMessage: (message) => api.post('/api/v1/user/messages', message),
    
    // Generic methods
    get: (url) => api.get(url),
    post: (url, data) => api.post(url, data),
    put: (url, data) => api.put(url, data),
    delete: (url) => api.delete(url)
  }
}