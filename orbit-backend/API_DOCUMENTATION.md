# Orbit Backend API Documentation

## Base URL
```
http://localhost:4000
```

## Authentication
Currently using FCL authorization placeholders. In production, implement proper service account or user wallet authorization.

---

## Health Check

### Get Health Status
Check if the server is running.

**Endpoint:** `GET /health`

**Response:**
```json
{
  "ok": true,
  "timestamp": "2025-10-24T12:00:00.000Z",
  "uptime": 123.456
}
```

---

## Pod Management

### Create Pod
Create a new pod with a name, creator address, and role.

**Endpoint:** `POST /api/flow/pods`

**Request Body:**
```json
{
  "name": "My Pod",
  "creatorAddress": "0x01",
  "role": "admin"
}
```

**Response:**
```json
{
  "success": true,
  "transactionId": "abc123...",
  "message": "Pod created successfully"
}
```

---

### Join Pod
Join an existing pod using a join code.

**Endpoint:** `POST /api/flow/pods/join`

**Request Body:**
```json
{
  "joinCode": "JOIN123",
  "signerAddress": "0x02"
}
```

**Response:**
```json
{
  "success": true,
  "transactionId": "def456...",
  "message": "Successfully joined pod"
}
```

---

### Leave Pod
Leave a pod by providing the pod ID.

**Endpoint:** `POST /api/flow/pods/leave`

**Request Body:**
```json
{
  "podID": 1,
  "signerAddress": "0x02"
}
```

**Response:**
```json
{
  "success": true,
  "transactionId": "ghi789...",
  "message": "Successfully left pod"
}
```

---

### Get All Pods
Retrieve a list of all pods.

**Endpoint:** `GET /api/flow/pods`

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "My Pod",
      "members": []
    }
  ]
}
```

---

### Get Pod Details
Get detailed information about a specific pod.

**Endpoint:** `GET /api/flow/pods/:podID`

**Parameters:**
- `podID` (path parameter) - The ID of the pod

**Example:** `GET /api/flow/pods/1`

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "My Pod",
    "creator": "0x01",
    "members": [],
    "balance": 0
  }
}
```

---

## Forte Scheduled Payments

### Create Scheduled Payment
Create a recurring payment schedule between two pods.

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

**Response:**
```json
{
  "success": true,
  "data": {
    "scheduleID": "sched_abc123",
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

---

### List Scheduled Payments
Get all scheduled payments for a specific pod.

**Endpoint:** `GET /api/forte/scheduled-payments?podID={podID}`

**Query Parameters:**
- `podID` (required) - The ID of the pod

**Example:** `GET /api/forte/scheduled-payments?podID=1`

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "scheduleID": "sched_abc123",
      "payerPodID": 1,
      "receiverPodID": 2,
      "amount": 50.0,
      "intervalDays": 7,
      "status": "active"
    }
  ],
  "message": "Scheduled payments retrieved successfully"
}
```

---

### Get Scheduled Payment Details
Get details of a specific scheduled payment.

**Endpoint:** `GET /api/forte/scheduled-payments/:scheduleID`

**Parameters:**
- `scheduleID` (path parameter) - The ID of the scheduled payment

**Example:** `GET /api/forte/scheduled-payments/sched_abc123`

**Response:**
```json
{
  "success": true,
  "data": {
    "scheduleID": "sched_abc123",
    "actionType": "transferBetweenPods",
    "metadata": {
      "payerPodID": 1,
      "receiverPodID": 2,
      "amount": 50.0,
      "intervalDays": 7
    },
    "status": "active",
    "nextExecution": "2025-10-31T12:00:00.000Z"
  },
  "message": "Scheduled payment details retrieved successfully"
}
```

---

### Update Scheduled Payment
Update the amount or interval of a scheduled payment.

**Endpoint:** `PATCH /api/forte/scheduled-payments/:scheduleID`

**Parameters:**
- `scheduleID` (path parameter) - The ID of the scheduled payment

**Request Body:**
```json
{
  "amount": 75.0,
  "intervalDays": 14
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "scheduleID": "sched_abc123",
    "amount": 75.0,
    "intervalDays": 14
  },
  "message": "Scheduled payment updated successfully"
}
```

---

### Cancel Scheduled Payment
Cancel an existing scheduled payment.

