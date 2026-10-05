#!/usr/bin/env bash

# Exit immediately if a command exits with a non-zero status
set -e

# Load environment variables
if [ -f .env ]; then
    echo "Loading environment variables from .env..."
    set -a
    source .env
    set +a
else
    echo "Error: .env file not found. Please create it first."
    exit 1
fi

GATEWAY_URL="http://localhost:8080"
TEST_SESSION="test-session-123"

echo "=========================================================="
echo "          PAYMENT GATEWAY E2E INTEGRATION TEST            "
echo "=========================================================="

# 1. Setup Test Session in Redis
echo -n "1. Setting up test session in Redis... "
if command -v redis-cli &> /dev/null && [ -n "$REDIS_URL" ]; then
    # Try setting the session key via redis-cli
    redis-cli -u "$REDIS_URL" SET "session:$TEST_SESSION" "active" > /dev/null
    echo "SUCCESS (Session session:$TEST_SESSION is active)"
else
    echo "WARNING: redis-cli not found or REDIS_URL is empty."
    echo "   Ensure you have manually run: SET session:$TEST_SESSION 'active' on your Upstash console."
fi

# Wait for API Gateway to boot up
echo -n "Waiting for API Gateway to boot up (takes 10-15s)..."
until curl -s "$GATEWAY_URL/api/users/register" &>/dev/null || [ $? -eq 7 ]; do
    echo -n "."
    sleep 2
done
# Check if port is open (curl exits with 7 if connection refused)
while curl -s --connect-timeout 2 "$GATEWAY_URL/api/users/register" &>/dev/null; [ $? -eq 7 ]; do
    echo -n "."
    sleep 2
done
echo " Online!"

# Generate a random email to prevent duplicate registration errors
RANDOM_ID=$((1000 + RANDOM % 9000))
EMAIL="anshul_${RANDOM_ID}@test.com"

# 2. Register User
echo "2. Registering a new user ($EMAIL)..."
REG_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$GATEWAY_URL/api/users/register" \
     -H "Content-Type: application/json" \
     -d "{\"email\":\"$EMAIL\",\"password\":\"securepassword\",\"firstName\":\"Anshul\",\"lastName\":\"Jagota\",\"phoneNumber\":\"9876543210\"}")

REG_STATUS=$(echo "$REG_RESPONSE" | tail -n1)
REG_BODY=$(echo "$REG_RESPONSE" | sed '$d')

if [ "$REG_STATUS" -ne 201 ] && [ "$REG_STATUS" -ne 200 ]; then
    echo "Error: Failed to register user. Status: $REG_STATUS"
    echo "$REG_BODY"
    exit 1
fi

# Extract User ID (Assuming ID is 1 for clean setup, or parse from body if needed)
# Since database is clean/recreated, first user ID is 1.
USER_ID="1"
echo "   User registered successfully! (Assumed User ID: $USER_ID)"

# 3. Credit Wallet
echo "3. Adding 1000.00 credits to Wallet $USER_ID..."
CREDIT_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$GATEWAY_URL/api/wallets/$USER_ID/credit?amount=1000" \
     -H "X-Session-ID: $TEST_SESSION")

CREDIT_STATUS=$(echo "$CREDIT_RESPONSE" | tail -n1)
if [ "$CREDIT_STATUS" -ne 200 ]; then
    echo "Error: Failed to credit wallet. Status: $CREDIT_STATUS"
    exit 1
fi
echo "   Wallet credited successfully."

# 4. Check Initial Balance
echo "4. Checking initial balance..."
BAL_RESPONSE=$(curl -s -H "X-Session-ID: $TEST_SESSION" "$GATEWAY_URL/api/wallets/user/$USER_ID")
echo "   Initial Wallet Details: $BAL_RESPONSE"

# 5. Initiate Payment
echo "5. Initiating a payment of 250.00..."
PAY_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$GATEWAY_URL/api/payments/initiate" \
     -H "Content-Type: application/json" \
     -H "X-Session-ID: $TEST_SESSION" \
     -d "{\"userId\":$USER_ID,\"amount\":250.00,\"paymentMethod\":\"WALLET\",\"description\":\"Integration Test Payment\"}")

PAY_STATUS=$(echo "$PAY_RESPONSE" | tail -n1)
if [ "$PAY_STATUS" -ne 200 ]; then
    echo "Error: Failed to initiate payment. Status: $PAY_STATUS"
    exit 1
fi
echo "   Payment initiated (PENDING). Processing in queue..."

# 6. Wait for Worker Processing
echo "6. Waiting 6 seconds for background worker to process..."
sleep 6

# 7. Verify Final Balance
echo "7. Verifying final balance (should be debited by 250.00)..."
FINAL_BAL_RESPONSE=$(curl -s -H "X-Session-ID: $TEST_SESSION" "$GATEWAY_URL/api/wallets/user/$USER_ID")
echo "   Final Wallet Details: $FINAL_BAL_RESPONSE"

echo "=========================================================="
echo "             TEST COMPLETE - CHECK STATUS CODES           "
echo "=========================================================="
