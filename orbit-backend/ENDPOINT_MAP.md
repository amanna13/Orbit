# 🗺️ Orbit Backend Endpoint Map

## API Base URL: `http://localhost:4000`

```
┌─────────────────────────────────────────────────────────────┐
│                    ORBIT BACKEND API                         │
│                     Port: 4000                               │
└─────────────────────────────────────────────────────────────┘

📊 HEALTH CHECK
├─ GET /health
│  └─ Returns: { ok: true, timestamp, uptime }
│  └─ Status: ✅ WORKING

🌊 FLOW BLOCKCHAIN OPERATIONS (/api/flow)
├─ Pod Management
│  ├─ POST   /api/flow/pods
│  │  └─ Create new pod
│  │  └─ Body: { name, creatorAddress, role }
│  │  └─ Returns: { success, transactionId, message }
│  │  └─ Status: ⚠️ NEEDS CADENCE FILES
│  │
│  ├─ POST   /api/flow/pods/join
│  │  └─ Join existing pod
│  │  └─ Body: { joinCode, signerAddress }
│  │  └─ Returns: { success, transactionId, message }
│  │  └─ Status: ⚠️ NEEDS CADENCE FILES
│  │
│  ├─ POST   /api/flow/pods/leave
│  │  └─ Leave a pod
│  │  └─ Body: { podID, signerAddress }
│  │  └─ Returns: { success, transactionId, message }
│  │  └─ Status: ⚠️ NEEDS CADENCE FILES
│  │
│  ├─ GET    /api/flow/pods
│  │  └─ Get all pods
│  │  └─ Returns: { success, data: [pods...] }
│  │  └─ Status: ⚠️ NEEDS CADENCE FILES
│  │
│  └─ GET    /api/flow/pods/:podID
│     └─ Get pod details
│     └─ Params: podID (number)
│     └─ Returns: { success, data: {...} }
│     └─ Status: ⚠️ NEEDS CADENCE FILES
│
└─ Transfers
   └─ POST   /api/flow/transfer
      └─ Transfer between pods
      └─ Body: { senderPodID, receiverPodID, amount }
      └─ Returns: { success, transactionId, message }
      └─ Status: ⚠️ NEEDS CADENCE FILES

💰 FORTE PAYMENT OPERATIONS (/api/forte)
├─ Scheduled Payments
│  ├─ POST   /api/forte/scheduled-payments
│  │  └─ Create recurring payment
│  │  └─ Body: { payerPodID, receiverPodID, amount, intervalDays }
│  │  └─ Returns: { success, data: { scheduleID, ... }, message }
│  │  └─ Status: ✅ READY (requires Forte API)
│  │
│  ├─ GET    /api/forte/scheduled-payments?podID={id}
│  │  └─ List all scheduled payments for pod
│  │  └─ Query: podID (required)
│  │  └─ Returns: { success, data: [schedules...], message }
│  │  └─ Status: ✅ READY (requires Forte API)
│  │
│  ├─ GET    /api/forte/scheduled-payments/:scheduleID
│  │  └─ Get payment details
│  │  └─ Params: scheduleID
│  │  └─ Returns: { success, data: {...}, message }
│  │  └─ Status: ✅ READY (requires Forte API)
│  │
│  ├─ PATCH  /api/forte/scheduled-payments/:scheduleID
│  │  └─ Update payment amount/interval
│  │  └─ Params: scheduleID
│  │  └─ Body: { amount?, intervalDays? }
│  │  └─ Returns: { success, data: {...}, message }
│  │  └─ Status: ✅ READY (requires Forte API)
│  │
│  └─ DELETE /api/forte/scheduled-payments/:scheduleID
│     └─ Cancel scheduled payment
│     └─ Params: scheduleID
│     └─ Returns: { success, data: {...}, message }
│     └─ Status: ✅ READY (requires Forte API)
│
└─ Immediate Payments
   └─ POST   /api/forte/immediate-payment
      └─ Trigger instant payment
      └─ Body: { payerPodID, receiverPodID, amount }
      └─ Returns: { success, data: { transactionId, ... }, message }
      └─ Status: ✅ READY (requires Forte API)
```

## 📊 Endpoint Status Summary

| Category | Total | Working | Blocked | Ready % |
|----------|-------|---------|---------|---------|
| Health | 1 | 1 | 0 | 100% |
| Flow | 6 | 0 | 6 | 0% |
| Forte | 6 | 6 | 0 | 100% |
| **TOTAL** | **13** | **7** | **6** | **54%** |

## 🔧 Service Dependencies

```
┌──────────────┐
│   Frontend   │
└──────┬───────┘
       │
       ├─────────────┐
       │             │
       v             v
┌─────────────┐  ┌──────────────┐
│ /api/flow/* │  │ /api/forte/* │
└──────┬──────┘  └──────┬───────┘
       │                │
       v                v
┌─────────────┐  ┌──────────────┐
│ flowService │  │ forteService │
└──────┬──────┘  └──────┬───────┘
       │                │
       v                v
┌─────────────┐  ┌──────────────┐
│ Flow Emul.  │  │  Forte API   │
│ + Cadence   │  │ (External)   │
│   Files     │  │              │
└─────────────┘  └──────────────┘
  ⚠️ MISSING      ✅ CONFIGURED
```

## 🎯 Testing Endpoints

### ✅ Working Now
```bash
# Health check
curl http://localhost:4000/health

# Forte endpoints (if API is available)
curl http://localhost:4000/api/forte/scheduled-payments?podID=1
```

### ⚠️ Will Work After Setup
```bash
# After running setup.ps1 and deploying contracts
curl -X POST http://localhost:4000/api/flow/pods \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Pod","creatorAddress":"0x01","role":"admin"}'
```

## 📝 Response Format

All endpoints follow consistent format:

### Success Response
```json
{
  "success": true,
  "data": { /* ... */ },
  "message": "Operation successful"
}
```

### Error Response
```json
{
  "success": false,
  "error": "Error description",
  "message": "Failed to complete operation"
}
```

## 🔗 Related Files

- **Routes:** `src/routes/flow.ts`, `src/routes/forte.ts`
- **Controllers:** `src/controllers/flowController.ts`, `src/controllers/forteController.ts`
- **Services:** `src/services/flowService.ts`, `src/services/forteService.ts`
- **Config:** `src/utils/flowConfig.ts`
- **Documentation:** `API_DOCUMENTATION.md`, `FORTE_INTEGRATION.md`
