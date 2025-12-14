import http from 'k6/http';
import { check, sleep } from 'k6';

// Configuration
const BASE_URL = 'http://localhost:8080';
const ROOM_LIST_ENDPOINT = '/api/v1/room/list';

// Test configuration
export const options = {
  stages: [
    // Ramp-up from 1 to 50 users over 30 seconds
    { duration: '30s', target: 50 },
    // Stay at 50 users for 1 minute
    { duration: '1m', target: 50 },
    // Ramp-up to 100 users over 30 seconds
    { duration: '30s', target: 100 },
    // Stay at 100 users for 2 minutes
    { duration: '2m', target: 100 },
    // Ramp-down to 0 users over 30 seconds
    { duration: '30s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'], // 95% of requests should be below 500ms
    http_req_failed: ['rate<0.01'],   // Less than 1% failed requests
  },
};

// Main test function
export default function () {
  // Test the room list endpoint
  const res = http.get(BASE_URL + ROOM_LIST_ENDPOINT);
  
  // Verify the response
  check(res, {
    'status is 200': (r) => r.status === 200,
    'response time < 500ms': (r) => r.timings.duration < 500,
    'response has data': (r) => r.json() && r.json().data,
  });
  
  // Add a small delay between requests
  sleep(0.5);
}