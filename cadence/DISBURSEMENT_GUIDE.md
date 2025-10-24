# Pod Disbursement System

## Overview
The Pod Disbursement system enables automated payouts from a Pod to its configured Sinks (payment receivers). It supports both manual execution by administrators and automated execution via Flow Forte Flasher.

## Key Components

### 1. Contract Updates (Pod.cdc)
- **New Event**: `PodDisbursement(podID, sinkAddress, amount, triggerType)`
- **New Function**: `executeDisbursement(caller, triggerType)` - Calculates and records disbursements

### 2. Transaction: ExecutePodDisbursement.cdc
Executes payouts from Pod to all configured Sinks.

**Parameters:**
- `podID: UInt64` - The Pod executing disbursement
- `triggerType: String` - Either "manual" or "flasher"

**Features:**
- ✅ Authorization check (only pod creator/admin)
- ✅ Balance validation before disbursement
- ✅ Support for both payment modes (fixed/ratio)
- ✅ Flow token transfers to each sink
- ✅ Event emission for tracking
- ✅ Detailed logging

### 3. Script: GetDisbursementPreview.cdc
Preview disbursement amounts before execution.

**Parameters:**
- `podID: UInt64` - The Pod to preview

**Returns:**
```json
{
  "podID": 1,
  "paymentMode": "fixed",
  "podBalance": 1000.0,
  "totalRequired": 800.0,
  "isReady": true,
  "shortage": 0.0,
  "sinkCount": 3,
  "disbursements": [...],
  "remainingAfter": 200.0
}
```

## Usage Workflows

### Setup: Add Sinks to Pod

**Fixed Mode (exact amounts):**
```bash
flow transactions send ./cadence/transactions/AddSink.cdc 1 0x123... 100.0 null --signer emulator-account --network emulator
```

**Ratio Mode (percentages):**
```bash
flow transactions send ./cadence/transactions/SetPaymentMode.cdc 1 "ratio" --signer emulator-account --network emulator
flow transactions send ./cadence/transactions/AddSink.cdc 1 0x123... null 50.0 --signer emulator-account --network emulator
flow transactions send ./cadence/transactions/AddSink.cdc 1 0x456... null 30.0 --signer emulator-account --network emulator
```

### Fund the Pod

Deposit funds through a Source:
```bash
flow transactions send ./cadence/transactions/DepositFromSource.cdc 1 "rent-payment" 1000.0 --signer emulator-account --network emulator
```

### Preview Disbursement

Check readiness before executing:
```bash
flow scripts execute ./cadence/scripts/GetDisbursementPreview.cdc 1 --network emulator
```

### Execute Disbursement

**Manual Execution:**
```bash
flow transactions send ./cadence/transactions/ExecutePodDisbursement.cdc 1 "manual" --signer emulator-account --network emulator
```

**Automated Execution (via Flasher):**
```bash
flow transactions send ./cadence/transactions/ExecutePodDisbursement.cdc 1 "flasher" --signer emulator-account --network emulator
```

## Payment Modes

### Fixed Mode
Each sink receives a **predetermined amount** regardless of pod balance.

**Example:**
- Pod Balance: $1000
- Sink A: $300
- Sink B: $200
- Sink C: $100
- **Total Disbursed:** $600
- **Remaining:** $400

### Ratio Mode
Each sink receives a **percentage** of the pod balance.

**Example:**
- Pod Balance: $1000
- Sink A: 50% → $500
- Sink B: 30% → $300
- Sink C: 20% → $200
- **Total Disbursed:** $1000
- **Remaining:** $0

## Flasher Integration

### Automated Scheduling
Use Flow Forte Flasher to trigger disbursements automatically:

1. **Time-Based Trigger**: Execute every month/week/day
2. **Condition-Based Trigger**: Execute when balance > threshold
3. **Event-Based Trigger**: Execute when specific source deposits

### Example Flasher Configuration
```json
{
  "trigger": {
    "type": "schedule",
    "cron": "0 0 1 * *",
    "description": "Monthly disbursement on 1st"
  },
  "action": {
    "type": "transaction",
    "cadence": "ExecutePodDisbursement.cdc",
    "args": [1, "flasher"]
  }
}
```

