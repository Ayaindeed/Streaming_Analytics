# Architecture Technique - Plateforme Analytics Streaming

## Vue d'ensemble

Plateforme Jakarta EE 10 pour l'analyse Big Data d'événements de streaming vidéo. Architecture 3-tiers avec API REST, couche service et MongoDB.

## Stack Technique

- **Backend**: Jakarta EE 10, JAX-RS 3.1, CDI 4.0
- **Serveur**: Tomcat 10.1.50, JDK 17
- **Base de données**: MongoDB 7.0 (Java Sync Driver 4.11.1)
- **Build**: Maven 3.8+ avec wrapper
- **Conteneurisation**: Docker Compose

## Modules Maven

### 1. analytics-api (Backend REST)

**Localisation**: `analytics-api/`

**Technologies**:
- Jersey 3.1 (implémentation JAX-RS)
- Weld 5.1.2 (implémentation CDI)
- Jackson 2.16.1 (JSON)
- JAXB 4.0 (marshalling XML/JSON, requis Java 17+)

**Structure**:

```
src/main/java/com/streaming/
├── api/
│   └── AnalyticsResource.java       # Contrôleur REST, 7 endpoints
├── service/
│   ├── EventProcessorService.java   # Ingestion, validation, batch (1000/batch)
│   └── AnalyticsService.java        # Agrégations, recommandations, KPI
├── repository/
│   └── EventRepository.java         # DAO MongoDB, CRUD + requêtes custom
├── model/
│   ├── ViewEvent.java               # Entité événement
│   ├── VideoStats.java              # Statistiques vidéo
│   └── VideoRecommendation.java     # Recommandation personnalisée
└── config/
    └── MongoDBConfig.java           # Configuration MongoDB, connexion singleton
```

**Fichiers clés**:

- **AnalyticsResource.java**: Point d'entrée REST
  - `@Path("/api/v1/analytics")`
  - Injection: `@Inject EventProcessorService, AnalyticsService`
  - Endpoints: health, events, events/batch, videos/top, videos/{id}/stats, users/{id}/recommendations, realtime/stream

- **EventProcessorService.java**: Logique métier ingestion
  - `@ApplicationScoped` (CDI singleton)
  - Méthodes: `ingestEvent()`, `ingestBatch()`, `validateEvent()`, `enrichEvent()`
  - Traitement asynchrone avec `CompletableFuture`

- **AnalyticsService.java**: Logique métier analytics
  - Agrégations MongoDB: `$group`, `$sort`, `$limit`, `$match`
  - `getTopVideos()`: Pipeline avec tri par vues
  - `getPersonalizedRecommendations()`: Analyse historique utilisateur
  - `calculateKPIs()`: Métriques business

- **EventRepository.java**: Accès données
  - `MongoCollection<Document>` direct (pas JPA)
  - Méthodes: `save()`, `findByUserId()`, `findByVideoId()`, `countByAction()`
  - Index: userId, videoId, timestamp

- **webapp/WEB-INF/beans.xml**: Active CDI
- **webapp/WEB-INF/web.xml**: Configure servlet Jersey

### 2. analytics-dashboard (Frontend Web)

**Localisation**: `analytics-dashboard/`

**Technologies**:
- Jakarta Servlets 6.0
- JSP avec JSTL
- MongoDB Java Driver (direct)
- JavaScript vanilla (fetch API)

**Structure**:

```
src/main/java/com/streaming/analytics/servlet/
├── DashboardViewController.java     # Servlet principal, agrégations MongoDB
├── DashboardServlet.java            # Page d'accueil simple
└── StatsServlet.java                # Page statistiques

src/main/webapp/
├── index.jsp                        # Landing page
└── WEB-INF/
    ├── views/
    │   └── dashboard.jsp            # Vue dashboard avec JavaScript
    ├── beans.xml                    # Active CDI
    └── web.xml                      # Configuration servlets
```

**Fichiers clés**:

- **DashboardViewController.java**: 
  - `@WebServlet("/dashboard/view")`
  - Connexion MongoDB directe dans `init()`
  - Agrégations optimisées (évite chargement 100k docs en mémoire)
  - Pipeline `$group` pour comptage utilisateurs
  - Pipeline complexe pour top 10 vidéos avec statistiques
  - Classe interne `VideoStatsData` avec getters JSP (title, totalViews, totalLikes, avgDuration, engagementRate)

- **dashboard.jsp**:
  - JSTL: `<c:forEach>`, `${variable}`
  - JavaScript: `fetch()` pour appels API
  - Template literals échappés: `\${...}` (évite conflit JSP EL)
  - API_BASE_URL: `/analytics-api/api/v1/analytics`

### 3. data-generator (Générateur données test)

**Localisation**: `data-generator/`

**Technologies**:
- Java 17, Jackson 2.16.1
- Maven Shade Plugin (JAR exécutable)

**Fichier principal**:
- `DataGenerator.java`: Génère events_100k.json (100k événements) + videos_catalog.json (10k vidéos)

### 4. mongo-init (Initialisation BD)

**Localisation**: `mongo-init/`