**Endpoint:** `DELETE /api/forte/scheduled-payments/:scheduleID`

**Parameters:**
- `scheduleID` (path parameter) - The ID of the scheduled payment to cancel

**Example:** `DELETE /api/forte/scheduled-payments/sched_abc123`

**Response:**
```json
{
  "success": true,
  "data": {
    "scheduleID": "sched_abc123",
    "status": "canceled"
  },
  "message": "Scheduled payment canceled successfully"
}
```

---

### Trigger Immediate Payment
Execute an immediate one-time payment between pods.

**Endpoint:** `POST /api/forte/immediate-payment`

**Request Body:**
```json
{
  "payerPodID": 1,
  "receiverPodID": 2,
  "amount": 100.0
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "transactionId": "txn_xyz789",
    "payerPodID": 1,
    "receiverPodID": 2,
    "amount": 100.0,
    "status": "completed"
  },
  "message": "Immediate payment triggered successfully"
}
```

---

## Transfers

### Transfer Between Pods
Transfer tokens from one pod to another.

**Endpoint:** `POST /api/flow/transfer`

**Request Body:**
```json
{
  "senderPodID": 1,
  "receiverPodID": 2,
  "amount": 10.5
}
```

**Response:**
```json
{
  "success": true,
  "transactionId": "jkl012...",
  "message": "Transfer completed successfully"
}
```

---

## Error Responses

All endpoints may return error responses in the following format:

```json
{
  "success": false,
  "error": "Error message describing what went wrong"
}
```

**Common HTTP Status Codes:**
- `200` - Success
- `201` - Created
- `400` - Bad Request (missing or invalid parameters)
- `404` - Not Found
- `500` - Internal Server Error

---

## Cadence Files Location

The service expects Cadence transaction and script files in the following structure:

```
cadence/
├── transactions/
│   ├── createPod.cdc
│   ├── JoinPod.cdc
│   ├── LeavePod.cdc
│   └── TransferBetweenPods.cdc
└── scripts/
    ├── GetAllPods.cdc
    └── GetPodDetails.cdc
```

---

## Environment Variables

Required environment variables (see `.env.example`):

```
FLOW_ACCESS_NODE=http://127.0.0.1:8888
FCL_WALLET_DISCOVERY=http://localhost:8701/fcl/authn
SERVICE_ACCOUNT_ADDRESS=your_service_account_address
SERVICE_ACCOUNT_PRIVATE_KEY=your_service_account_private_key
FORTE_API_BASE_URL=http://localhost:3000/api/forte
FORTE_API_KEY=your_forte_api_key
PORT=4000
NODE_ENV=development
```

---

## Testing with cURL

### Create a Pod
```bash
curl -X POST http://localhost:4000/api/flow/pods \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Pod","creatorAddress":"0x01","role":"admin"}'
```

### Get All Pods
```bash
curl http://localhost:4000/api/flow/pods
```

### Join a Pod
```bash
curl -X POST http://localhost:4000/api/flow/pods/join \
  -H "Content-Type: application/json" \
  -d '{"joinCode":"ABC123","signerAddress":"0x02"}'
```

### Transfer Between Pods
```bash
curl -X POST http://localhost:4000/api/flow/transfer \
  -H "Content-Type: application/json" \
  -d '{"senderPodID":1,"receiverPodID":2,"amount":50}'
```

### Create Scheduled Payment
```bash
curl -X POST http://localhost:4000/api/forte/scheduled-payments \
  -H "Content-Type: application/json" \
  -d '{"payerPodID":1,"receiverPodID":2,"amount":50,"intervalDays":7}'
```

### List Scheduled Payments
```bash
curl "http://localhost:4000/api/forte/scheduled-payments?podID=1"
```

### Trigger Immediate Payment
```bash
curl -X POST http://localhost:4000/api/forte/immediate-payment \
  -H "Content-Type: application/json" \
  -d '{"payerPodID":1,"receiverPodID":2,"amount":100}'
```

### Cancel Scheduled Payment
```bash
curl -X DELETE http://localhost:4000/api/forte/scheduled-payments/sched_abc123
```

### Update Scheduled Payment
```bash
curl -X PATCH http://localhost:4000/api/forte/scheduled-payments/sched_abc123 \
  -H "Content-Type: application/json" \
  -d '{"amount":75,"intervalDays":14}'
```
