# Streaming Analytics Platform - Big Data Project

Status: Production Ready | Jakarta EE 10 | MongoDB 7.0 | Tomcat 10.1

## Overview

Big Data analytics platform for video streaming services. Processes and analyzes viewing events using Jakarta EE 10, MongoDB 7.0, and Docker.

### Core Features

- Event ingestion (single/batch) with 1000+ events/second throughput
- Real-time analytics with aggregated statistics
- Personalized video recommendations based on viewing history
- RESTful API with 7 endpoints
- Web dashboard with live data visualization
- MongoDB aggregation pipelines for performance  

## Quick Start

```powershell
# 1. Start infrastructure
docker-compose up -d

# 2. Build and deploy
mvn clean package

# 3. Generate test data
java -jar data-generator/target/data-generator-1.0-SNAPSHOT.jar

# 4. Import data into MongoDB
docker exec streaming-mongodb mongoimport --username admin --password admin123 --authenticationDatabase admin --db streaming_analytics --collection events --file /data-generator/events_100k.json --jsonArray --drop

docker exec streaming-mongodb mongoimport --username admin --password admin123 --authenticationDatabase admin --db streaming_analytics --collection videos --file /data-generator/videos_catalog.json --jsonArray --drop

# 5. Access services
# Dashboard: http://localhost:8080/analytics-dashboard/dashboard/view
# API: http://localhost:8080/analytics-api/api/v1/analytics/health
```

## API Endpoints

**Base URL**: `http://localhost:8080/analytics-api/api/v1/analytics`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/health` | GET | Health check |
| `/events` | POST | Ingest single event |
| `/events/batch` | POST | Ingest batch events |
| `/videos/top` | GET | Get top videos |
| `/videos/{id}/stats` | GET | Video statistics |
| `/users/{id}/recommendations` | GET | User recommendations |
| `/realtime/stream` | GET | Real-time SSE stream |

## Architecture

### Backend (analytics-api)
- JAX-RS REST API with Jersey 3.1
- CDI dependency injection (Weld 5.1.2)
- MongoDB Java Driver 4.11.1
- Service layer: EventProcessorService, AnalyticsService
- Repository pattern: EventRepository with direct MongoDB access

### Frontend (analytics-dashboard)
- Jakarta Servlets for server-side rendering
- JSP views with JSTL
- Direct MongoDB queries via Java Driver
- JavaScript for API integration

### Data Layer
- MongoDB 7.0 with aggregation pipelines
- Collections: events, videos, video_stats, recommendations
- Optimized indexes on userId, videoId, timestamp

## Technology Stack

- **Jakarta EE 10** - Enterprise framework
- **MongoDB 7.0** - NoSQL database
- **JAX-RS** - REST API
- **Docker Compose** - Container orchestration
- **Maven** - Build automation
- Jakarta EE 10 (JAX-RS 3.1, CDI 4.0, Servlets 6.0)
- MongoDB 7.0 with Java Sync Driver 4.11.1
- Jersey 3.1 (JAX-RS implementation)
- Weld 5.1.2 (CDI implementation)
- Jackson 2.16.1 (JSON processing)
- Docker Compose (MongoDB, Tomcat, Mongo Express)
- Maven 3.8+ with wrapper
- Tomcat 10.1.50 on JDK 17
```
├── analytics-api/          # REST API backend
├── analytics-dashboard/    # Web dashboard
├── data-generator/         # Test data generator
├── mongo-init/             # DB initialization scripts
├── docker-compose.yml      # Infrastructure
└── inject-events-improved.ps1  # Data injection script
```


- Dashboard: http://localhost:8080/analytics-dashboard/dashboard/view
- Stats Page: http://localhost:8080/analytics-dashboard/stats
- API Health: http://localhost:8080/analytics-api/api/v1/analytics/health
- Mongo Express
```bash
# View logs
docker-compose logs -f

# Check health
curl http://localhost:8080/analytics-api/api/v1/analytics/health

# MongoDB admin
http://localhost:8081/
```

## Performance

- **Throughput**: 1000+ events/second
- **Latency**: Metrics

- Throughput: 1000+ events/second via batch endpoint
- API latency: <50ms for health check, <200ms for recommendations
- Dashboard load time: ~1s for 100k events (optimized aggregations)
- Data volume: Tested with 100k events + 10k videos

## Troubleshooting

- If dashboard shows 0 values: Check MongoDB data import completed
- If API returns 500: Check JAXB dependencies in pom.xml
- If dashboard timeout: Check servlet uses aggregation pipelines, not find().into()
- If JSP errors: Escape JavaScript template literals with backslash: \${variable}

---