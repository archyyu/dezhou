<template>
  <div class="login-page">
    <div class="row justify-content-center">
      <div class="col-md-6 col-lg-4">
        <div class="card shadow">
          <div class="card-body p-4">
            <h2 class="card-title text-center mb-4">Login</h2>
            
            <form @submit.prevent="handleLogin">
              <div class="mb-3">
                <label for="account" class="form-label">Account</label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="account" 
                  v-model="form.account" 
                  required
                >
              </div>
              
              <div class="mb-3">
                <label for="password" class="form-label">Password</label>
                <input 
                  type="password" 
                  class="form-control" 
                  id="password" 
                  v-model="form.password" 
                  required
                >
              </div>
              
              <div class="mb-3 form-check">
                <input type="checkbox" class="form-check-input" id="remember" v-model="form.remember">
                <label class="form-check-label" for="remember">Remember me</label>
              </div>
              
              <button type="submit" class="btn btn-primary w-100" :disabled="loading">
                <span v-if="loading" class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                <span v-else>Login</span>
              </button>
              
              <div v-if="error" class="alert alert-danger mt-3">
                {{ error }}
              </div>
            </form>
            
            <div class="text-center mt-3">
              <p class="mb-0">Don't have an account? <router-link to="/register">Register</router-link></p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useApi } from '@/composables/useApi'
import { useAuth } from '@/composables/useAuth'

const router = useRouter()
const { login } = useApi()
const { initializeAuth, getCurrentUser } = useAuth()

const form = ref({
  account: '',
  password: '',
  remember: false
})

const loading = ref(false)
const error = ref('')

const handleLogin = async () => {
  try {
    loading.value = true
    error.value = ''
    
    const response = await login({
      name: form.value.account,
      password: form.value.password
    })
    
    console.log('Login response:', response.data) // Debug logging
    
    // Handle the API response format correctly
    if (response.data && response.data.success && response.data.data) {
      // New API response format - use pure Pinia approach
      // Store in localStorage for persistence across page refreshes
      localStorage.setItem('token', response.data.data.token)
      localStorage.setItem('user', JSON.stringify(response.data.data.user))
      
      // Initialize Pinia auth store
      initializeAuth(response.data.data.token, response.data.data.user)
    } else if (response.data && response.data.token) {
      // Legacy response format - use pure Pinia approach
      // Store in localStorage for persistence across page refreshes
      localStorage.setItem('token', response.data.token)
      localStorage.setItem('user', JSON.stringify(response.data.user))
      
      // Initialize Pinia auth store
      initializeAuth(response.data.token, response.data.user)
    } else {
      throw new Error('Invalid response format: ' + JSON.stringify(response.data))
    }
    
    // Redirect to rooms
    router.push('/rooms')
    
  } catch (err) {
    console.error('Login error:', err)
    if (err.response && err.response.data) {
      error.value = err.response.data.message || 'Login failed: ' + JSON.stringify(err.response.data)
    } else if (err.message) {
      error.value = err.message
    } else {
      error.value = 'Login failed: ' + err
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 70vh;
  display: flex;
  align-items: center;
}

.card {
  border: none;
}
</style>