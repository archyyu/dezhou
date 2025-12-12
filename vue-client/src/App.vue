<template>
  <div id="app">
    <header class="bg-dark text-white p-3">
      <div class="container">
        <h1 class="h3">Dezhou Puker Game</h1>
      </div>
    </header>
    
    <main class="container mt-4">
      <router-view />
    </main>
    
    <footer class="bg-light text-center p-3 mt-5">
      <p class="mb-0">© 2023 Dezhou Puker. All rights reserved.</p>
    </footer>
  </div>
</template>

<script setup>
// Main app component
</script>

<style>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

main {
  flex: 1;
}
</style>