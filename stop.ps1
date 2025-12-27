# Script d'arrêt des services Docker

param(
    [switch]$RemoveData
)

Write-Host "🛑 Arrêt des services..." -ForegroundColor Yellow
Write-Host ""

if ($RemoveData) {
    Write-Host "⚠ Suppression des volumes de données également..." -ForegroundColor Red
    docker-compose down -v
    Write-Host "✓ Services arrêtés et données supprimées" -ForegroundColor Green
} else {
    docker-compose down
    Write-Host "✓ Services arrêtés (données conservées)" -ForegroundColor Green
}

Write-Host ""
Write-Host "Pour redémarrer: .\start.ps1" -ForegroundColor Gray
Write-Host "Pour tout nettoyer: .\stop.ps1 -RemoveData" -ForegroundColor Gray
Write-Host ""
