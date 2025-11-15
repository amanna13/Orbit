# Orbit Backend API Endpoints - Complete Reference

**Base URL:** `http://localhost:4000`

---

## 🔐 Health & System

### 1. Health Check
**Endpoint:** `GET /health`

**Request:** None

**Response:**
```json
{
  "ok": true,
  "timestamp": "2025-11-08T12:34:56.789Z",
  "uptime": 123.456
}
```

---

## 👥 Pod Management Endpoints

### 2. Create Pod
**Endpoint:** `POST /api/flow/pods`

**Request Body:**
```json
{
  "name": "My Awesome Pod",
  "creatorAddress": "0x01cf0e2f2f715450",
  "role": "admin"
}
```

**Success Response (201):**
```json
{
  "success": true,
  "transactionId": "a1b2c3d4e5f6...",
  "podID": 1,
  "joinCode": "8f4b2a1c",
  "message": "Pod created successfully"
}
```

**Error Response (400):**
```json
{
  "success": false,
  "error": "Missing required fields: name, creatorAddress, role"
}
```

**Error Response (500):**
```json
{
  "success": false,
  "error": "Failed to create pod"
}
```

---

### 3. Join Pod
**Endpoint:** `POST /api/flow/pods/join`

**Request Body:**
```json
{
  "joinCode": "8f4b2a1c",
  "signerAddress": "0x179b6b1cb6755e31"
}
```

**Success Response (200):**
```json
{
  "success": true,
  "transactionId": "b2c3d4e5f6a7...",
  "message": "Successfully joined pod"
}
```

**Error Response (400):**
```json
{
  "success": false,
  "error": "Missing required fields: joinCode, signerAddress"
}
```

**Error Response (500):**
```json
{
  "success": false,
  "error": "Failed to join pod"
}
```

---

### 4. Leave Pod
**Endpoint:** `POST /api/flow/pods/leave`

**Request Body:**
```json
{
  "podID": 1,
  "signerAddress": "0x179b6b1cb6755e31"
}
```

**Success Response (200):**
```json
{
  "success": true,
  "transactionId": "c3d4e5f6a7b8...",
  "message": "Successfully left pod"
}
```

**Error Response (400):**
```json
{
  "success": false,
  "error": "Missing required fields: podID, signerAddress"
}
```

**Error Response (500):**
```json
{
  "success": false,
  "error": "Failed to leave pod"
}
```

---

### 5. Get All Pods
**Endpoint:** `GET /api/flow/pods`

**Request:** None

**Success Response (200):**
```json
{
  "success": true,
  "data": {
    "1": "Marketing Team Pod",
    "2": "Engineering Pod",
    "3": "Design Team"
  }
}
```

**Error Response (500):**
```json
{
  "success": false,
  "error": "Failed to retrieve pods"
}
```

---

### 6. Get Pod Details
**Endpoint:** `GET /api/flow/pods/:podID`

**URL Parameters:**
- `podID` - The ID of the pod (e.g., `/api/flow/pods/1`)

**Success Response (200):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "Marketing Team Pod",
    "joinCode": "8f4b2a1c",
    "members": {
      "0x01cf0e2f2f715450": "admin",
      "0x179b6b1cb6755e31": "member",
      "0x2345678901234567": "member"
    },
    "memberBalances": {
      "0x01cf0e2f2f715450": 150.5,
      "0x179b6b1cb6755e31": 75.25,
      "0x2345678901234567": 200.0
    },
    "podBalance": 1000.75,
    "memberCount": 3
  }
}
```

**Error Response (400):**
```json
{
  "success": false,
  "error": "Missing required parameter: podID"
}
```

**Error Response (500):**
```json
{
  "success": false,
  "error": "Failed to retrieve pod details"
}
```

---

### 7. Get Pods by Address
**Endpoint:** `GET /api/flow/pods/by-address/:address`

**URL Parameters:**
- `address` - Flow address (e.g., `/api/flow/pods/by-address/0x01cf0e2f2f715450`)

**Success Response (200):**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "Marketing Team Pod",
      "joinCode": "8f4b2a1c",
      "myBalance": 150.5
    },
    {
      "id": 3,
      "name": "Design Team",
      "joinCode": "a1b2c3d4",
      "myBalance": 75.0
    }
  ]
}
```

