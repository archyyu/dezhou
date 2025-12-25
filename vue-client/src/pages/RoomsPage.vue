<template>
  <div class="rooms-page container mt-4">
    <h2 class="mb-4">Game Rooms</h2>
    
    <!-- Room Type Filter -->
    <div class="card mb-4">
      <div class="card-body">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h5 class="card-title mb-0">Room Types</h5>
          <button @click="createRoomNow" class="btn btn-success" :disabled="loading">
            Create Room
          </button>
        </div>
        
        <div class="room-types mb-3">
          <button 
            v-for="roomType in roomTypes" 
            :key="roomType.id" 
            @click="selectRoomType(roomType.id)" 
            class="btn me-2 mb-2" 
            :class="{
              'btn-primary': selectedRoomType === roomType.id,
              'btn-outline-primary': selectedRoomType !== roomType.id
            }"
          >
            {{ roomType.name }} ({{ roomTypeCount[roomType.id] || 0 }})
          </button>
        </div>
        
        <div v-if="loadingRoomTypes" class="text-center">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Loading...</span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Room List -->
    <div class="card">
      <div class="card-body">
        <h5 class="card-title">Available Rooms</h5>
        
        <div v-if="loadingRooms" class="text-center my-4">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Loading...</span>
          </div>
        </div>
        
        <div v-if="error" class="alert alert-danger">
          {{ error }}
        </div>
        
        <div v-if="!loadingRooms && rooms.length === 0" class="alert alert-info">
          No rooms available for this type. Create one!
        </div>
        
        <div class="room-list">
          <div v-for="room in rooms" :key="room.roomid" class="card mb-3 room-card">
            <div class="card-body">
              <div class="d-flex justify-content-between align-items-center">
                <div>
                  <h5 class="card-title mb-1">{{ room.name }}</h5>
                  <p class="card-text mb-1">
                    <span class="badge bg-secondary me-2">{{ room.roomTypeName }}</span>
                    <span class="text-muted">Players: {{ 1 }} / {{ room.maxPlayers }}</span>
                  </p>
                </div>
                <div>
                  <button 
                    @click="joinRoomNow(room.roomid)" 
                    class="btn btn-primary btn-sm" 
                    :disabled="loading || 1 >= room.maxPlayers"
                  >
                    {{ 1 >= room.maxPlayers ? 'Full' : 'Join' }}
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Create Room Modal -->
    <div class="modal fade" id="createRoomModal" tabindex="-1" aria-labelledby="createRoomModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="createRoomModalLabel">Create New Room</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">Room Type</label>
              <select v-model="newRoomTypeId" class="form-select">
                <option v-for="roomType in roomTypes" :key="roomType.id" :value="roomType.id">
                  {{ roomType.name }}
                </option>
              </select>
            </div>
            <div class="mb-3">
              <label class="form-label">Room Name</label>
              <input v-model="newRoomName" type="text" class="form-control" placeholder="My Poker Room">
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
            <button type="button" class="btn btn-primary" @click="confirmCreateRoom" :disabled="loading">
              Create Room
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useApi } from '@/composables/useApi'
import { Modal } from 'bootstrap'

const { getRoomTypeList, getRoomsByType, createRoom, joinRoom } = useApi()
const router = useRouter()

const roomTypes = ref([])
const rooms = ref([])
const selectedRoomType = ref(null)
const loadingRoomTypes = ref(false)
const loadingRooms = ref(false)
const loading = ref(false)
const error = ref('')

// Create room modal state
const newRoomTypeId = ref(null)
const newRoomName = ref('')
let createRoomModal = null

// Room type counts
const roomTypeCount = computed(() => {
  const counts = {}
  console.log(rooms)
  rooms.value.forEach(room => {
    counts[room.roomTypeId] = (counts[room.roomTypeId] || 0) + 1
  })
  return counts
})

// Load room types on mount
onMounted(async () => {
  await loadRoomTypes()
  createRoomModal = new Modal(document.getElementById('createRoomModal'))
})

const loadRoomTypes = async () => {
  try {
    loadingRoomTypes.value = true
    error.value = ''
    const response = await getRoomTypeList()
    roomTypes.value = response.data.data
    
    // Select first room type by default
    if (roomTypes.value.length > 0) {
      selectRoomType(roomTypes.value[0].id)
    }
  } catch (err) {
    error.value = 'Failed to load room types: ' + (err.response?.data?.message || err.message)
  } finally {
    loadingRoomTypes.value = false
  }
}

const selectRoomType = async (roomTypeId) => {
  selectedRoomType.value = roomTypeId
  await loadRoomsForType(roomTypeId)
}

const loadRoomsForType = async (roomTypeId) => {
  try {
    loadingRooms.value = true
    error.value = ''
    rooms.value = []
    const response = await getRoomsByType(roomTypeId)
    rooms.value = response.data.data
    console.log(rooms)
  } catch (err) {
    error.value = 'Failed to load rooms: ' + (err.response?.data?.message || err.message)
  } finally {
    loadingRooms.value = false
  }
}

const createRoomNow = () => {
  if (roomTypes.value.length > 0) {
    newRoomTypeId.value = roomTypes.value[0].id
    newRoomName.value = `Room ${Math.floor(Math.random() * 1000)}`
    createRoomModal.show()
  }
}

const confirmCreateRoom = async () => {
  try {
    loading.value = true
    error.value = ''
    
    const response = await createRoom(newRoomTypeId.value, newRoomName.value)
    
    // Refresh room list
    await loadRoomsForType(selectedRoomType.value)
    
    // Close modal
    createRoomModal.hide()
  } catch (err) {
    error.value = 'Failed to create room: ' + (err.response?.data?.message || err.message)
  } finally {
    loading.value = false
  }
}

const joinRoomNow = async (roomId) => {
  try {
    loading.value = true
    error.value = ''
    
    const response = await joinRoom(roomId)
    
    // Navigate to game page
    router.push({ name: 'game', params: { roomId } })
  } catch (err) {
    error.value = 'Failed to join room: ' + (err.response?.data?.message || err.message)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.rooms-page {
  max-width: 1200px;
  margin: 0 auto;
}

.room-card {
  transition: transform 0.2s, box-shadow 0.2s;
}

.room-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.room-types {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
</style>