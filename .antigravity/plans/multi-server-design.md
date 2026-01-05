# Implementation Plan - Multi-Server Selection

This plan outlines the design and steps for allowing the Vue client to connect to different game servers dynamically.

## 1. Architecture Design

### Central Server Store
Create a Pinia store (`serverStore.js`) to manage the application's connection point.
*   **State:**
    *   `baseUrl`: The current active server URL (e.g., `http://localhost:8080`).
    *   `availableServers`: A list of predefined server options.
*   **Persistence:** Sync the `baseUrl` with `localStorage` to remember the selection across sessions.

### Dynamic API Layer
Modify the `useApi` composable to reactively use the `baseUrl` from the `serverStore`.
*   Instead of a hardcoded `VITE_API_BASE_URL`, every request should fetch the latest URL from the store.
*   Use an Axios interceptor to set the `config.baseURL` dynamically for every request.

### Dynamic WebSocket Layer
Update the `useWebSocket` composable to derive the `/ws` endpoint from the active server URL.
*   Ensure that when the server changes, the existing WebSocket connection is disconnected and re-established to the new host.

## 2. User Experience Design

### Selection UI
Add a "Server Picker" component, most logically placed on the **Login Page**.
*   **Why Login Page?** Authentication (JWT tokens) is server-specific. A user must select the server they want to play on *before* logging in.
*   **Interaction:** A dropdown or list of cards showing server names, status (ping/online), and current population if available.

### Custom Server Option
Allow power users to input a manual URL (e.g., for local development or private servers).

## 3. Implementation Steps

1.  **Define Store:** Create `src/stores/serverStore.js`.
2.  **Update API Composable:** Modify `src/composables/useApi.js` to use the store's URL.
3.  **Update WebSocket Composable:** Modify `src/composables/useWebSocket.js`.
4.  **Enhance Login Page:** Add the selection UI to `src/pages/LoginPage.vue`.
5.  **Add Validation:** Ensure the client can "ping" a server info endpoint to verify it's reachable before switching.

## 4. Verification Plan
*   Start two server instances on different ports (e.g., 8080 and 8081).
*   Switch servers on the login page.
*   Verify that API calls and WebSockets target the correct port.
*   Verify that refreshing the page keeps the selected server active.
