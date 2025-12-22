# Streaming Analytics Platform

A Jakarta EE-based streaming video analytics platform with real-time data processing and visualization.

## Architecture

- **Backend**: Jakarta EE 10 (JAX-RS, CDI) with Jersey implementation
- **Database**: MongoDB for event storage and analytics
- **Frontend**: JSP-based dashboard with retro styling
- **Build**: Maven multi-module project
- **Deployment**: Docker Compose (Tomcat, MongoDB, Mongo Express)

## Modules

- `analytics-api`: REST API for event ingestion and analytics queries
- `analytics-dashboard`: Web interface for data visualization
- `data-generator`: Utility for generating test data

## Prerequisites

- Java 11+
- Maven 3.6+
- Docker and Docker Compose

## Quick Start

1. Clone the repository
2. Start services: `docker-compose up -d`
3. Generate data: `mvn clean package -pl data-generator && java -jar data-generator/target/data-generator-1.0-SNAPSHOT.jar`
4. Import data: `docker exec streaming-mongodb mongoimport --db streaming --collection events --file /data/events_100k.json --jsonArray`
5. Build and deploy: `mvn clean package && docker-compose restart streaming-tomcat`
6. Access dashboard: http://localhost:8080/analytics-dashboard/
7. API health check: http://localhost:8080/analytics-api/api/v1/analytics/health

## API Endpoints

- `GET /api/v1/analytics/health` - Health check
- `POST /api/v1/analytics/events` - Ingest single event
- `POST /api/v1/analytics/events/batch` - Ingest batch events
- `GET /api/v1/analytics/videos/top?limit=N` - Top viewed videos
- `GET /api/v1/analytics/videos/{videoId}/stats` - Video statistics
- `GET /api/v1/analytics/users/{userId}/recommendations?limit=N` - User recommendations

## Development

- Build all modules: `mvn clean package`
- Run tests: `mvn test`
- Start development server: `docker-compose up`

## Configuration

- Database connection: MongoDB at localhost:27017/streaming
- Web server: Tomcat at localhost:8080
- Admin interface: Mongo Express at localhost:8081

## Data Model

- Events: User video interactions (views, likes, shares)
- Videos: Content metadata
- Analytics: Aggregated statistics and recommendations