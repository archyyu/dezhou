// Polyfill for global object in browser environments
// This fixes the "global is not defined" error with SockJS

if (typeof global === 'undefined') {
  // Define global object for browser environment
  window.global = window;
  
  // Also ensure process is defined (some libraries expect this)
  if (typeof window.process === 'undefined') {
    window.process = {
      env: {},
      nextTick: setTimeout,
      version: ''
    };
  }
}

// Ensure Buffer is available (some WebSocket libraries need this)
if (typeof window.Buffer === 'undefined') {
  // Use a minimal Buffer polyfill for browser
  window.Buffer = {
    from: (data) => new Uint8Array(data),
    alloc: (size) => new Uint8Array(size),
    isBuffer: (obj) => obj instanceof Uint8Array
  };
}

console.log('Global polyfill applied for SockJS compatibility');