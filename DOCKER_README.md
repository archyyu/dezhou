# Dezhou Poker - Docker Setup Guide

This guide explains how to set up and run the Dezhou Poker application using Docker and Docker Compose.

## 🐳 Docker Architecture

The application consists of three services:

```
┌───────────────────────────────────────────────────────────────┐
│                        DOCKER COMPOSE                          │
├─────────────────┬─────────────────┬───────────────────────────┤
│  MySQL Database  │  Spring Boot    │   Vue.js Frontend         │
│  (Port: 33306)  │  Backend        │   (Port: 80, 443)         │
│                 │  (Port: 8080)   │                           │
└─────────────────┴─────────────────┴───────────────────────────┘
```

## 🚀 Quick Start

### Prerequisites
- Docker (v20.10+ recommended)
- Docker Compose (v1.29+ recommended)
- At least 4GB RAM available for Docker

### 1. Build and Start the Application

```bash
# Build and start all services
docker-compose up --build
```

### 2. Access the Application

- **Frontend**: `http://localhost` (Nginx serves the Vue.js app)
- **Backend API**: `http://localhost:8080` (Spring Boot backend)
- **MySQL**: `localhost:33306` (Database)

### 3. Stop the Application

```bash
# Stop all services
docker-compose down
```

## 📦 Service Details

### MySQL Database
- **Image**: `mysql:8.0`
- **Ports**: `33306:3306`
- **Credentials**:
  - Root: `root/aida87014999`
  - User: `dezhou_user/dezhou_pass`
  - Database: `dezhou`
- **Features**:
  - Persistent volume for data
  - Health checks
  - Initial database schema loading

### Spring Boot Backend
- **Build**: Multi-stage Maven build
- **Ports**: `8080:8080`
- **Features**:
  - Connects to MySQL database
  - REST API endpoints
  - WebSocket support for real-time game updates
  - Health checks
  - Production profile

### Vue.js Frontend
- **Build**: Multi-stage Node.js + Nginx
- **Ports**: `80:80` (HTTP), `443:443` (HTTPS)
- **Features**:
  - Nginx web server
  - API proxy to backend
  - WebSocket proxy configuration
  - Static file serving with caching
  - Security headers
  - Gzip compression

## 🛠️ Development Workflow

### 1. Backend Development

```bash
# Build only backend
docker-compose build backend

# Start backend with MySQL
docker-compose up backend mysql

# Access backend directly
http://localhost:8080
```

### 2. Frontend Development

```bash
# Build only frontend
docker-compose build frontend

# Start frontend (requires backend running)
docker-compose up frontend

# Access frontend
http://localhost
```

### 3. Database Management

```bash
# Connect to MySQL container
docker exec -it dezhou-mysql mysql -u root -p

# Import database dump
cat database.sql | docker exec -i dezhou-mysql mysql -u root -paida87014999 dezhou
```

## 🔧 Configuration

### Environment Variables

#### Backend (Spring Boot)
- `SPRING_DATASOURCE_URL`: MySQL connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `SPRING_PROFILES_ACTIVE`: Active Spring profile (production)

#### Frontend (Vue.js)
- `VITE_API_BASE_URL`: Backend API base URL

### Customizing Ports

Edit `docker-compose.yml` to change ports:

```yaml
services:
  backend:
    ports:
      - "8081:8080"  # Change backend port
  frontend:
    ports:
      - "8080:80"    # Change frontend port
  mysql:
    ports:
      - "3307:3306"  # Change MySQL port
```

## 🧪 Health Checks

All services include health checks:

- **MySQL**: `docker-compose ps` shows healthy status
- **Backend**: `http://localhost:8080/actuator/health`
- **Frontend**: `http://localhost/`

## 🔒 Security Considerations

### Production Recommendations

1. **Database Security**:
   - Change default passwords
   - Consider using Docker secrets for sensitive data

2. **Frontend Security**:
   - Add SSL/TLS certificates
   - Configure proper CORS settings
   - Set up rate limiting

3. **Network Security**:
   - Use a reverse proxy (Nginx, Traefik)
   - Implement proper authentication
   - Set up firewall rules

## 📈 Scaling

### For Production Deployment

```yaml
# Example for scaling backend services
services:
  backend:
    deploy:
      replicas: 2
      resources:
        limits:
          cpus: '1.0'
          memory: '1G'
```

## 🐛 Troubleshooting

### Common Issues

1. **Port Conflicts**:
   ```bash
   # Check for port conflicts
   netstat -tuln | grep 8080
   
   # Fix: Change ports in docker-compose.yml
   ```

2. **Database Connection Issues**:
   ```bash
   # Check MySQL container logs
   docker logs dezhou-mysql
   
   # Fix: Ensure MySQL is healthy before starting backend
   ```

3. **Build Failures**:
   ```bash
   # Clean build
   docker-compose down --rmi all -v
   docker-compose up --build
   ```

### Debugging

```bash
# View logs for a specific service
docker logs dezhou-backend

# View logs with follow
docker logs -f dezhou-frontend

# Enter a running container
docker exec -it dezhou-backend sh
```

## 📚 Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [Vue.js Docker Guide](https://vuejs.org/guide/scaling-up/tooling.html#docker)

## 🤝 Contributing

If you make improvements to the Docker setup:

1. Update this README
2. Test your changes thoroughly
3. Ensure all health checks pass
4. Document any new configuration options

---

**Happy Dockerizing!** 🚀🐳