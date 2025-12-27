# Script de démarrage rapide Docker uniquement
# Utiliser ce script si le projet est déjà compilé

Write-Host "🚀 Démarrage de l'infrastructure Docker..." -ForegroundColor Cyan
Write-Host ""

# Vérifier que les WAR existent
$apiWar = "analytics-api\target\analytics-api.war"
$dashboardWar = "analytics-dashboard\target\analytics-dashboard-1.0-SNAPSHOT.war"

if (-not (Test-Path $apiWar) -or -not (Test-Path $dashboardWar)) {
    Write-Host "⚠ Fichiers WAR introuvables. Compilation nécessaire..." -ForegroundColor Yellow
    Write-Host "Utilisez: .\deploy.ps1 pour compiler et déployer" -ForegroundColor Yellow
    exit 1
}

# Démarrer Docker
Write-Host "Démarrage des conteneurs..." -ForegroundColor Yellow
docker-compose up -d

Write-Host ""
Write-Host "✓ Services démarrés !" -ForegroundColor Green
Write-Host ""
Write-Host "Services disponibles :" -ForegroundColor White
Write-Host "  📊 API: http://localhost:8080/analytics-api/api/v1/analytics/health" -ForegroundColor Cyan
Write-Host "  🎯 Dashboard: http://localhost:8080/analytics-dashboard/" -ForegroundColor Cyan
Write-Host "  🗄️  Mongo Express: http://localhost:8081/" -ForegroundColor Cyan
Write-Host ""
Write-Host "Logs: docker-compose logs -f" -ForegroundColor Gray
Write-Host ""
