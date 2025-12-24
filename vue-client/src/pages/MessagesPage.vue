<template>
  <div class="messages-page container mt-4">
    <h2 class="mb-4">Messages</h2>
    
    <div class="card">
      <div class="card-body">
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h5 class="card-title mb-0">Your Messages</h5>
          <button @click="refreshMessages" class="btn btn-outline-primary" :disabled="loading">
            <i class="bi bi-arrow-clockwise"></i> Refresh
          </button>
        </div>
        
        <div v-if="loading" class="text-center my-4">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Loading...</span>
          </div>
        </div>
        
        <div v-if="error" class="alert alert-danger">
          {{ error }}
        </div>
        
        <div v-if="!loading && messages.length === 0" class="alert alert-info">
          No messages found.
        </div>
        
        <!-- Message List -->
        <div class="message-list">
          <div v-for="message in messages" :key="message.id" class="card mb-3 message-card">
            <div class="card-body">
              <div class="d-flex justify-content-between">
                <div>
                  <h6 class="card-subtitle mb-2 text-muted">
                    {{ formatDate(message.timestamp) }}
                  </h6>
                  <p class="card-text">{{ message.content }}</p>
                </div>
                <div class="text-end">
                  <span class="badge bg-primary">
                    {{ message.type }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Send Message Section -->
    <div class="card mt-4">
      <div class="card-body">
        <h5 class="card-title">Send Message</h5>
        
        <div class="mb-3">
          <label class="form-label">Message Type</label>
          <select v-model="newMessage.type" class="form-select">
            <option value="GENERAL">General</option>
            <option value="GAME">Game Related</option>
            <option value="SUPPORT">Support</option>
          </select>
        </div>
        
        <div class="mb-3">
          <label class="form-label">Content</label>
          <textarea 
            v-model="newMessage.content" 
            class="form-control" 
            rows="3" 
            placeholder="Enter your message..."
          ></textarea>
        </div>
        
        <button 
          @click="sendMessage" 
          class="btn btn-primary" 
          :disabled="loading || !newMessage.content.trim()"
        >
          Send Message
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useApi } from '@/composables/useApi'

const { getMessages, sendMessage } = useApi()

const messages = ref([])
const loading = ref(false)
const error = ref('')

const newMessage = ref({
  type: 'GENERAL',
  content: ''
})

onMounted(() => {
  loadMessages()
})

const loadMessages = async () => {
  try {
    loading.value = true
    error.value = ''
    
    const response = await getMessages()
    messages.value = response.data || []
    
  } catch (err) {
    error.value = 'Failed to load messages: ' + (err.response?.data?.message || err.message)
  } finally {
    loading.value = false
  }
}

const refreshMessages = () => {
  loadMessages()
}

const sendMessage = async () => {
  try {
    loading.value = true
    error.value = ''
    
    const response = await sendMessage({
      type: newMessage.value.type,
      content: newMessage.value.content
    })
    
    // Clear form
    newMessage.value.content = ''
    
    // Refresh messages
    await loadMessages()
    
  } catch (err) {
    error.value = 'Failed to send message: ' + (err.response?.data?.message || err.message)
  } finally {
    loading.value = false
  }
}

const formatDate = (timestamp) => {
  if (!timestamp) return 'Unknown time'
  
  const date = new Date(timestamp)
  return date.toLocaleString()
}
</script>

<style scoped>
.messages-page {
  max-width: 1200px;
  margin: 0 auto;
}

.message-card {
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: pointer;
}

.message-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.message-list {
  max-height: 500px;
  overflow-y: auto;
}
</style>