### Idempotence Considerations
For automated triggers, ensure:
- ✅ Balance checks prevent double-execution
- ✅ Event tracking for audit trail
- ✅ Sufficient balance before execution
- ✅ Error handling for failed transfers

## Events Emitted

### PodDisbursement Event
```cadence
event PodDisbursement(
    podID: UInt64,
    sinkAddress: Address,
    amount: UFix64,
    triggerType: String
)
```

**Emitted for each sink payment** during disbursement.

**Query Events:**
```bash
flow events get Pod.PodDisbursement --network emulator
```

## Example Complete Flow

### 1. Create Pod
```bash
flow transactions send ./cadence/transactions/CreatePod.cdc "Rental Pod" "creator" --signer emulator-account --network emulator
```

### 2. Add Income Source
```bash
flow transactions send ./cadence/transactions/AddSourceToPod.cdc 1 "tenant-rent" 0x789... 1500.0 true null --signer emulator-account --network emulator
```

### 3. Configure Sinks (Landlord, Utilities, Management)
```bash
# Landlord gets 70%
flow transactions send ./cadence/transactions/SetPaymentMode.cdc 1 "ratio" --signer emulator-account --network emulator
flow transactions send ./cadence/transactions/AddSink.cdc 1 0xLANDLORD null 70.0 --signer emulator-account --network emulator

# Utilities get 20%
flow transactions send ./cadence/transactions/AddSink.cdc 1 0xUTILITIES null 20.0 --signer emulator-account --network emulator

# Management fee 10%
flow transactions send ./cadence/transactions/AddSink.cdc 1 0xMANAGEMENT null 10.0 --signer emulator-account --network emulator
```

### 4. Tenant Deposits Rent
```bash
flow transactions send ./cadence/transactions/DepositFromSource.cdc 1 "tenant-rent" 1500.0 --signer tenant-account --network emulator
```

### 5. Preview Disbursement
```bash
flow scripts execute ./cadence/scripts/GetDisbursementPreview.cdc 1 --network emulator
```

**Output:**
```json
{
  "podBalance": 1500.0,
  "totalRequired": 1500.0,
  "isReady": true,
  "disbursements": [
    {"receiver": "0xLANDLORD", "amount": 1050.0, "ratio": 70.0},
    {"receiver": "0xUTILITIES", "amount": 300.0, "ratio": 20.0},
    {"receiver": "0xMANAGEMENT", "amount": 150.0, "ratio": 10.0}
  ]
}
```

### 6. Execute Disbursement
```bash
flow transactions send ./cadence/transactions/ExecutePodDisbursement.cdc 1 "manual" --signer emulator-account --network emulator
```

## Security Considerations

1. **Authorization**: Only pod creator/admin can execute disbursement
2. **Balance Validation**: Prevents overdraft scenarios
3. **Token Handling**: Uses secure FungibleToken interface
4. **Event Tracking**: All disbursements are logged on-chain
5. **Idempotence**: Safe for automated triggers

## Future Enhancements

- [ ] Pod-owned vaults (currently uses admin's vault)
- [ ] Partial disbursement support (if insufficient balance)
- [ ] Multi-token support (not just Flow)
- [ ] Disbursement history tracking
- [ ] Reward credits integration
- [ ] Conditional disbursement rules
- [ ] Sink priority levels

## Troubleshooting

### Error: "Insufficient pod balance"
**Solution:** Check balance with `GetDisbursementPreview.cdc`, ensure sufficient funds deposited

### Error: "Only authorized members can execute"
**Solution:** Execute with pod creator/admin account

### Error: "Could not borrow receiver reference"
**Solution:** Ensure sink addresses have FlowToken receiver capabilities set up

### Lint Errors in Editor
**Expected:** Contract imports show errors in editor but work when executed via Flow CLI

## Integration with Forte Actions

Future integration will enable:
- Pre-approved disbursement actions
- Secure multi-sig approvals
- Cross-tenant payment flows
- Automated reward distribution
- Escrow functionality

---

**Note:** This disbursement system is designed for modularity and future extension. The current implementation uses the pod admin's vault for token transfers. Production deployment should implement pod-owned vaults for full decentralization.
