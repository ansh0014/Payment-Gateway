# run_all.ps1 - Launch all microservices natively on Windows

# Parse .env file and build environment setup script
$envAssignments = ""
if (Test-Path .env) {
    Get-Content .env | ForEach-Object {
        $line = $_.Trim()
        if ($line -and -not $line.StartsWith("#")) {
            $key, $val = $line -split '=', 2
            if ($key -and $val) {
                $valClean = $val.Trim().Replace("'", "''")
                $envAssignments += "`$env:$($key.Trim())='$valClean'; "
            }
        }
    }
    Write-Host "Environment variables parsed successfully!" -ForegroundColor Green
} else {
    Write-Error ".env file not found!"
    exit 1
}

$services = @("user-service", "payment-service", "transaction-service", "notification-service", "worker-service", "api-gateway")

Write-Host "Launching all microservices..." -ForegroundColor Cyan

foreach ($service in $services) {
    Write-Host "Starting $service in a new window..." -ForegroundColor Yellow
    # Prepend environment assignments to the command so they are set in the child process
    $command = "`$Host.UI.RawUI.WindowTitle = '$service'; $envAssignments .\mvnw.cmd spring-boot:run -pl $service"
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $command
}

Write-Host "All services started! You can monitor them in their respective windows." -ForegroundColor Green
