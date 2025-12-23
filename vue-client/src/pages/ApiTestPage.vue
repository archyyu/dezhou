<template>
  <div class="container mt-5">
    <h2>API Test Page</h2>
    
    <div class="card mb-4">
      <div class="card-body">
        <h5 class="card-title">Test API Health</h5>
        <button @click="testHealth" class="btn btn-primary" :disabled="loading">
          Test Health Check
        </button>
        <div v-if="healthResult" class="mt-3 alert alert-success">
          {{ healthResult }}
        </div>
      </div>
    </div>

    <div class="card mb-4">
      <div class="card-body">
        <h5 class="card-title">Test Login</h5>
        <div class="mb-3">
          <label class="form-label">Username</label>
          <input v-model="testUsername" type="text" class="form-control" placeholder="testuser">
        </div>
        <div class="mb-3">
          <label class="form-label">Password</label>
          <input v-model="testPassword" type="password" class="form-control" placeholder="password">
        </div>
        <button @click="testLogin" class="btn btn-primary" :disabled="loading">
          Test Login
        </button>
        <div v-if="loginResult" class="mt-3 alert alert-success">
          {{ loginResult }}
        </div>
        <div v-if="loginError" class="mt-3 alert alert-danger">
          {{ loginError }}
        </div>
      </div>
    </div>

    <div class="card">
      <div class="card-body">
        <h5 class="card-title">Test Room List</h5>
        <button @click="testRooms" class="btn btn-primary" :disabled="loading">
          Get Rooms
        </button>
        <div v-if="roomsResult" class="mt-3 alert alert-success">
          {{ roomsResult }}
        </div>
        <div v-if="roomsError" class="mt-3 alert alert-danger">
          {{ roomsError }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useTestApi } from '@/composables/useTestApi'

const { testApiHealth, testLogin, testSimpleGet, testLoginManual } = useTestApi()

const loading = ref(false)
const healthResult = ref('')
const loginResult = ref('')
const loginError = ref('')
const roomsResult = ref('')
const roomsError = ref('')
const testUsername = ref('testuser')
const testPassword = ref('password')

const testHealth = async () => {
  try {
    loading.value = true
    const response = await testApiHealth()
    healthResult.value = JSON.stringify(response.data, null, 2)
  } catch (err) {
    healthResult.value = 'Error: ' + (err.response?.data?.message || err.message)
  } finally {
    loading.value = false
  }
}

const testLogin = async () => {
  try {
    loading.value = true
    loginError.value = ''
    
    const response = await testLogin(testUsername.value, testPassword.value)
    loginResult.value = JSON.stringify(response.data, null, 2)
    
  } catch (err) {
    console.error('Login test error:', err)
    loginError.value = 'Error: ' + (err.response?.data?.message || err.message)
    if (err.response) {
      loginError.value += '\nResponse: ' + JSON.stringify(err.response.data)
    }
  } finally {
    loading.value = false
  }
}

const testRooms = async () => {
  try {
    loading.value = true
    roomsError.value = ''
    
    const response = await testSimpleGet()
    roomsResult.value = JSON.stringify(response.data, null, 2)
    
  } catch (err) {
    roomsError.value = 'Error: ' + (err.response?.data?.message || err.message)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.alert {
  white-space: pre-wrap;
  font-family: monospace;
  font-size: 0.9em;
}
</style>