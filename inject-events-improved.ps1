# PowerShell script to inject events into the analytics API
# This script generates and sends events to the streaming analytics platform

param(
    [int]$NumEvents = 1000,
    [string]$ApiUrl = "http://localhost:8080/analytics-api/api/v1/analytics"
)

Write-Host "Streaming Analytics - Event Injection Script" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Check if API is available
try {
    $healthCheck = Invoke-RestMethod -Uri "$ApiUrl/health" -Method GET -ErrorAction Stop
    Write-Host "API Status: $($healthCheck.status)" -ForegroundColor Green
} catch {
    Write-Host "ERROR: Cannot connect to API at $ApiUrl" -ForegroundColor Red
    Write-Host "Please ensure the application is running." -ForegroundColor Yellow
    exit 1
}

# Function to generate random event
function Generate-ViewEvent {
    param([int]$index)
    
    $actions = @("WATCH", "PAUSE", "STOP", "RESUME", "SEEK")
    $qualities = @("360p", "480p", "720p", "1080p", "4K")
    $deviceTypes = @("mobile", "desktop", "tablet", "tv", "console")
    
    $event = @{
        eventId = "evt_$(Get-Random -Minimum 100000 -Maximum 999999)"
        userId = "user_$(Get-Random -Minimum 1 -Maximum 50000)"
        videoId = "video_$(Get-Random -Minimum 1 -Maximum 10000)"
        timestamp = (Get-Date).ToUniversalTime().ToString("yyyy-MM-ddTHH:mm:ssZ")
        action = $actions | Get-Random
        duration = Get-Random -Minimum 30 -Maximum 7200
        quality = $qualities | Get-Random
        deviceType = $deviceTypes | Get-Random
    }
    
    return $event
}

Write-Host "Generating and sending $NumEvents events..." -ForegroundColor Yellow
Write-Host ""

$batchSize = 100
$successCount = 0
$errorCount = 0

for ($i = 0; $i -lt $NumEvents; $i += $batchSize) {
    $batch = @()
    $remaining = [Math]::Min($batchSize, $NumEvents - $i)
    
    for ($j = 0; $j -lt $remaining; $j++) {
        $batch += Generate-ViewEvent -index ($i + $j)
    }
    
    try {
        $jsonBody = $batch | ConvertTo-Json -Depth 10
        $response = Invoke-RestMethod -Uri "$ApiUrl/events/batch" -Method POST `
            -Body $jsonBody -ContentType "application/json" -ErrorAction Stop
        
        $successCount += $remaining
        Write-Progress -Activity "Injecting Events" `
            -Status "Sent $successCount/$NumEvents events" `
            -PercentComplete (($successCount / $NumEvents) * 100)
    } catch {
        $errorCount += $remaining
        Write-Host "Error sending batch: $($_.Exception.Message)" -ForegroundColor Red
    }
    
    Start-Sleep -Milliseconds 100
}

Write-Progress -Activity "Injecting Events" -Completed

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "Event Injection Complete!" -ForegroundColor Green
Write-Host "Successful: $successCount" -ForegroundColor Green
Write-Host "Failed: $errorCount" -ForegroundColor $(if($errorCount -gt 0){"Red"}else{"Green"})
Write-Host ""
Write-Host "Access dashboard at: http://localhost:8080/analytics-dashboard/" -ForegroundColor Cyan
