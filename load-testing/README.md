# TexasHolder Poker - Load Testing

This directory contains all load testing scripts and documentation for the TexasHolder Poker application.

## 📁 Directory Structure

```
load-testing/
├── load_test.js              # Main k6 load test script
├── LOAD_TESTING_DOCUMENTATION.md # Comprehensive load testing guide
├── test-game-process.html     # Game process test HTML
├── test-login.html           # Login functionality test HTML
├── test-websocket.html       # WebSocket functionality test HTML
├── test-websocket-security.html # WebSocket security test HTML
├── test-simple.html          # Simple test HTML
├── test-vite.js             # Vite test script
└── test-websocket-fix.html   # WebSocket fix test HTML
```

## 🚀 Quick Start

### Prerequisites
- [k6](https://k6.io/) installed (for load testing)
- Modern web browser (for HTML tests)
- Node.js (for JavaScript tests)

### Running Load Tests

```bash
# Install k6 (if not already installed)
brew install k6  # macOS

# Run the main load test
k6 run load_test.js

# Run with specific options
k6 run --vus 50 --duration 30s load_test.js
```

## 📋 Test Files Description

### `load_test.js`
Main k6 load testing script that simulates multiple virtual users interacting with the poker game API.

**Features:**
- Configurable virtual users (VUs)
- Multiple test scenarios
- Performance metrics collection
- Threshold-based validation

### `LOAD_TESTING_DOCUMENTATION.md`
Comprehensive guide covering:
- Load testing methodology
- Test scenarios and setup
- Performance metrics analysis
- Result interpretation
- Best practices

### HTML Test Files
- **`test-game-process.html`**: Tests complete game flow
- **`test-login.html`**: Tests authentication functionality
- **`test-websocket.html`**: Tests WebSocket connections
- **`test-websocket-security.html`**: Tests WebSocket security
- **`test-simple.html`**: Simple functionality test
- **`test-websocket-fix.html`**: WebSocket fix verification

### `test-vite.js`
Vite-specific test script for frontend testing.

## 🎯 Load Testing Scenarios

### 1. Basic API Testing
```javascript
// Example from load_test.js
import http from 'k6/http';
import { check, sleep } from 'k6';

export default function () {
  // Test API endpoints
  let res = http.get('http://localhost:8080/api/health');
  check(res, {
    'status is 200': (r) => r.status === 200,
  });
  sleep(1);
}
```

### 2. Game Process Testing
Tests the complete poker game flow including:
- Player registration and login
- Room creation and joining
- Game actions (bet, fold, check, raise)
- Game completion and results

### 3. WebSocket Testing
Tests real-time WebSocket functionality:
- Connection establishment
- Message sending/receiving
- Game state updates
- Error handling

### 4. Stress Testing
Simulates high load scenarios:
```bash
# 100 virtual users for 5 minutes
k6 run --vus 100 --duration 300s load_test.js
```

## 📊 Performance Metrics

Key metrics collected during load tests:

- **Request Rate**: Requests per second
- **Response Time**: Average, median, 95th percentile
- **Error Rate**: Failed request percentage
- **Throughput**: Data transfer rate
- **Virtual Users**: Concurrent user simulation

## 🔧 Configuration

### k6 Options

| Option | Description | Example |
|--------|-------------|---------|
| `--vus` | Virtual users | `--vus 50` |
| `--duration` | Test duration | `--duration 30s` |
| `--rps` | Requests per second | `--rps 100` |
| `--stage` | Ramp-up stages | `--stage 10s:10,30s:50` |

### Environment Setup

```bash
# Set environment variables
export API_BASE_URL="http://localhost:8080"
export TEST_DURATION="60s"
export VIRTUAL_USERS="25"
```

## 📈 Test Results Analysis

### Sample Output
```
          /\      |‾‾|  /‾‾/  /‾/
     /
    /  \     |  |_/  /  / /
   /___\    |      |  /  /__\
  /____ \   |  |\/|  /  /____\
 /      \  |__|  |__/______/

  execution: local
     script: load_test.js
     output: -

  scenarios: (100.00%) 1 scenario, 50 max VUs, 1m30s max duration (incl. graceful stop):
           * default: 50 looping VUs for 1m0s (gracefulStop: 30s)

  ✓ status is 200
  ✓ status is 201
  ✓ response time < 500ms

  checks.........................: 100.00% ✓ 15000     ✗ 0
  data_received..................: 12 MB   200 kB/s
  data_sent......................: 2.1 MB 35 kB/s
  http_req_blocked...............: avg=1.23ms  min=1µs    med=1µs    max=10ms
  http_req_connecting............: avg=1.12ms  min=0s     med=0s     max=5ms
  http_req_duration..............: avg=45.2ms  min=10ms   med=35ms   max=200ms
  http_req_failed................: 0.00%   ✓ 0        ✗ 15000
  http_req_receiving.............: avg=120µs  min=50µs   med=100µs max=1ms
  http_req_sending...............: avg=50µs   min=10µs   med=40µs   max=500µs
  http_req_tls_handshaking.......: avg=0s     min=0s     med=0s     max=0s
  http_req_waiting...............: avg=45ms   min=10ms   med=35ms   max=200ms
  http_reqs......................: 15000   250.123457/s
  iteration_duration.............: avg=1.05s   min=1s     med=1s     max=1.5s
  iterations.....................: 15000   250.123457/s
  vus............................: 50      min=50      max=50
  vus_max........................: 50      min=50      max=50
```

## 🛠️ Advanced Testing

### 1. Ramp-up Testing
```bash
k6 run --stage 10s:10,30s:50,1m:100,30s:50,10s:10 load_test.js
```

### 2. Spike Testing
```bash
k6 run --stage 10s:10,10s:100,10s:10 load_test.js
```

### 3. Soak Testing
```bash
k6 run --duration 1h --vus 20 load_test.js
```

## 📚 Best Practices

### 1. Test Environment
- Use a dedicated test environment
- Ensure database is properly seeded
- Monitor server resources during tests

### 2. Test Design
- Start with small loads and increase gradually
- Test individual components before full system
- Include realistic think times
- Validate both success and failure scenarios

### 3. Result Analysis
- Compare results against baselines
- Identify performance bottlenecks
- Analyze error patterns
- Monitor resource utilization

## 🔒 Security Testing

The WebSocket security tests verify:
- Proper authentication
- Message validation
- Connection security
- Error handling

## 🤝 Contributing

If you add new load tests:

1. **Document the test purpose** in the file header
2. **Add to this README** with usage instructions
3. **Include sample output** for reference
4. **Update documentation** with new scenarios

## 📖 Additional Resources

- [k6 Documentation](https://k6.io/docs/)
- [Load Testing Guide](https://k6.io/docs/test-types/load-testing/)
- [Performance Testing Best Practices](https://www.guru99.com/performance-testing.html)

---

**Happy Load Testing!** 🚀📊