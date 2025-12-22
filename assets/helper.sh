#!/bin/bash

# Script helper pour le TP Streaming Analytics
# Facilite les opérations courantes

set -e

# Couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Fonction d'affichage
print_step() {
    echo -e "${BLUE}==>${NC} $1"
}

print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

# Fonction d'aide
show_help() {
    cat << EOF
🎬 Streaming Analytics - Script Helper

Usage: ./helper.sh [COMMAND]

Commandes disponibles:

  setup         Configuration initiale complète
  start         Démarrer l'infrastructure (Docker)
  stop          Arrêter l'infrastructure
  restart       Redémarrer l'infrastructure
  
  build         Compiler le projet Maven
  deploy        Compiler et déployer l'application
  
  generate      Générer les données de test
  import        Importer les données dans MongoDB
  
  logs          Afficher les logs (Ctrl+C pour quitter)
  status        Vérifier le statut des services
  
  test-api      Tester l'API REST
  clean         Nettoyer les builds et données
  
  help          Afficher cette aide

Exemples:
  ./helper.sh setup       # Première utilisation
  ./helper.sh deploy      # Recompiler et déployer
  ./helper.sh logs        # Voir les logs en temps réel

EOF
}

# Setup initial
setup() {
    print_step "Configuration initiale du projet..."
    
    # Vérifier les prérequis
    print_step "Vérification des prérequis..."
    
    if ! command -v java &> /dev/null; then
        print_error "Java n'est pas installé"
        exit 1
    fi
    print_success "Java $(java -version 2>&1 | head -n 1)"
    
    if ! command -v mvn &> /dev/null; then
        print_error "Maven n'est pas installé"
        exit 1
    fi
    print_success "Maven $(mvn -version | head -n 1)"
    
    if ! command -v docker &> /dev/null; then
        print_error "Docker n'est pas installé"
        exit 1
    fi
    print_success "Docker $(docker --version)"
    
    # Créer les répertoires nécessaires
    print_step "Création de la structure de répertoires..."
    mkdir -p data-generator/src/main/java/com/streaming/datagenerator
    mkdir -p analytics-api/src/main/{java,resources}/com/streaming/analytics/{model,repository,service,api}
    mkdir -p analytics-api/src/main/resources/META-INF
    mkdir -p analytics-dashboard/src/main/java/com/streaming/dashboard/servlet
    mkdir -p analytics-dashboard/src/main/webapp/{WEB-INF/views,static}
    mkdir -p mongo-init
    print_success "Structure créée"
    
    # Démarrer l'infrastructure
    start
    
    print_success "Setup terminé !"
    echo ""
    print_warning "Prochaines étapes :"
    echo "  1. Générer les données : ./helper.sh generate"
    echo "  2. Compiler le projet : ./helper.sh build"
    echo "  3. Tester l'API : ./helper.sh test-api"
}

# Démarrer l'infrastructure
start() {
    print_step "Démarrage de l'infrastructure Docker..."
    docker-compose up -d
    
    print_step "Attente du démarrage de MongoDB..."
    sleep 5
    
    print_success "Infrastructure démarrée"
    status
}

# Arrêter l'infrastructure
stop() {
    print_step "Arrêt de l'infrastructure..."
    docker-compose down
    print_success "Infrastructure arrêtée"
}

# Redémarrer
restart() {
    stop
    start
}

# Compiler le projet
build() {
    print_step "Compilation du projet Maven..."
    mvn clean package
    print_success "Compilation terminée"
}

# Déployer
deploy() {
    build
    print_step "Redémarrage de Tomcat pour déployer..."
    docker-compose restart tomcat
    sleep 3
    print_success "Application déployée"
    
    print_step "Test de l'API..."
    sleep 2
    curl -s http://localhost:8080/analytics-api/api/v1/analytics/health || print_warning "API non accessible"
}

# Générer les données
generate() {
    print_step "Génération des données de test..."
    
    cd data-generator
    if [ ! -f "target/data-generator-1.0-SNAPSHOT.jar" ]; then
        print_step "Compilation du générateur..."
        mvn clean package
    fi
    
    java -jar target/data-generator-1.0-SNAPSHOT.jar
    cd ..
    
    print_success "Données générées : events_100k.json et videos_catalog.json"
}

# Importer les données dans MongoDB
import_data() {
    print_step "Import des données dans MongoDB..."
    
    if [ ! -f "data-generator/events_100k.json" ]; then
        print_error "Fichier events_100k.json non trouvé. Exécutez d'abord: ./helper.sh generate"
        exit 1
    fi
    
    docker-compose exec -T mongodb mongoimport \
        --username admin \
        --password admin123 \
        --authenticationDatabase admin \
        --db streaming_analytics \
        --collection events \
        --file /tmp/events_100k.json \
        --jsonArray
    
    print_success "Import terminé"
}

# Afficher les logs
logs() {
    print_step "Affichage des logs (Ctrl+C pour quitter)..."
    docker-compose logs -f
}

# Vérifier le statut
status() {
    print_step "Statut des services..."
    echo ""
    docker-compose ps
    echo ""
    
    print_step "Vérification de MongoDB..."
    if docker-compose exec mongodb mongosh --quiet --eval "db.adminCommand('ping')" &> /dev/null; then
        print_success "MongoDB : UP"
    else
        print_error "MongoDB : DOWN"
    fi
    
    print_step "Vérification de l'API..."
    if curl -s http://localhost:8080/analytics-api/api/v1/analytics/health &> /dev/null; then
        print_success "API : UP"
    else
        print_warning "API : DOWN ou non déployée"
    fi
    
    echo ""
    print_step "URLs utiles :"
    echo "  MongoDB Express : http://localhost:8081"
    echo "  API Health Check : http://localhost:8080/analytics-api/api/v1/analytics/health"
    echo "  Dashboard : http://localhost:8080/analytics-dashboard/dashboard"
}

# Tester l'API
test_api() {
    print_step "Tests de l'API REST..."
    
    API_BASE="http://localhost:8080/analytics-api/api/v1/analytics"
    
    # Health check
    print_step "Test 1 : Health Check"
    curl -s "${API_BASE}/health" | jq . || print_error "Health check échoué"
    echo ""
    
    # Insérer un événement
    print_step "Test 2 : Insertion d'un événement"
    curl -s -X POST "${API_BASE}/events" \
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
        }' | jq . || print_warning "Endpoint non implémenté"
    echo ""
    
    # Top vidéos
    print_step "Test 3 : Top vidéos"
    curl -s "${API_BASE}/videos/top?limit=5" | jq . || print_warning "Endpoint non implémenté"
    echo ""
    
    print_success "Tests terminés"
}

# Nettoyer
clean() {
    print_step "Nettoyage..."
    
    print_step "Arrêt de l'infrastructure..."
    docker-compose down -v
    
    print_step "Nettoyage Maven..."
    mvn clean
    
    print_step "Suppression des fichiers générés..."
    rm -f data-generator/*.json
    
    print_success "Nettoyage terminé"
}

# Main
case "$1" in
    setup)
        setup
        ;;
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        restart
        ;;
    build)
        build
        ;;
    deploy)
        deploy
        ;;
    generate)
        generate
        ;;
    import)
        import_data
        ;;
    logs)
        logs
        ;;
    status)
        status
        ;;
    test-api)
        test_api
        ;;
    clean)
        clean
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        print_error "Commande inconnue: $1"
        echo ""
        show_help
        exit 1
        ;;
esac
