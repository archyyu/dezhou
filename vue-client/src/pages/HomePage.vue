<template>
  <div class="home-page">
    <div class="jumbotron bg-light p-5 rounded">
      <h1 class="display-4">Welcome to Dezhou Puker!</h1>
      <p class="lead">A modern poker game experience</p>
      <hr class="my-4">
      <p>Join exciting poker games with players from around the world.</p>
      <div class="d-grid gap-2 d-md-flex justify-content-md-start">
        <router-link to="/login" class="btn btn-primary btn-lg px-4 me-md-2">
          Login
        </router-link>
        <router-link to="/rooms" class="btn btn-outline-secondary btn-lg px-4">
          View Rooms
        </router-link>
        <router-link to="/api-test" class="btn btn-info btn-lg px-4 text-white">
          API Test
        </router-link>
      </div>
    </div>
    
    <div class="features mt-5">
      <div class="row">
        <div class="col-md-4 mb-4">
          <div class="card h-100">
            <div class="card-body">
              <h5 class="card-title">Real-time Gameplay</h5>
              <p class="card-text">Play with friends in real-time with WebSocket technology.</p>
            </div>
          </div>
        </div>
        <div class="col-md-4 mb-4">
          <div class="card h-100">
            <div class="card-body">
              <h5 class="card-title">Modern Interface</h5>
              <p class="card-text">Beautiful, responsive design that works on any device.</p>
            </div>
          </div>
        </div>
        <div class="col-md-4 mb-4">
          <div class="card h-100">
            <div class="card-body">
              <h5 class="card-title">Secure & Fair</h5>
              <p class="card-text">Advanced security and fair play algorithms.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// Home page component
</script>

<style scoped>
.home-page {
  max-width: 1200px;
  margin: 0 auto;
}
</style>