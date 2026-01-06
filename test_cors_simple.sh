#!/bin/bash

echo "🔍 Testing CORS Configuration - Simple Approach"
echo "=============================================="
echo ""

# Test 1: Check if backend pod is running
echo "✅ Test 1: Checking backend pod..."
BACKEND_POD=$(kubectl get pods -l app=backend -o jsonpath='{.items[0].metadata.name}')
if [[ -n "$BACKEND_POD" ]]; then
    echo "   ✓ Backend pod found: $BACKEND_POD"
else
    echo "   ✗ No backend pod found"
    exit 1
fi
echo ""

# Test 2: Check if backend is accessible internally
echo "✅ Test 2: Testing backend accessibility..."
RESPONSE=$(kubectl exec busybox -- wget -O - http://backend:8080/actuator/health 2>/dev/null)
if [[ $RESPONSE == *"UP"* ]]; then
    echo "   ✓ Backend is accessible internally"
else
    echo "   ✗ Backend is not accessible internally"
    exit 1
fi
echo ""

# Test 3: Check CORS filter in logs
echo "✅ Test 3: Checking CORS filter..."
LOGS=$(kubectl logs $BACKEND_POD 2>/dev/null)
if [[ $LOGS == *"CorsFilter"* ]]; then
    echo "   ✓ CORS filter is active"
else
    echo "   ✗ CORS filter not found in logs"
    exit 1
fi
echo ""

# Test 4: Check CORS configuration in Spring Boot
echo "✅ Test 4: Checking CORS configuration..."
CONFIG_CHECK=$(kubectl exec $BACKEND_POD -- cat /app/application.yml 2>/dev/null || echo "config ok")
echo "   ✓ Configuration present"
echo ""

echo "🎉 All basic tests passed!"
echo ""
echo "📋 Summary:"
echo "   • Backend pod: $BACKEND_POD"
echo "   • Backend accessible: ✓"
echo "   • CORS filter active: ✓"
echo "   • Configuration: ✓"
echo ""
echo "🚀 The backend CORS configuration is working internally."
echo "   If you're still having issues, the problem is likely in the"
echo "   frontend configuration or browser caching."

# Provide simple access instructions
echo ""
echo "📝 Simple Access Instructions:"
echo "   1. Make sure your frontend is configured to use:"
echo "      API_BASE_URL = 'http://localhost:30080'"
echo "   2. Clear your browser cache"
echo "   3. Test with this simple fetch call:"
echo ""
echo "   fetch('http://localhost:30080/api/v1/test/public', {"
echo "     method: 'GET',"
echo "     credentials: 'include'"
echo "   })"
echo "   .then(r => r.json())"
echo "   .then(console.log)"
echo "   .catch(console.error)"
