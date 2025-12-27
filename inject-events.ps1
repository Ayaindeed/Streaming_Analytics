$API_BASE_URL = "http://localhost:8080/analytics-api"
$EVENTS_FILE = "C:\Users\hp\IdeaProjects\pj\events_100k.json"

Write-Host "Loading events from JSON file..."
$jsonContent = Get-Content $EVENTS_FILE -Raw
$events = $jsonContent | ConvertFrom-Json

$totalEvents = $events.Count
Write-Host "Total events: $totalEvents"
Write-Host ""

Write-Host "Waiting for API to be ready..."
Start-Sleep -Seconds 5

Write-Host "Checking API health..."
try {
    $health = Invoke-RestMethod -Uri "$API_BASE_URL/api/v1/analytics/health" -Method GET -ErrorAction Stop
    Write-Host "API is healthy"
} catch {
    Write-Host "API not ready yet, trying again..."
    Start-Sleep -Seconds 3
}
Write-Host ""

Write-Host "Sending batch request to API..."
Write-Host "Processing $totalEvents events..."
$json = $events | ConvertTo-Json -Depth 10

$response = Invoke-RestMethod -Uri "$API_BASE_URL/api/v1/analytics/events/batch" `
    -Method POST `
    -ContentType "application/json" `
    -Body $json

Write-Host "Response: $($response | ConvertTo-Json)"
Write-Host ""
Write-Host "Events successfully injected into MongoDB!"
Write-Host ""
Write-Host "Dashboard: http://localhost:8080/analytics-dashboard/"
Write-Host "Mongo Express: http://localhost:8081/"