**Error Response (400):**
```json
{
  "success": false,
  "error": "Missing required parameter: address"
}
```

**Error Response (500):**
```json
{
  "success": false,
  "error": "Failed to retrieve pods by address"
}
```

---

## 💰 Account & Balance Endpoints

### 8. Get Flow Balance
**Endpoint:** `GET /api/flow/balance/:address`

**URL Parameters:**
- `address` - Flow address (e.g., `/api/flow/balance/0x01cf0e2f2f715450`)

**Success Response (200):**
```json
{
  "success": true,
  "data": {
    "address": "0x01cf0e2f2f715450",
    "balance": 1234.56789012,
    "formatted": "1234.56789012 FLOW"
  }
}
```

**Error Response (400):**
```json
{
  "success": false,
  "error": "Missing required parameter: address"
}
```

**Error Response (500):**
```json
{
  "success": false,
  "error": "Failed to retrieve Flow balance"
}
```

---

## 💸 Transfer & Deposit Endpoints

### 9. Transfer Between Pods
**Endpoint:** `POST /api/flow/transfer`

**Request Body:**
```json
{
  "senderPodID": 1,
  "receiverPodID": 2,
  "amount": 50.5
}
```

**Success Response (200):**
```json
{
  "success": true,
  "transactionId": "d4e5f6a7b8c9...",
  "message": "Transfer completed successfully"
}
```

**Error Response (400):**
```json
{
  "success": false,
  "error": "Missing required fields: senderPodID, receiverPodID, amount"
}
```

**Error Response (500):**
```json
{
  "success": false,
  "error": "Failed to complete transfer"
}
```

---

### 10. Deposit to Pod
**Endpoint:** `POST /api/flow/deposit`

**Request Body:**
```json
{
  "podID": 1,
  "amount": 100.0,
  "signerAddress": "0x01cf0e2f2f715450"
}
```

**Success Response (200):**
```json
{
  "success": true,
  "transactionId": "e5f6a7b8c9d0...",
  "message": "Deposit completed successfully"
}
```

**Error Response (400):**
```json
{
  "success": false,
  "error": "Missing required fields: podID, amount, signerAddress"
}
```

**Error Response (500):**
```json
{
  "success": false,
  "error": "Failed to complete deposit"
}
```

---

## 📊 Distribution & Disbursement Endpoints

### 11. Distribute Pod Funds (Equal Share)
**Endpoint:** `POST /api/flow/distribute`

**Request Body:**
```json
{
  "podID": 1,
  "signerAddress": "0x01cf0e2f2f715450"
}
```

**Success Response (200):**
```json
{
  "success": true,
  "transactionId": "f6a7b8c9d0e1...",
  "message": "Distribution completed successfully"
}
```

**Error Response (400):**
```json
{
  "success": false,
  "error": "Missing required fields: podID, signerAddress"
}
```

**Error Response (500):**
```json
{
  "success": false,
  "error": "Failed to complete distribution"
}
```

---

### 12. Execute Pod Disbursement (Sink Payouts)
**Endpoint:** `POST /api/flow/disburse`

**Request Body:**
```json
{
  "podID": 1,
  "triggerType": "manual",
  "signerAddress": "0x01cf0e2f2f715450"
}
```

**Note:** `triggerType` must be either `"manual"` or `"flasher"`

**Success Response (200):**
```json
{
  "success": true,
  "transactionId": "a7b8c9d0e1f2...",
  "message": "Disbursement completed successfully"
}
```

**Error Response (400) - Missing Fields:**
```json
{
  "success": false,
  "error": "Missing required fields: podID, triggerType, signerAddress"
}
```

**Error Response (400) - Invalid Trigger Type:**
```json
{
  "success": false,
  "error": "Invalid triggerType. Must be \"manual\" or \"flasher\""
}
```

**Error Response (500):**
```json
{
  "success": false,
  "error": "Failed to execute disbursement"
}
```