**Fichier**:
- `mongo-init.js`: Exécuté au démarrage MongoDB container
  - Crée collections: events, videos, video_stats, recommendations
  - Crée index: userId, videoId, timestamp
  - Validateurs JSON Schema pour events

## Architecture de données

### Collections MongoDB

**events**:
```json
{
  "eventId": "evt_xxx",
  "userId": "user_xxx",
  "videoId": "video_xxx", 
  "timestamp": "2025-12-27T...",
  "action": "WATCH|PAUSE|STOP|SEEK",
  "duration": 123,
  "quality": "360p|720p|1080p|4K",
  "deviceType": "mobile|desktop|tablet"
}
```

**videos**:
```json
{
  "videoId": "video_xxx",
  "title": "...",
  "category": "...",
  "duration": 1234,
  "views": 0
}
```

### Index MongoDB
- `events.userId` (1)
- `events.videoId` (1)
- `events.timestamp` (-1)
- `videos.videoId` (1, unique)

## Flux de données

### 1. Ingestion événement unique
```
Client → POST /events → AnalyticsResource 
  → EventProcessorService.ingestEvent()
  → EventRepository.save()
  → MongoDB.events.insertOne()
```

### 2. Ingestion batch
```
Client → POST /events/batch → AnalyticsResource
  → EventProcessorService.ingestBatch(List<ViewEvent>)
  → EventRepository.saveAll()
  → MongoDB.events.insertMany() [1000/batch]
```

### 3. Top vidéos
```
Client → GET /videos/top?limit=10 → AnalyticsResource
  → AnalyticsService.getTopVideos(10)
  → MongoDB aggregation pipeline:
    $group(videoId) → $sum(views) → $sort(-views) → $limit(10)
```

### 4. Dashboard
```
Browser → GET /dashboard/view → DashboardViewController.doGet()
  → MongoDB.events.aggregate([
      {$group: {_id: "$videoId", totalViews: {$sum: 1}}},
      {$sort: {totalViews: -1}},
      {$limit: 10}
    ])
  → Forward to dashboard.jsp
  → Render HTML avec JSTL
```

### 5. Recommandations
```
Browser → JS fetch(/users/{id}/recommendations) → AnalyticsResource
  → AnalyticsService.getPersonalizedRecommendations()
  → MongoDB.events.find({userId: xxx})
  → Analyse historique + filtrage
  → Return JSON → Display dans dashboard.jsp
```

## Optimisations critiques

### Problèmes résolus

1. **distinct() charge 40k+ IDs en mémoire**:
   - Solution: `$group` + `$count` pipeline
   ```java
   db.events.aggregate([
     {$group: {_id: "$userId"}},
     {$count: "totalUsers"}
   ])
   ```

2. **find().into(List) charge 100k docs**:
   - Solution: Agrégations MongoDB côté serveur
   - DashboardViewController utilise uniquement pipelines

3. **Template literals ${} en JSP**:
   - Problème: JSP interprète comme EL
   - Solution: Échapper avec `\${variable}`

4. **JAXB manquant Java 17+**:
   - Ajout dépendances: `jakarta.xml.bind-api` + `jaxb-runtime`

5. **VideoStatsData propriétés manquantes**:
   - JSP demande: title, totalLikes, avgDuration, engagementRate
   - Solution: Ajouter getters + alias

## Déploiement Docker

**docker-compose.yml**:
```yaml
services:
  mongodb:         # Port 27017, volume persistant
  tomcat:          # Port 8080, monte WARs depuis target/
  mongo-express:   # Port 8081, UI MongoDB
```

**Volumes**:
- `mongodb_data`: Données persistantes MongoDB
- `./analytics-api/target/analytics-api.war → /usr/local/tomcat/webapps/`
- `./analytics-dashboard/target/analytics-dashboard.war → /usr/local/tomcat/webapps/`

## Performance

- **Throughput**: 1000+ événements/seconde (batch endpoint)
- **Latency API**: <50ms (health), <200ms (recommendations)
- **Dashboard**: ~1s pour 100k événements (agrégations optimisées)
- **Scalabilité**: Horizontale via sharding MongoDB

## Commandes essentielles

```powershell
# Build complet
mvn clean package

# Build module spécifique
mvn clean package -pl analytics-api

# Démarrer infra
docker-compose up -d

# Arrêter tout
docker-compose down

# Import données
docker exec streaming-mongodb mongoimport --username admin --password admin123 --authenticationDatabase admin --db streaming_analytics --collection events --file /data-generator/events_100k.json --jsonArray --drop

# Logs Tomcat
docker logs streaming-tomcat -f

# Shell MongoDB
docker exec -it streaming-mongodb mongosh --username admin --password admin123 --authenticationDatabase admin streaming_analytics
```

## Points d'attention

- CDI nécessite beans.xml dans WEB-INF/
- Jersey nécessite jersey-cdi1x pour injection @Inject
- MongoDB connection string: `mongodb://admin:admin123@mongodb:27017`
- Database name: `streaming_analytics`
- Template literals JSP: toujours échapper `\${}`
- Java 17 nécessite JAXB explicitement dans pom.xml
