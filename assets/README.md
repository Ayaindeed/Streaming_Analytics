# PLATEFORME D'ANALYSE BIG DATA - STREAMING VIDÉO

##  Vue d'ensemble

Application JEE/Jakarta EE complète pour l'analyse Big Data d'une plateforme de streaming vidéo.
Traite 1000+ événements/seconde et génère des statistiques en temps réel.

##  Architecture

```
streaming-analytics/
├── data-generator/          # Générateur de données simulées
│   ├── src/
│   │   └── main/java/
│   │       └── DataGenerator.java
│   └── pom.xml
│
├── analytics-api/           # API REST backend
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   ├── model/           # Entités JPA/MongoDB
│   │       │   ├── repository/      # DAO/Repositories
│   │       │   ├── service/         # Logique métier
│   │       │   └── api/             # Endpoints REST
│   │       └── resources/
│   │           └── META-INF/
│   │               └── persistence.xml
│   └── pom.xml
│
├── analytics-dashboard/     # Dashboard web (JSP/Servlet)
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── servlet/         # Contrôleurs MVC
│   │       └── webapp/
│   │           ├── WEB-INF/
│   │           │   ├── views/       # JSP
│   │           │   └── web.xml
│   │           └── static/          # CSS, JS
│   └── pom.xml
│
├── docker-compose.yml       # Infrastructure (MongoDB, Tomcat)
└── pom.xml                  # POM parent
```

##  Démarrage rapide

### 1. Prérequis

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- 4 Go RAM minimum

### 2. Lancer l'infrastructure

```bash
# Démarrer MongoDB et Tomcat
docker-compose up -d

# Vérifier que tout est UP
docker-compose ps

# Logs MongoDB
docker-compose logs -f mongodb
```

MongoDB sera accessible sur : `localhost:27017`
Mongo Express (UI) : `http://localhost:8081`

### 3. Générer les données

```bash
# Compiler le générateur
cd data-generator
mvn clean package

# Générer 100 000 événements
java -jar target/data-generator-1.0-SNAPSHOT.jar

# Fichiers générés :
# - events_100k.json (événements)
# - videos_catalog.json (catalogue vidéos)
```

### 4. Compiler et déployer l'API

```bash
# Retour à la racine
cd ..

# Compiler tout le projet
mvn clean package

# Le WAR sera automatiquement déployé via Docker volume
# Vérifier : http://localhost:8080/analytics-api/api/v1/analytics/health
```

### 5. Tester l'API

```bash
# Health check
curl http://localhost:8080/analytics-api/api/v1/analytics/health

# Insérer un événement
curl -X POST http://localhost:8080/analytics-api/api/v1/analytics/events \
  -H "Content-Type: application/json" \
  -d '{
    "eventId": "evt_test_001",
    "userId": "user_123",
    "videoId": "video_456",
    "timestamp": "2024-01-15T10:30:00Z",
    "action": "WATCH",
    "duration": 245,
    "quality": "1080p",
    "deviceType": "mobile"
  }'

# Top 10 vidéos
curl http://localhost:8080/analytics-api/api/v1/analytics/videos/top?limit=10

# Stats d'une vidéo
curl http://localhost:8080/analytics-api/api/v1/analytics/videos/video_456/stats

# Recommandations
curl http://localhost:8080/analytics-api/api/v1/analytics/users/user_123/recommendations
```

### 6. Accéder au Dashboard

```
http://localhost:8080/analytics-dashboard/dashboard
```

##  Données générées

### Événements de visualisation

```json
{
  "eventId": "evt_abc123",
  "userId": "user_456",
  "videoId": "video_789",
  "timestamp": "2024-01-15T14:30:00Z",
  "action": "WATCH",
  "duration": 245,
  "quality": "1080p",
  "deviceType": "mobile"
}
```

### Statistiques calculées

```json
{
  "videoId": "video_789",
  "totalViews": 15234,
  "avgDuration": 342.5,
  "uniqueViewers": 8901,
  "lastUpdated": "2024-01-15T14:35:00Z"
}
```

##  Objectifs du TP

### Partie 1 : Modèle de données (40 min)
- [ ] Créer les entités MongoDB/JPA
- [ ] Implémenter les repositories avec CDI
- [ ] Configurer la connexion MongoDB

### Partie 2 : Traitement Big Data (50 min)
- [ ] Service de traitement des événements
- [ ] Calcul des statistiques agrégées
- [ ] Génération de recommandations
- [ ] Détection des tendances

### Partie 3 : API REST (40 min)
- [ ] Endpoints d'ingestion (single + batch)
- [ ] Endpoints de lecture (stats, top vidéos)
- [ ] Optimisations (pagination, cache)
- [ ] Gestion d'erreurs robuste

### Partie 4 : Dashboard MVC (40 min)
- [ ] Servlet contrôleur
- [ ] Vue JSP avec JSTL
- [ ] CSS/JavaScript
- [ ] Temps réel (SSE - optionnel)

### Partie 5 : Tests (10 min)
- [ ] Tests unitaires
- [ ] Tests d'intégration API
- [ ] Mesures de performance

##  Métriques de performance attendues

| Métrique | Objectif |
|----------|----------|
| Ingestion single event | < 50ms |
| Ingestion batch (1000 events) | < 2s |
| Top 10 vidéos | < 100ms |
| Recommandations | < 200ms |
| Débit ingestion | > 500 events/s |

##  Tests avec Postman

Importez la collection fournie : `postman-collection.json`

Endpoints disponibles :
1. Health Check
2. Ingest Single Event
3. Ingest Batch Events
4. Get Top Videos
5. Get Video Stats
6. Get User Recommendations
7. Real-time Stream (SSE)

##  Dépannage

### Problème : MongoDB connection refused

```bash
# Vérifier que MongoDB est démarré
docker-compose ps

# Relancer si nécessaire
docker-compose restart mongodb

# Voir les logs
docker-compose logs mongodb
```

### Problème : WAR non déployé

```bash
# Vérifier le volume
docker-compose exec tomcat ls /usr/local/tomcat/webapps/

# Recompiler et redéployer
mvn clean package
docker-compose restart tomcat
```

### Problème : OutOfMemoryError

```bash
# Augmenter la mémoire Tomcat dans docker-compose.yml
environment:
  - CATALINA_OPTS=-Xms1024m -Xmx2048m
```

##  Technologies utilisées

- **Backend** : Jakarta EE 10, JAX-RS, CDI, JPA
- **Base de données** : MongoDB 7.0
- **Serveur** : Apache Tomcat 10.1
- **Build** : Maven 3.8+
- **Conteneurisation** : Docker & Docker Compose
- **Frontend** : JSP, JSTL, JavaScript, CSS

## 🎓 Critères d'évaluation

| Critère | Points |
|---------|--------|
| Architecture et patterns | /4 |
| Persistance MongoDB | /4 |
| Traitement Big Data | /5 |
| API REST | /5 |
| Dashboard MVC | /4 |
| Tests et performance | /2 |
| **TOTAL** | **/20** |

##  Conseils

1. **Commencez par le modèle de données** - C'est la fondation
2. **Testez au fur et à mesure** - Ne compilez pas tout d'un coup
3. **Utilisez les logs** - `docker-compose logs -f` est votre ami
4. **Optimisez progressivement** - Faites marcher avant d'optimiser
5. **Documentez vos choix** - Expliquez pourquoi vous avez fait tel choix