---

## 💳 Forte Payment Endpoints

### 13. Create Scheduled Payment
**Endpoint:** `POST /api/forte/scheduled-payments`

**Request Body:**
```json
{
  "payerPodID": 1,
  "receiverPodID": 2,
  "amount": 50.0,
  "intervalDays": 7
}
```

**Success Response (200):**
```json
{
  "success": true,
  "data": {
    "scheduleID": "sched_abc123xyz",
    "actionType": "transferBetweenPods",
    "metadata": {
      "payerPodID": 1,
      "receiverPodID": 2,
      "amount": 50.0,
      "intervalDays": 7
    }
  },
  "message": "Scheduled payment created successfully"
}
```

**Error Response:**
```json
{
  "success": false,
  "data": {
    "error": "Insufficient balance"
  },
  "message": "Failed to create scheduled payment"
}
```

---

### 14. List Scheduled Payments
**Endpoint:** `GET /api/forte/scheduled-payments?podID={id}`

**Query Parameters:**
- `podID` - Required. The pod ID to query (e.g., `?podID=1`)

**Success Response (200):**
```json
{
  "success": true,
  "data": [
    {
      "scheduleID": "sched_abc123xyz",
      "payerPodID": 1,
      "receiverPodID": 2,
      "amount": 50.0,
      "intervalDays": 7,
      "status": "active"
    },
    {
      "scheduleID": "sched_def456uvw",
      "payerPodID": 1,
      "receiverPodID": 3,
      "amount": 25.0,
      "intervalDays": 30,
      "status": "active"
    }
  ],
  "message": "Scheduled payments retrieved successfully"
}
```

---

### 15. Get Scheduled Payment Details
**Endpoint:** `GET /api/forte/scheduled-payments/:scheduleID`

**URL Parameters:**
- `scheduleID` - The schedule ID (e.g., `/api/forte/scheduled-payments/sched_abc123xyz`)

**Success Response (200):**
```json
{
  "success": true,
  "data": {
    "scheduleID": "sched_abc123xyz",
    "actionType": "transferBetweenPods",
    "metadata": {
      "payerPodID": 1,
      "receiverPodID": 2,
      "amount": 50.0,
      "intervalDays": 7
    },
    "status": "active",
    "nextExecution": "2025-11-15T12:00:00.000Z"
  },
  "message": "Scheduled payment details retrieved successfully"
}
```

---

### 16. Update Scheduled Payment
**Endpoint:** `PATCH /api/forte/scheduled-payments/:scheduleID`

**URL Parameters:**
- `scheduleID` - The schedule ID

**Request Body:**
```json
{
  "amount": 75.0,
  "intervalDays": 14
}
```

**Success Response (200):**
```json
{
  "success": true,
  "data": {
    "scheduleID": "sched_abc123xyz",
    "amount": 75.0,
    "intervalDays": 14
  },
  "message": "Scheduled payment updated successfully"
}
```

---

### 17. Cancel Scheduled Payment
**Endpoint:** `DELETE /api/forte/scheduled-payments/:scheduleID`

**URL Parameters:**
- `scheduleID` - The schedule ID

**Success Response (200):**
```json
{
  "success": true,
  "data": {
    "scheduleID": "sched_abc123xyz",
    "status": "canceled"
  },
  "message": "Scheduled payment canceled successfully"
}
```

---

### 18. Trigger Immediate Payment
**Endpoint:** `POST /api/forte/immediate-payment`

**Request Body:**
```json
{
  "payerPodID": 1,
  "receiverPodID": 2,
  "amount": 100.0
}
```

**Success Response (200):**
```json
{
  "success": true,
  "data": {
    "transactionId": "txn_xyz789abc",
    "payerPodID": 1,
    "receiverPodID": 2,
    "amount": 100.0,
    "status": "completed"
  },
  "message": "Immediate payment triggered successfully"
}
```

---

## 📝 Endpoint Summary Table

