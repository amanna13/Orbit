# Quick Test: Pod Disbursement System

## Prerequisites
```bash
# Ensure Pod contract is updated with disbursement function
flow accounts update-contract Pod ./cadence/contracts/Pod.cdc --signer emulator-account --network emulator
```

## Test Scenario: Simple 2-Sink Fixed Distribution

### Step 1: Create Pod
```bash
flow transactions send ./cadence/transactions/CreatePod.cdc "Test Disbursement Pod" "creator" --signer emulator-account --network emulator
```
**Expected:** Pod created with ID 1 (or next available)

### Step 2: Set Fixed Payment Mode (default, but explicit)
```bash
flow transactions send ./cadence/transactions/SetPaymentMode.cdc 1 "fixed" --signer emulator-account --network emulator
```

### Step 3: Add Two Sinks
```bash
# Sink 1: Gets 100 FLOW
flow transactions send ./cadence/transactions/AddSink.cdc 1 0x01cf0e2f2f715450 100.0 null --signer emulator-account --network emulator

# Sink 2: Gets 50 FLOW
flow transactions send ./cadence/transactions/AddSink.cdc 1 0x179b6b1cb6755e31 50.0 null --signer emulator-account --network emulator
```
**Expected:** Two sinks added (total required: 150 FLOW)

### Step 4: Add Income Source
```bash
flow transactions send ./cadence/transactions/AddSourceToPod.cdc 1 "test-source" 0xf8d6e0586b0a20c7 500.0 true null --signer emulator-account --network emulator
```

### Step 5: Fund Pod via Source Deposit
```bash
flow transactions send ./cadence/transactions/DepositFromSource.cdc 1 "test-source" 200.0 --signer emulator-account --network emulator
```
**Expected:** Pod balance now 200 FLOW

### Step 6: Preview Disbursement
```bash
flow scripts execute ./cadence/scripts/GetDisbursementPreview.cdc 1 --network emulator
```
**Expected Output:**
```json
{
  "podID": 1,
  "paymentMode": "fixed",
  "podBalance": 200.0,
  "totalRequired": 150.0,
  "isReady": true,
  "shortage": 0.0,
  "sinkCount": 2,
  "disbursements": [
    {"receiver": "0x01cf0e2f2f715450", "amount": 100.0, "type": "fixed"},
    {"receiver": "0x179b6b1cb6755e31", "amount": 50.0, "type": "fixed"}
  ],
  "remainingAfter": 50.0
}
```

### Step 7: Execute Disbursement (Manual)
```bash
flow transactions send ./cadence/transactions/ExecutePodDisbursement.cdc 1 "manual" --signer emulator-account --network emulator
```

**Expected Output:**
```
✅ Pod Disbursement Executed Successfully
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
💸 Disbursement Details:
   Pod ID: #1
   Trigger Type: manual
   Payment Mode: fixed
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   Sink #1: 0x01cf0e2f2f715450 → 100.0 FLOW
   Sink #2: 0x179b6b1cb6755e31 → 50.0 FLOW
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📊 Total Disbursed: 150.0 FLOW
📊 Number of Sinks: 2
📊 Remaining Pod Balance: 50.0 FLOW
```

### Step 8: View Events
```bash
flow events get Pod.PodDisbursement --network emulator
```

**Expected:** 2 PodDisbursement events (one per sink)

### Step 9: Verify Final State
```bash
flow scripts execute ./cadence/scripts/GetPodDetails.cdc 1 --network emulator
```

**Expected:** Pod balance = 50.0 FLOW (200 - 150)

---

## Test Scenario: Ratio-Based Distribution

### Setup
```bash
# Create pod
flow transactions send ./cadence/transactions/CreatePod.cdc "Ratio Test Pod" "creator" --signer emulator-account --network emulator

# Set ratio mode (assuming pod ID 2)
flow transactions send ./cadence/transactions/SetPaymentMode.cdc 2 "ratio" --signer emulator-account --network emulator

# Add sinks with percentages
flow transactions send ./cadence/transactions/AddSink.cdc 2 0x01cf0e2f2f715450 null 60.0 --signer emulator-account --network emulator
flow transactions send ./cadence/transactions/AddSink.cdc 2 0x179b6b1cb6755e31 null 40.0 --signer emulator-account --network emulator

# Add source
flow transactions send ./cadence/transactions/AddSourceToPod.cdc 2 "ratio-source" 0xf8d6e0586b0a20c7 1000.0 true null --signer emulator-account --network emulator

# Deposit
flow transactions send ./cadence/transactions/DepositFromSource.cdc 2 "ratio-source" 1000.0 --signer emulator-account --network emulator
```

