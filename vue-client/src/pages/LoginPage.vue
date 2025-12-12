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

const router = useRouter()
const { login } = useApi()

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
      account: form.value.account,
      password: form.value.password
    })
    
    // Store token
    localStorage.setItem('token', response.data.token)
    localStorage.setItem('user', JSON.stringify(response.data.user))
    
    // Redirect to rooms
    router.push('/rooms')
    
  } catch (err) {
    error.value = err.response?.data?.message || 'Login failed'
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