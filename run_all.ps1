# run_all.ps1 - Launch all microservices natively on Windows

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$projectRoot = if (Test-Path "$scriptDir\payment-gateway\pom.xml") { "$scriptDir\payment-gateway" } elseif (Test-Path "$scriptDir\pom.xml") { $scriptDir } else { "$scriptDir\.." }
$envPath = if (Test-Path "$projectRoot\.env") { "$projectRoot\.env" } elseif (Test-Path "$scriptDir\.env") { "$scriptDir\.env" } else { "$scriptDir\payment-gateway\.env" }

# Parse .env file and build environment setup script
$envAssignments = ""
if (Test-Path $envPath) {
    Get-Content $envPath | ForEach-Object {
        $line = $_.Trim()
        if ($line -and -not $line.StartsWith("#")) {
            $key, $val = $line -split '=', 2
            if ($key -and $val) {
                $valClean = $val.Trim().Replace("'", "''")
                $envAssignments += "`$env:$($key.Trim())='$valClean'; "
            }
        }
    }
    Write-Host "Environment variables loaded from $envPath" -ForegroundColor Green
} else {
    Write-Warning ".env file not found at $envPath"
}

$services = @("user-service", "payment-service", "transaction-service", "notification-service", "worker-service", "api-gateway")

Write-Host "Launching all microservices from $projectRoot..." -ForegroundColor Cyan

foreach ($service in $services) {
    Write-Host "Starting $service in a new window..." -ForegroundColor Yellow
    $command = "Set-Location '$projectRoot'; `$Host.UI.RawUI.WindowTitle = '$service'; $envAssignments .\mvnw.cmd spring-boot:run -pl $service"
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $command
}

Write-Host "All services started! You can monitor them in their respective windows." -ForegroundColor Green
