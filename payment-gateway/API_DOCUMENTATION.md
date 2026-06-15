# Payment Gateway - Complete Implementation

## Setup Instructions

### 1. Database Setup (PostgreSQL)
```sql
CREATE DATABASE payment_gateway;
```

### 2. Redis Setup
```
Redis should be running on localhost:6379
```

### 3. Update application.properties
- Change `spring.datasource.password` to your PostgreSQL password
- Ensure Redis is accessible at localhost:6379

### 4. Build Project
```bash
mvn clean install
```

### 5. Run Application
```bash
mvn spring-boot:run
```

Server runs on: `http://localhost:8080/api`

---

## API Endpoints

### USER MANAGEMENT

#### 1. Register User
```http
POST /api/users/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePass123",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "9876543210"
}
```

#### 2. Get User
```http
GET /api/users/{userId}
```

#### 3. Get User by Email
```http
GET /api/users/email/{email}
```

---

### WALLET MANAGEMENT

#### 1. Create Wallet (Auto-created on User Registration)
```http
POST /api/wallets/user/{userId}
```

#### 2. Get Wallet
```http
GET /api/wallets/{walletId}
```

#### 3. Get Wallet by User ID
```http
GET /api/wallets/user/{userId}
```

#### 4. Credit Wallet
```http
POST /api/wallets/{walletId}/credit?amount=1000
```

#### 5. Debit Wallet
```http
POST /api/wallets/{walletId}/debit?amount=500
```

#### 6. Get Available Balance
```http
GET /api/wallets/{walletId}/available-balance
```

---

### PAYMENT PROCESSING

#### 1. Initiate Payment
```http
POST /api/payments/initiate
Content-Type: application/json

{
  "userId": "user-id-here",
  "amount": 500.00,
  "paymentMethod": "WALLET",
  "description": "Payment for order #123"
}
```

Payment Methods: WALLET, CARD, BANK, UPI

#### 2. Process Payment
```http
POST /api/payments/{paymentId}/process
```

#### 3. Get Payment
```http
GET /api/payments/{paymentId}
```

#### 4. Get Payment by Reference
```http
GET /api/payments/reference/{referenceNumber}
```

#### 5. Get User's Payments
```http
GET /api/payments/user/{userId}
```

#### 6. Get Pending Payments
```http
GET /api/payments/pending
```

#### 7. Cancel Payment
```http
POST /api/payments/{paymentId}/cancel
```

---

### TRANSACTION MANAGEMENT

#### 1. Get Transaction
```http
GET /api/transactions/{transactionId}
```

#### 2. Get Transaction by ID
```http
GET /api/transactions/id/{transactionId}
```

#### 3. Get Transactions by Payment
```http
GET /api/transactions/payment/{paymentId}
```

#### 4. Get Transactions by User
```http
GET /api/transactions/user/{userId}
```

#### 5. Get Recent Transactions
```http
GET /api/transactions/user/{userId}/recent?limit=10
```

---

### WORKER MANAGEMENT (Async Processing)

#### 1. Start Worker 1
```http
POST /api/workers/start/worker1
```

#### 2. Start Worker 2
```http
POST /api/workers/start/worker2
```

#### 3. Start Worker 3
```http
POST /api/workers/start/worker3
```

---

## Complete Flow Example

### 1. Create User
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "SecurePass123",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "9876543210"
  }'
```
Response: `userId`, `walletId` created automatically

### 2. Credit Wallet
```bash
curl -X POST http://localhost:8080/api/wallets/{walletId}/credit?amount=1000
```

### 3. Initiate Payment
```bash
curl -X POST http://localhost:8080/api/payments/initiate \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "{userId}",
    "amount": 500,
    "paymentMethod": "WALLET",
    "description": "Test Payment"
  }'
```
Response: `paymentId`, `referenceNumber`, status = PENDING

### 4. Process Payment
```bash
curl -X POST http://localhost:8080/api/payments/{paymentId}/process
```
Response: status = SUCCESS, wallet debited, transaction created

### 5. View Transactions
```bash
curl -X GET http://localhost:8080/api/transactions/user/{userId}
```

---

## Project Structure

```
src/main/java/com/payment/paymentgateway/
├── config/
│   ├── SecurityConfig.java (PasswordEncoder)
│   └── RedisConfig.java (Redis Template)
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── InsufficientBalanceException.java
│   └── PaymentProcessingException.java
├── user/
│   ├── model/ (User, Wallet, UserRole)
│   ├── repository/ (UserRepository, WalletRepository)
│   ├── service/ (UserService, WalletService)
│   ├── controller/ (UserController, WalletController)
│   └── dto/ (UserRegisterRequest, UserResponse, WalletResponse)
├── payment/
│   ├── model/ (Payment, PaymentStatus, PaymentMethod)
│   ├── repository/ (PaymentRepository)
│   ├── service/ (PaymentService)
│   ├── controller/ (PaymentController)
│   ├── dto/ (PaymentRequest, PaymentResponse)
│   ├── validator/ (BalanceValidator)
│   └── gateway/ (BankGatewayClient)
├── transaction/
│   ├── model/ (Transaction, TransactionType)
│   ├── repository/ (TransactionRepository)
│   ├── service/ (TransactionService)
│   ├── controller/ (TransactionController)
│   └── dto/ (TransactionResponse)
├── queue/
│   ├── config/ (RedisConfig)
│   ├── model/ (PaymentQueueMessage)
│   └── service/ (PaymentQueueProducer)
├── worker/
│   ├── service/ (PaymentWorker - 3 workers)
│   └── controller/ (WorkerController)
├── notification/
│   ├── model/ (Notification, NotificationType)
│   ├── repository/ (NotificationRepository)
│   └── service/ (NotificationService)
└── PaymentGatewayApplication.java (@EnableAsync)
```

---

## Key Features

✅ User Registration & Authentication
✅ Wallet Management (Credit/Debit)
✅ Payment Processing (Multiple Methods)
✅ Transaction Logging & Audit
✅ Balance Validation
✅ Async Processing with Redis Queue
✅ Worker Nodes (3 parallel workers)
✅ Exception Handling
✅ Notification Service
✅ Bank Gateway Integration
✅ ORM with JPA/Hibernate
✅ PostgreSQL Database
✅ Spring Security
✅ Transaction Management

---

## Technologies Used

- Spring Boot 3.5.14
- Spring Data JPA
- Spring Security
- PostgreSQL
- Redis
- Lombok
- Jakarta Persistence

All files are created and ready to use. Start the application and test the endpoints!