| # | Method | Endpoint | Purpose |
|---|--------|----------|---------|
| 1 | GET | `/health` | Health check |
| 2 | POST | `/api/flow/pods` | Create pod |
| 3 | POST | `/api/flow/pods/join` | Join pod |
| 4 | POST | `/api/flow/pods/leave` | Leave pod |
| 5 | GET | `/api/flow/pods` | Get all pods |
| 6 | GET | `/api/flow/pods/:podID` | Get pod details |
| 7 | GET | `/api/flow/pods/by-address/:address` | Get user's pods |
| 8 | GET | `/api/flow/balance/:address` | Get Flow balance |
| 9 | POST | `/api/flow/transfer` | Transfer between pods |
| 10 | POST | `/api/flow/deposit` | Deposit to pod |
| 11 | POST | `/api/flow/distribute` | Equal distribution |
| 12 | POST | `/api/flow/disburse` | Sink disbursement |
| 13 | POST | `/api/forte/scheduled-payments` | Create schedule |
| 14 | GET | `/api/forte/scheduled-payments` | List schedules |
| 15 | GET | `/api/forte/scheduled-payments/:scheduleID` | Schedule details |
| 16 | PATCH | `/api/forte/scheduled-payments/:scheduleID` | Update schedule |
| 17 | DELETE | `/api/forte/scheduled-payments/:scheduleID` | Cancel schedule |
| 18 | POST | `/api/forte/immediate-payment` | Immediate payment |

**Total Endpoints: 18**

---

## 🧪 Testing Examples (cURL)

### Create a Pod
```bash
curl -X POST http://localhost:4000/api/flow/pods \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Pod",
    "creatorAddress": "0x01cf0e2f2f715450",
    "role": "admin"
  }'
```

### Get Flow Balance
```bash
curl http://localhost:4000/api/flow/balance/0x01cf0e2f2f715450
```

### Join Pod
```bash
curl -X POST http://localhost:4000/api/flow/pods/join \
  -H "Content-Type: application/json" \
  -d '{
    "joinCode": "8f4b2a1c",
    "signerAddress": "0x179b6b1cb6755e31"
  }'
```

### Get User's Pods
```bash
curl http://localhost:4000/api/flow/pods/by-address/0x01cf0e2f2f715450
```

### Deposit to Pod
```bash
curl -X POST http://localhost:4000/api/flow/deposit \
  -H "Content-Type: application/json" \
  -d '{
    "podID": 1,
    "amount": 100.0,
    "signerAddress": "0x01cf0e2f2f715450"
  }'
```

### Transfer Between Pods
```bash
curl -X POST http://localhost:4000/api/flow/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "senderPodID": 1,
    "receiverPodID": 2,
    "amount": 50.0
  }'
```

### Distribute Pod Funds
```bash
curl -X POST http://localhost:4000/api/flow/distribute \
  -H "Content-Type: application/json" \
  -d '{
    "podID": 1,
    "signerAddress": "0x01cf0e2f2f715450"
  }'
```

### Execute Disbursement
```bash
curl -X POST http://localhost:4000/api/flow/disburse \
  -H "Content-Type: application/json" \
  -d '{
    "podID": 1,
    "triggerType": "manual",
    "signerAddress": "0x01cf0e2f2f715450"
  }'
```

### Create Scheduled Payment
```bash
curl -X POST http://localhost:4000/api/forte/scheduled-payments \
  -H "Content-Type: application/json" \
  -d '{
    "payerPodID": 1,
    "receiverPodID": 2,
    "amount": 50.0,
    "intervalDays": 7
  }'
```

---

## 🔄 Common Response Patterns

### Success Pattern
All successful responses include:
- `success: true`
- `data` or `transactionId` (depending on operation)
- `message` (descriptive success message)

### Error Pattern
All error responses include:
- `success: false`
- `error` (error description)
- Optional: `message` for additional context

### HTTP Status Codes
- `200` - Success (GET, POST, PATCH, DELETE operations)
- `201` - Created (Pod creation)
- `400` - Bad Request (missing/invalid parameters)
- `500` - Internal Server Error (transaction/query failures)

---

**Last Updated:** November 8, 2025  
**Backend Version:** 1.0.0  
**Total Endpoints:** 18 (12 Flow + 6 Forte)
