# Forte Service Integration Guide

## Overview

The Forte service provides integration with the Flow Forte Actions API for managing scheduled and immediate payments between pods.

## Configuration

Add to your `.env` file:

```env
FORTE_API_BASE_URL=http://localhost:3000/api/forte
FORTE_API_KEY=your_forte_api_key_here
```

## Features

### ✅ Scheduled Payments
- **Create** recurring payments between pods
- **List** all scheduled payments for a pod
- **Get** details of a specific scheduled payment
- **Update** amount or interval of existing schedules
- **Cancel** scheduled payments

### ✅ Immediate Payments
- Trigger one-time instant payments between pods

## API Endpoints

### Scheduled Payments

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/forte/scheduled-payments` | Create scheduled payment |
| GET | `/api/forte/scheduled-payments?podID={id}` | List scheduled payments |
| GET | `/api/forte/scheduled-payments/:scheduleID` | Get payment details |
| PATCH | `/api/forte/scheduled-payments/:scheduleID` | Update payment |
| DELETE | `/api/forte/scheduled-payments/:scheduleID` | Cancel payment |

### Immediate Payments

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/forte/immediate-payment` | Trigger immediate payment |

## Usage Examples

### 1. Create a Weekly Scheduled Payment

```typescript
import { createScheduledPayment } from './services/forteService';

const result = await createScheduledPayment(
  1,    // payerPodID
  2,    // receiverPodID
  50.0, // amount
  7     // intervalDays (weekly)
);

console.log(result.data.scheduleID); // "sched_abc123"
```

**cURL:**
```bash
curl -X POST http://localhost:4000/api/forte/scheduled-payments \
  -H "Content-Type: application/json" \
  -d '{
    "payerPodID": 1,
    "receiverPodID": 2,
    "amount": 50,
    "intervalDays": 7
  }'
```

### 2. List All Scheduled Payments for a Pod

```typescript
import { listScheduledPayments } from './services/forteService';

const result = await listScheduledPayments(1);
console.log(result.data); // Array of scheduled payments
```

**cURL:**
```bash
curl "http://localhost:4000/api/forte/scheduled-payments?podID=1"
```

### 3. Update a Scheduled Payment

```typescript
import { updateScheduledPayment } from './services/forteService';

const result = await updateScheduledPayment('sched_abc123', {
  amount: 75.0,
  intervalDays: 14
});
```

**cURL:**
```bash
curl -X PATCH http://localhost:4000/api/forte/scheduled-payments/sched_abc123 \
  -H "Content-Type: application/json" \
  -d '{"amount": 75, "intervalDays": 14}'
```

### 4. Cancel a Scheduled Payment

```typescript
import { cancelScheduledPayment } from './services/forteService';

const result = await cancelScheduledPayment('sched_abc123');
console.log(result.message); // "Scheduled payment canceled successfully"
```

**cURL:**
```bash
curl -X DELETE http://localhost:4000/api/forte/scheduled-payments/sched_abc123
```

### 5. Trigger an Immediate Payment

```typescript
import { triggerImmediatePayment } from './services/forteService';

const result = await triggerImmediatePayment(
  1,     // payerPodID
  2,     // receiverPodID
  100.0  // amount
);

console.log(result.data.transactionId);
```

**cURL:**
```bash
curl -X POST http://localhost:4000/api/forte/immediate-payment \
  -H "Content-Type: application/json" \
  -d '{
    "payerPodID": 1,
    "receiverPodID": 2,
    "amount": 100
  }'
```

## Response Format

All Forte service functions return a consistent response format:

```typescript
{
  success: boolean;
  data?: any;
  message: string;
}
```

**Success Example:**
```json
{
  "success": true,
  "data": {
    "scheduleID": "sched_abc123",
    "status": "active"
  },
  "message": "Scheduled payment created successfully"
}
```

**Error Example:**
```json
{
  "success": false,
  "data": {
    "error": "Pod not found"
  },
  "message": "Failed to create scheduled payment"
}
```

## Error Handling

All functions handle errors gracefully:

- **Network errors** - Logged and returned with error message
- **API errors** - Response data included in error object
- **Validation errors** - 400 status with descriptive message
- **Timeout errors** - 10 second timeout configured

## Service Architecture

```
src/
├── services/
│   └── forteService.ts       # Forte API integration
├── controllers/
│   └── forteController.ts    # Request handlers
└── routes/
    └── forte.ts              # API routes
```

## Axios Client Configuration

The Forte service uses a pre-configured Axios client:

- **Base URL**: From `FORTE_API_BASE_URL` environment variable
- **Authorization**: Bearer token from `FORTE_API_KEY`
- **Timeout**: 10 seconds
- **Content-Type**: application/json

## Common Intervals

| Interval | Days |
|----------|------|
| Daily | 1 |
| Weekly | 7 |
| Bi-weekly | 14 |
| Monthly | 30 |
| Quarterly | 90 |

## Testing

### Test Scheduled Payment Creation
```bash
npm run dev
# In another terminal:
curl -X POST http://localhost:4000/api/forte/scheduled-payments \
  -H "Content-Type: application/json" \
  -d '{"payerPodID":1,"receiverPodID":2,"amount":10,"intervalDays":1}'
```

### Test Immediate Payment
```bash
curl -X POST http://localhost:4000/api/forte/immediate-payment \
  -H "Content-Type: application/json" \
  -d '{"payerPodID":1,"receiverPodID":2,"amount":5}'
```

## Production Considerations

1. **API Key Security**: Store `FORTE_API_KEY` securely, never commit to version control
2. **Rate Limiting**: Implement rate limiting for Forte endpoints
3. **Retry Logic**: Consider adding retry logic for failed API calls
4. **Monitoring**: Log all Forte API interactions for debugging
5. **Webhooks**: Set up Forte webhooks for payment status updates

## Next Steps

1. Set up Forte API credentials
2. Test endpoints in development
3. Implement webhook handlers for payment status updates
4. Add rate limiting middleware
5. Set up monitoring and alerting

---

For full API documentation, see [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)
