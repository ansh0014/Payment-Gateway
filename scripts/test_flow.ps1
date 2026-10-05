# scripts/test_flow.ps1 - Automated End-to-End Integration Test for Windows

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$envPath = if (Test-Path "$scriptDir\..\payment-gateway\.env") { "$scriptDir\..\payment-gateway\.env" } else { "$scriptDir\.env" }

if (Test-Path $envPath) {
    Get-Content $envPath | ForEach-Object {
        $line = $_.Trim()
        if ($line -and -not $line.StartsWith("#")) {
            $key, $val = $line -split '=', 2
            if ($key -and $val) {
                [System.Environment]::SetEnvironmentVariable($key.Trim(), $val.Trim(), "Process")
            }
        }
    }
}

$GATEWAY_URL = "http://localhost:8080"
$TEST_SESSION = "test-session-123"

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "          PAYMENT GATEWAY E2E INTEGRATION TEST            " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# Wait for Gateway
Write-Host "Waiting for API Gateway to boot up..." -ForegroundColor Yellow
$gatewayReady = $false
while (-not $gatewayReady) {
    try {
        $response = Invoke-WebRequest -Uri "$GATEWAY_URL/api/users/register" -Method Get -TimeoutSec 2 -ErrorAction Stop
        $gatewayReady = $true
    } catch {
        Write-Host -NoNewline "."
        Start-Sleep -Seconds 2
    }
}
Write-Host " Online!" -ForegroundColor Green

# 1. Register User
$randomId = Get-Random -Minimum 1000 -Maximum 9999
$email = "anshul_$randomId@test.com"
Write-Host "1. Registering a new user ($email)..." -ForegroundColor Yellow

$regBody = @{
    email = $email
    password = "securepassword"
    firstName = "Anshul"
    lastName = "Jagota"
    phoneNumber = "9876543210"
} | ConvertTo-Json

try {
    $regResponse = Invoke-RestMethod -Uri "$GATEWAY_URL/api/users/register" -Method Post -ContentType "application/json" -Body $regBody
    Write-Host "   User registered successfully! User ID: $($regResponse.id)" -ForegroundColor Green
    $userId = $regResponse.id
} catch {
    Write-Error "Failed to register user: $_"
    exit 1
}

# 2. Credit Wallet
Write-Host "2. Adding 1000.00 credits to Wallet..." -ForegroundColor Yellow
try {
    $headers = @{ "X-Session-ID" = $TEST_SESSION }
    $creditResponse = Invoke-RestMethod -Uri "$GATEWAY_URL/api/wallets/$userId/credit?amount=1000" -Method Post -Headers $headers
    Write-Host "   Wallet credited successfully. New Balance: $($creditResponse.balance)" -ForegroundColor Green
} catch {
    Write-Error "Failed to credit wallet: $_"
    exit 1
}

# 3. Check Initial Balance
Write-Host "3. Checking initial balance..." -ForegroundColor Yellow
try {
    $balResponse = Invoke-RestMethod -Uri "$GATEWAY_URL/api/wallets/user/$userId" -Method Get -Headers $headers
    Write-Host "   Initial Wallet Details: Balance = $($balResponse.balance)" -ForegroundColor Green
} catch {
    Write-Error "Failed to check balance: $_"
    exit 1
}

# 4. Initiate Payment
Write-Host "4. Initiating a payment of 250.00..." -ForegroundColor Yellow
$payBody = @{
    userId = $userId
    amount = 250.00
    paymentMethod = "WALLET"
    description = "Integration Test Payment"
} | ConvertTo-Json

try {
    $payResponse = Invoke-RestMethod -Uri "$GATEWAY_URL/api/payments/initiate" -Method Post -ContentType "application/json" -Body $payBody -Headers $headers
    Write-Host "   Payment initiated (Status: $($payResponse.status)). Processing in queue..." -ForegroundColor Green
} catch {
    Write-Error "Failed to initiate payment: $_"
    exit 1
}

# 5. Wait for Worker Processing
Write-Host "5. Waiting 6 seconds for background worker to process..." -ForegroundColor Yellow
Start-Sleep -Seconds 6

# 6. Verify Final Balance
Write-Host "6. Verifying final balance (should be debited by 250.00)..." -ForegroundColor Yellow
try {
    $finalBalResponse = Invoke-RestMethod -Uri "$GATEWAY_URL/api/wallets/user/$userId" -Method Get -Headers $headers
    Write-Host "   Final Wallet Details: Balance = $($finalBalResponse.balance)" -ForegroundColor Green
} catch {
    Write-Error "Failed to verify final balance: $_"
    exit 1
}

Write-Host "==========================================================" -ForegroundColor Green
Write-Host "             TEST COMPLETE - SUCCESS!                     " -ForegroundColor Green
Write-Host "==========================================================" -ForegroundColor Green
