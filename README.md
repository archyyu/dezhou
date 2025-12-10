# Dezhou Poker Server - Spring Boot Edition

A poker game server built with Spring Boot, providing a modern architecture for the classic Dezhou poker game.

## Technology Stack

- **Language**: Java 17+
- **Framework**: Spring Boot 3.2.0
- **Database**: MySQL 8+
- **ORM**: MyBatis + Spring Data JPA
- **Protocol**: HTTP/REST + WebSocket (planned)
- **Build Tool**: Maven

## Features

- ✅ Modern Spring Boot architecture
- ✅ RESTful API endpoints
- ✅ MyBatis integration for database access
- ✅ Health checks and monitoring
- ✅ Backward compatibility with legacy code
- 🚀 Ready for cloud deployment

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-repo/dezhou.git
   cd dezhou
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

4. Test the health endpoint:
   ```bash
   curl http://localhost:8080/health
   ```

## API Documentation

### Health Endpoints

- `GET /health` - Basic health check
- `GET /health/ready` - Readiness check

### Game Endpoints

- `POST /api/game/process` - Process game requests
- `POST /api/game/{cmd}` - Legacy command processing

## Configuration

The application uses `application.yml` for configuration. Key properties:

- `server.port` - HTTP server port (default: 8080)
- `spring.datasource.*` - Database configuration
- `app.servers` - Legacy server configurations

## Database Setup

1. Create the database:
   ```sql
   CREATE DATABASE dezhou CHARACTER SET utf8 COLLATE utf8_general_ci;
   ```

2. Import the schema:
   ```bash
   mysql -u root -p dezhou < database.sql
   ```

3. Update `application.yml` with your database credentials

## Architecture

```
DezhouApplication (Spring Boot Main)
├── GameRestController (REST API)
├── GameController (Game Logic)
├── AppConfig (Configuration)
├── MyBatis (Database Access)
└── Spring Data JPA (ORM)
```

## Migration Notes

This project was migrated from a legacy Netty-based architecture to Spring Boot. See `MIGRATION_GUIDE.md` for details on:

- Changes made during migration
- Backward compatibility considerations
- Next steps for complete modernization

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to your branch
5. Create a pull request

## License

[MIT License](LICENSE)

## Contact

For questions or support, please open an issue on GitHub.
