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
