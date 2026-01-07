# TexasHolder Poker - Kubernetes Deployment for k3s

This directory contains simple Kubernetes manifests for deploying TexasHolder Poker on k3s.

## 🚀 Quick Start

### Prerequisites
- [k3s](https://k3s.io/) installed and running
- [kubectl](https://kubernetes.io/docs/tasks/tools/) configured to work with your k3s cluster
- Docker images built for backend and frontend

### 1. Build Docker Images

First, build the Docker images for your backend and frontend:

```bash
# Build backend image
docker build -t texasholder-backend:latest .

# Build frontend image
cd vue-client
docker build -t texasholder-frontend:latest .
cd ..
```

### 2. Load Images into k3s

```bash
# Load backend image
k3s ctr images import texasholder-backend:latest

# Load frontend image
k3s ctr images import texasholder-frontend:latest
```

### 3. Apply Kubernetes Manifests

Apply the manifests in the correct order:

```bash
# Create namespace
kubectl apply -f k8s/namespace.yaml

# Create MySQL PVC
kubectl apply -f k8s/mysql-pvc.yaml

# Create MySQL ConfigMap with initialization scripts
kubectl apply -f k8s/mysql-configmap.yaml

# Deploy MySQL
kubectl apply -f k8s/mysql-deployment.yaml

# Wait for MySQL to be ready
kubectl wait --for=condition=ready pod -l app=mysql -n texasholder --timeout=300s

# Deploy backend
kubectl apply -f k8s/backend-deployment.yaml

# Wait for backend to be ready
kubectl wait --for=condition=ready pod -l app=backend -n texasholder --timeout=300s

# Deploy frontend
kubectl apply -f k8s/frontend-deployment.yaml

# Wait for frontend to be ready
kubectl wait --for=condition=ready pod -l app=frontend -n texasholder --timeout=300s
```

### 4. Access Your Application

After all services are running:

- **Frontend**: `http://<your-k3s-node-ip>:30080`
- **Backend API**: `http://<your-k3s-node-ip>:30081`

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        TexasHolder Poker on k3s                            │
├─────────────────────────┬─────────────────────────┬─────────────────────────┤
│  MySQL Database         │  Backend Service        │  Frontend Service       │
├─────────────────────────┼─────────────────────────┼─────────────────────────┤
│  • MySQL 8.0            │  • Spring Boot          │  • Vue.js               │
│  • Persistent Storage   │  • REST API             │  • NodePort 30080       │
│  • ClusterIP Service    │  • NodePort 30081       │  • Connects to backend  │
│  • 5GB Storage          │  • Connects to MySQL    │  • Static files         │
└─────────────────────────┴─────────────────────────┴─────────────────────────┘
```

## 📋 Services Overview

### MySQL Database
- **Image**: `mysql:8.0`
- **Service Type**: ClusterIP (internal only)
- **Port**: 3306
- **Storage**: 5GB persistent volume
- **Initialization**: ConfigMap with database schema and test data

### Backend Service
- **Image**: `texasholder-backend:latest`
- **Service Type**: NodePort
- **Ports**: 8080 (internal), 30081 (external)
- **Environment**: 
  - `SPRING_DATASOURCE_URL`: Connects to MySQL service
  - `SPRING_PROFILES_ACTIVE`: production
- **Health Checks**: `/actuator/health` endpoint

### Frontend Service
- **Image**: `texasholder-frontend:latest`
- **Service Type**: NodePort
- **Ports**: 80 (internal), 30080 (external)
- **Environment**:
  - `VITE_API_BASE_URL`: Points to backend NodePort
- **Health Checks**: Root path `/`

## 🎯 Service Dependencies

```
Frontend (30080) → Backend (30081) → MySQL (3306)
```

## 🔧 Troubleshooting

### Check Pod Status
```bash
kubectl get pods -n texasholder
```

### View Pod Logs
```bash
kubectl logs -l app=mysql -n texasholder
kubectl logs -l app=backend -n texasholder
kubectl logs -l app=frontend -n texasholder
```

### Check Service Endpoints
```bash
kubectl get svc -n texasholder
kubectl get endpoints -n texasholder
```

### Check Persistent Volume
```bash
kubectl get pvc -n texasholder
kubectl describe pvc mysql-pvc -n texasholder
```

### Check ConfigMaps
```bash
kubectl get configmaps -n texasholder
kubectl describe configmap mysql-init-scripts -n texasholder
```

## 📊 Monitoring

### Check Resource Usage
```bash
kubectl top pods -n texasholder
kubectl top nodes
```

### Check Events
```bash
kubectl get events -n texasholder
```

## 🧹 Cleanup

To remove all resources:

```bash
kubectl delete namespace texasholder
```

## 🔒 Security Notes

- MySQL is only accessible within the cluster (ClusterIP)
- Backend and frontend use NodePort for external access
- Database credentials are in environment variables (consider using Kubernetes Secrets for production)
- The application uses basic authentication with JWT tokens

## 📈 Scaling

To scale services:

```bash
# Scale backend
kubectl scale deployment backend -n texasholder --replicas=2

# Scale frontend
kubectl scale deployment frontend -n texasholder --replicas=2
```

## 🎯 Next Steps

1. **Add Ingress**: For better routing and SSL termination
2. **Add Monitoring**: Prometheus and Grafana for metrics
3. **Add Logging**: EFK stack for centralized logging
4. **Add CI/CD**: Automate builds and deployments
5. **Add Secrets Management**: Use Kubernetes Secrets or external vault