#!/bin/bash

# TexasHolder Poker - k3s Deployment Script
# This script applies all Kubernetes manifests in the correct order

echo "🚀 Starting TexasHolder Poker deployment on k3s..."

# Create namespace
echo "📦 Creating namespace..."
kubectl apply -f k8s/namespace.yaml

# Create MySQL PVC
echo "💾 Creating MySQL PersistentVolumeClaim..."
kubectl apply -f k8s/mysql-pvc.yaml

# Create MySQL ConfigMap
echo "📄 Creating MySQL ConfigMap with initialization scripts..."
kubectl apply -f k8s/mysql-configmap.yaml

# Deploy MySQL
echo "🐬 Deploying MySQL..."
kubectl apply -f k8s/mysql-deployment.yaml

# Wait for MySQL to be ready
echo "⏳ Waiting for MySQL to be ready..."
kubectl wait --for=condition=ready pod -l app=mysql -n texasholder --timeout=300s

if [ $? -ne 0 ]; then
    echo "❌ MySQL failed to start. Check logs with: kubectl logs -l app=mysql -n texasholder"
    exit 1
fi

# Deploy backend
echo "🖥️  Deploying backend..."
kubectl apply -f k8s/backend-deployment.yaml

# Wait for backend to be ready
echo "⏳ Waiting for backend to be ready..."
kubectl wait --for=condition=ready pod -l app=backend -n texasholder --timeout=300s

if [ $? -ne 0 ]; then
    echo "❌ Backend failed to start. Check logs with: kubectl logs -l app=backend -n texasholder"
    exit 1
fi

# Deploy frontend
echo "🌐 Deploying frontend..."
kubectl apply -f k8s/frontend-deployment.yaml

# Wait for frontend to be ready
echo "⏳ Waiting for frontend to be ready..."
kubectl wait --for=condition=ready pod -l app=frontend -n texasholder --timeout=300s

if [ $? -ne 0 ]; then
    echo "❌ Frontend failed to start. Check logs with: kubectl logs -l app=frontend -n texasholder"
    exit 1
fi

echo "✅ All services deployed successfully!"
echo ""
echo "📋 Service Information:"
kubectl get svc -n texasholder

echo ""
echo "🌐 Access your application:"
echo "  Frontend: http://<your-k3s-node-ip>:30080"
echo "  Backend API: http://<your-k3s-node-ip>:30081"
echo ""
echo "💡 To check pod status: kubectl get pods -n texasholder"
echo "💡 To view logs: kubectl logs -l app=<service-name> -n texasholder"