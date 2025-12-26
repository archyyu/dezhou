<template>
  <div class="register-page">
    <div class="row justify-content-center">
      <div class="col-md-6 col-lg-4">
        <div class="card shadow">
          <div class="card-body p-4">
            <h2 class="card-title text-center mb-4">Register</h2>
            
            <form @submit.prevent="handleRegister">
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
              
              <div class="mb-3">
                <label for="email" class="form-label">Email</label>
                <input 
                  type="email" 
                  class="form-control" 
                  id="email" 
                  v-model="form.email"
                >
              </div>
              
              <div class="mb-3">
                <label for="name" class="form-label">Full Name</label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="name" 
                  v-model="form.name"
                >
              </div>
              
              <button type="submit" class="btn btn-primary w-100" :disabled="loading">
                <span v-if="loading" class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                <span v-else>Register</span>
              </button>
              
              <div v-if="error" class="alert alert-danger mt-3">
                {{ error }}
              </div>
            </form>
            
            <div class="text-center mt-3">
              <p class="mb-0">Already have an account? <router-link to="/login">Login</router-link></p>
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
const { register } = useApi()

const form = ref({
  account: '',
  password: '',
  email: '',
  name: ''
})

const loading = ref(false)
const error = ref('')

const handleRegister = async () => {
  try {
    loading.value = true
    error.value = ''
    
    const response = await register({
      name: form.value.account,
      password: form.value.password,
      email: form.value.email,
      // Additional registration fields
      gendar: 'male',
      birthday: '1990-01-01'
    })
    
    console.log('Registration response:', response.data)
    
    // Handle the API response format correctly
    if (response.data && response.data.success && response.data.data) {
      // New API response format
      localStorage.setItem('token', response.data.data.token)
      localStorage.setItem('user', JSON.stringify(response.data.data.user))
      
      // Redirect to rooms after successful registration
      router.push('/rooms')
    } else if (response.data && response.data.token) {
      // Legacy response format
      localStorage.setItem('token', response.data.token)
      localStorage.setItem('user', JSON.stringify(response.data.user))
      
      // Redirect to rooms after successful registration
      router.push('/rooms')
    } else {
      throw new Error('Registration failed: ' + JSON.stringify(response.data))
    }
    
  } catch (err) {
    console.error('Registration error:', err)
    if (err.response && err.response.data) {
      error.value = err.response.data.message || 'Registration failed: ' + JSON.stringify(err.response.data)
    } else if (err.message) {
      error.value = err.message
    } else {
      error.value = 'Registration failed: ' + err
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 70vh;
  display: flex;
  align-items: center;
}

.card {
  border: none;
}
</style>