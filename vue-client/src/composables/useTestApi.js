import axios from 'axios'

/**
 * Test API composable for debugging
 */
export function useTestApi() {
  const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
    headers: {
      'Content-Type': 'application/json'
    }
  })

  return {
    // Test if API is reachable
    testApiHealth: () => api.get('/health'),
    
    // Test login with specific parameters
    testLogin: (username, password) => api.post('/api/v1/user/login', null, { 
      params: { name: username, password: password }
    }),
    
    // Test room type list
    testRoomTypeList: () => api.get('/api/v1/room/roomTypeList'),
    
    // Test rooms by type
    testRoomsByType: (roomTypeId) => api.get(`/api/v1/room/${roomTypeId}/list`),
    
    // Test simple GET request
    testSimpleGet: () => api.get('/api/v1/room/list'),
    
    // Test with manual URL construction
    testLoginManual: (username, password) => {
      const url = `/api/v1/user/login?name=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`
      return api.post(url)
    }
  }
}