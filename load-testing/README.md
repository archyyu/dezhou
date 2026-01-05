# TexasHolder Poker - Load Testing Suite

Modern load testing suite using **k6** to validate performance, scalability, and stability of the TexasHolder Poker server.

## 🚀 Structure

```text
load-testing/
├── libs/                  # Reusable libraries
│   ├── api.js             # API client (Login, Room, Game actions)
│   └── utils.js           # Random generators and sleep helpers
├── scripts/               # Test scenarios
│   ├── smoke.js           # Verification test (1 VU)
│   ├── load.js            # Standard load test (ramp-up to 20+ VUs)
│   ├── stress.js          # Stress test (ramp-up to failure point)
│   └── gameplay_flow.js   # Complex simulation (Join -> Play -> Leave)
├── legacy/                # Old scripts and HTML tests
└── README.md              # This file
```

## 🛠️ Prerequisites

1. **Install k6**:
   - macOS: `brew install k6`
   - Linux: `sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69 && echo "deb https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list && sudo apt-get update && sudo apt-get install k6`
   - Windows: `choco install k6`

2. **Server Running**:
   Ensure the TexasHolder server is running at `http://localhost:8080`.

## 🏃 Running Tests

### Smoke Test
Verify that the system is up and basic endpoints work.
```bash
k6 run load-testing/scripts/smoke.js
```

### Load Test
Test the system under expected concurrent load.
```bash
k6 run load-testing/scripts/load.js
```

### Gameplay Simulation
Simulate realistic user behavior (login, join room, play hands, leave).
```bash
k6 run load-testing/scripts/gameplay_flow.js
```

### Custom Configuration
You can override environment variables and k6 options:
```bash
k6 run -e BASE_URL=http://production-api:8080 --vus 50 --duration 5m load-testing/scripts/gameplay_flow.js
```

## 📊 Thresholds

The following performance thresholds are defined across scripts:
- **Success Rate**: > 99% of requests must succeed.
- **Response Time**: 95% of requests must be faster than 500ms (smoke) or 1000ms (load).

## 📄 Documentation

For detailed analysis and methodology, see the [LOAD_TESTING_DOCUMENTATION.md](./LOAD_TESTING_DOCUMENTATION.md).
