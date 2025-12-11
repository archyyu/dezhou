# Docker Setup for Dezhou Poker Server

This guide explains how to run the Dezhou Poker Server using Docker Compose with MySQL database.

## 🚀 Quick Start

```bash
# Build and start the containers
docker-compose up -d

# Check running containers
docker-compose ps

# View logs
docker-compose logs -f

# Stop the containers
docker-compose down
```

## 📋 Services

### 1. MySQL Database (`dezhou-mysql`)
- **Image**: `mysql:8.0`
- **Port**: `33306:3306`
- **Root Password**: `aida87014999`
- **Database**: `dezhou`
- **User**: `dezhou_user` / `dezhou_pass`
- **Initialization**: Automatically loads `database.sql` on first run
- **Volume**: Persistent storage at `mysql_data`

### 2. Dezhou Application (`dezhou-app`)
- **Image**: Built from `Dockerfile`
- **Port**: `8080:8080`
- **Java**: OpenJDK 17 (Eclipse Temurin)
- **Depends on**: MySQL service (waits for healthy database)
- **Environment**: Configured to connect to MySQL container

## 🔧 Configuration

### Database Configuration
The MySQL container is configured to:
1. Create a `dezhou` database
2. Set root password to `aida87014999`
3. Create a user `dezhou_user` with password `dezhou_pass`
4. Automatically import `database.sql` on first startup
5. Persist data in a Docker volume

### Application Configuration
The application connects to MySQL using:
- **URL**: `jdbc:mysql://mysql:3306/dezhou`
- **Username**: `root`
- **Password**: `aida87014999`
- **Parameters**: Unicode support, multi-queries, UTC timezone

## 📦 Database Schema

The `database.sql` file contains:

```sql
-- Users table
CREATE TABLE dezhou_user (
    uid INT AUTO_INCREMENT PRIMARY KEY,
    account VARCHAR(50) DEFAULT '',
    password VARCHAR(20) DEFAULT '',
    roommoney INT DEFAULT 0,
    allmoney INT DEFAULT 100000,
    -- ... other user fields
    UNIQUE KEY uidx_account (account)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Rooms table
CREATE TABLE dezhou_room (
    id INT AUTO_INCREMENT PRIMARY KEY,
    showname VARCHAR(50) DEFAULT '',
    name VARCHAR(50) DEFAULT '',
    bbet INT DEFAULT 0,
    sbet INT DEFAULT 0,
    maxbuy INT DEFAULT 0,
    minbuy INT DEFAULT 0,
    roomtype VARCHAR(10) DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

## 🛠️ Build Process

### 1. Build the Application
```bash
mvn clean package
```

### 2. Build Docker Images
```bash
docker-compose build
```

### 3. Start Services
```bash
docker-compose up -d
```

## 🧪 Testing

### Check Database Connection
```bash
# Connect to MySQL container
docker exec -it dezhou-mysql mysql -uroot -paida87014999 dezhou

# Show tables
SHOW TABLES;

# Check users
SELECT * FROM dezhou_user;

# Check rooms
SELECT * FROM dezhou_room;
```

### Test Application
```bash
# Check application logs
docker-compose logs app

# Test health endpoint
curl http://localhost:8080/health

# Test API
curl -X POST http://localhost:8080/api/game/process \
  -H "Content-Type: application/json" \
  -d '{"fn":"login","data":{}}'
```

## 🔄 Common Commands

```bash
# Rebuild and restart
docker-compose down && docker-compose up -d --build

# View specific service logs
docker-compose logs app

# Connect to MySQL container
docker exec -it dezhou-mysql bash

# Connect to app container
docker exec -it dezhou-app bash

# Remove volumes (careful - this deletes data!)
docker-compose down -v
```

## 🐳 Docker Network

Both services are connected via a bridge network `dezhou-network`:
- `mysql` container is accessible as `mysql` hostname from app container
- `app` container can access MySQL on port 3306
- Host machine accesses MySQL on port 33306

## 📈 Scaling (Future)

For production, consider:
```yaml
# Example for scaling (not in current compose file)
services:
  app:
    deploy:
      replicas: 2
      restart_policy:
        condition: on-failure
```

## 🔒 Security Notes

1. **Database Credentials**: Change passwords in production
2. **Root Access**: Consider using non-root user for application
3. **Network**: Use internal networks, don't expose MySQL to host in production
4. **Volumes**: Backup `mysql_data` volume regularly

## 🚫 Troubleshooting

### MySQL Connection Issues
- Check if MySQL container is healthy: `docker-compose ps`
- Verify MySQL logs: `docker-compose logs mysql`
- Test connection from app container: `docker exec -it dezhou-app bash -c "apt update && apt install -y mysql-client && mysql -h mysql -uroot -paida87014999"`

### Application Startup Issues
- Check application logs: `docker-compose logs app`
- Verify database is initialized: `docker exec -it dezhou-mysql mysql -uroot -paida87014999 dezhou -e "SHOW TABLES;"`
- Check Spring Boot configuration matches Docker environment variables

### Port Conflicts
- MySQL: 33306 (host) → 3306 (container)
- App: 8080 (host) → 8080 (container)
- Change host ports if conflicts occur

## 🎯 Next Steps

1. **Complete Application Migration**: Fix remaining compilation issues
2. **Add Health Checks**: Enhance application health monitoring
3. **Configure Logging**: Set up proper logging in containers
4. **Add Monitoring**: Prometheus/Grafana for metrics
5. **Implement CI/CD**: Automated testing and deployment

The Docker setup is now ready to run the Dezhou Poker Server with MySQL database! 🎉