### Preview
```bash
flow scripts execute ./cadence/scripts/GetDisbursementPreview.cdc 2 --network emulator
```

**Expected:**
- Sink 1: 600 FLOW (60%)
- Sink 2: 400 FLOW (40%)
- Total: 1000 FLOW
- Remaining: 0 FLOW

### Execute
```bash
flow transactions send ./cadence/transactions/ExecutePodDisbursement.cdc 2 "manual" --signer emulator-account --network emulator
```

---

## Test Scenario: Insufficient Balance

### Setup
```bash
# Create pod (assuming ID 3)
flow transactions send ./cadence/transactions/CreatePod.cdc "Insufficient Test Pod" "creator" --signer emulator-account --network emulator

# Add sinks requiring 500 FLOW total
flow transactions send ./cadence/transactions/AddSink.cdc 3 0x01cf0e2f2f715450 300.0 null --signer emulator-account --network emulator
flow transactions send ./cadence/transactions/AddSink.cdc 3 0x179b6b1cb6755e31 200.0 null --signer emulator-account --network emulator

# Add source
flow transactions send ./cadence/transactions/AddSourceToPod.cdc 3 "short-source" 0xf8d6e0586b0a20c7 100.0 true null --signer emulator-account --network emulator

# Deposit only 100 FLOW (insufficient)
flow transactions send ./cadence/transactions/DepositFromSource.cdc 3 "short-source" 100.0 --signer emulator-account --network emulator
```

### Preview
```bash
flow scripts execute ./cadence/scripts/GetDisbursementPreview.cdc 3 --network emulator
```

**Expected:**
```json
{
  "isReady": false,
  "shortage": 400.0,
  "totalRequired": 500.0,
  "podBalance": 100.0
}
```

### Try Execute (Should Fail)
```bash
flow transactions send ./cadence/transactions/ExecutePodDisbursement.cdc 3 "manual" --signer emulator-account --network emulator
```

**Expected Error:** "Insufficient pod balance for total disbursement"

---

## Test Scenario: Flasher Trigger

### Setup & Execute
```bash
# Use existing funded pod (e.g., pod 1 after refunding)
flow transactions send ./cadence/transactions/DepositFromSource.cdc 1 "test-source" 200.0 --signer emulator-account --network emulator

# Execute with flasher trigger type
flow transactions send ./cadence/transactions/ExecutePodDisbursement.cdc 1 "flasher" --signer emulator-account --network emulator
```

**Expected Output Includes:**
```
🤖 This disbursement was triggered automatically by Flasher
ℹ️  Ensure idempotence checks are in place for scheduled triggers
```

---

## Verification Commands

### Check Pod Details
```bash
flow scripts execute ./cadence/scripts/GetPodDetails.cdc <podID> --network emulator
```

### Check Source-Sink State
```bash
flow scripts execute ./cadence/scripts/GetSourceSinkDetails.cdc <podID> --network emulator
```

### Check All Pods
```bash
flow scripts execute ./cadence/scripts/GetAllPods.cdc --network emulator
```

### Query Events
```bash
# All disbursement events
flow events get Pod.PodDisbursement --network emulator

# All sink additions
flow events get Pod.SinkAdded --network emulator

# All source deposits
flow events get Pod.SourceDeposit --network emulator
```

---

## Common Issues

### Issue: "Pod not found"
**Solution:** Check pod ID with `GetAllPods.cdc`

### Issue: "Only authorized members can execute"
**Solution:** Use `--signer emulator-account` (pod creator)

### Issue: Contract changes not reflecting
**Solution:** Redeploy contract:
```bash
flow accounts update-contract Pod ./cadence/contracts/Pod.cdc --signer emulator-account --network emulator
```

### Issue: Lint errors in VSCode
**Solution:** Ignore - contracts work when executed via CLI

---

## Success Criteria

✅ Pod created successfully  
✅ Sinks added (fixed or ratio mode)  
✅ Source deposits increase pod balance  
✅ Preview shows correct calculations  
✅ Disbursement executes without errors  
✅ Flow tokens transferred to sink addresses  
✅ Events emitted correctly  
✅ Pod balance updated (reduced by total disbursed)  
✅ Both trigger types work (manual & flasher)

---

**Next Steps:**
1. Test with real Flow accounts (not emulator)
2. Integrate with Flasher for automation
3. Implement pod-owned vaults
4. Add multi-token support
