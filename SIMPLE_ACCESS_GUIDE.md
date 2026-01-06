# 🎯 Simple Texas Holder Access Guide

## 🚀 Simplified Architecture

```
Client → Backend (NodePort 30880)
Client → Frontend (NodePort 30888)
```

## 📋 Access Information

### Backend Service
- **URL**: `http://<your-k3s-node-ip>:30880`
- **Example**: `http://localhost:30880/api/v1/test/public`
- **CORS Configured**: ✅ Yes (allows `http://localhost:8888`)

### Frontend Service  
- **URL**: `http://<your-k3s-node-ip>:30888`
- **Example**: `http://localhost:30888`

## 🎯 How to Access from Your Frontend

### JavaScript Example

```javascript
// Make a request from your frontend (running on http://localhost:8888)
fetch('http://localhost:30880/api/v1/user/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    name: 'user1',
    password: '1212'
  }),
  credentials: 'include' // Important for CORS with credentials
})
.then(response => {
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
  return response.json();
})
.then(data => {
  console.log('Success:', data);
})
.catch(error => {
  console.error('Error:', error);
});
```

### cURL Test

```bash
# Test CORS headers
curl -I -X OPTIONS \
  -H "Origin: http://localhost:8888" \
  -H "Access-Control-Request-Method: POST" \
  http://localhost:30880/api/v1/user/login

# Test actual API call
curl -X POST \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:8888" \
  -d '{"name":"user1","password":"1212"}' \
  http://localhost:30880/api/v1/user/login
```

## 🔧 CORS Configuration Summary

### Allowed Origins
- `http://localhost:5173` (Vue.js default)
- `http://localhost:5174` (Vue.js alternative)  
- `http://localhost:3000` (React/Node.js)
- `http://localhost:8080` (Spring Boot)
- `http://localhost:8880` (Alternative backend)
- `http://localhost:8888` (Your frontend) ✨
- `http://127.0.0.1:5173` (Alternative format)

### CORS Headers You Should See
```
Access-Control-Allow-Origin: http://localhost:8888
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
Access-Control-Allow-Headers: Authorization, Content-Type, Accept, X-Requested-With
Access-Control-Allow-Credentials: true
Access-Control-Max-Age: 3600
```

## 🐛 Troubleshooting

### If You Still See CORS Errors

1. **Clear browser cache** - Old CORS headers might be cached
2. **Check browser console** - Look for specific error messages
3. **Test with Postman/cURL** - Verify the headers are present
4. **Check network tab** - Confirm the `Origin` header is being sent

### Common Issues

- **Missing Origin header**: Ensure your frontend sends the `Origin: http://localhost:8888` header
- **Credentials issue**: Use `credentials: 'include'` in fetch requests
- **Port conflicts**: Make sure nothing else is using ports 30880 or 30888

## 🎉 Expected Result

When you make a request from `http://localhost:8888` to `http://localhost:30880`, you should:

1. ✅ See CORS headers in the response
2. ✅ Get a successful response (no CORS errors)
3. ✅ Be able to make authenticated requests with JWT

## 📝 Kubernetes Service Summary

```bash
# Backend
kubectl get service backend
# Output: backend   NodePort   10.43.61.180   <none>        8080:30880/TCP

# Frontend  
kubectl get service frontend
# Output: frontend   NodePort   10.43.125.157   <none>        80:30888/TCP
```

This simple setup eliminates the complexity of proxy servers and gives you direct access to your services with proper CORS configuration!