$content = Get-Content -Path "app/google-services.json" -Raw
$bytes = [System.Text.Encoding]::UTF8.GetBytes($content)
$base64 = [Convert]::ToBase64String($bytes)
$base64 | Out-File -FilePath "google-services-base64.txt"
Write-Host "Base64 encoded content has been saved to google-services-base64.txt" 