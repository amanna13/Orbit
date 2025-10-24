# Flow Forte Flasher Integration Guide

## Overview
This guide explains how to set up automated scheduled payments using Flow Forte Flasher or any automation service.

## Architecture

### User Actions (On-Chain)
1. User creates scheduled payment via `RegisterScheduledPayment.cdc`
2. Payment details stored in Pod contract
3. Payment becomes visible to automation services

### Automation Service (Off-Chain - Flasher)
1. Monitors all scheduled payments via `GetAllScheduledPayments.cdc`
2. Checks which payments are due for execution
3. Calls `TriggerScheduledPayments.cdc` at regular intervals
4. Executes all due payments automatically

---

## User Workflow

### Step 1: User Registers a Scheduled Payment

**Command:**
```bash
flow transactions send ./cadence/transactions/RegisterScheduledPayment.cdc \
  1 \           # fromPodID
  2 \           # toPodID
  50.0 \        # amount
  "daily" \     # schedule description
  86400.0 \     # intervalSeconds (daily = 86400)
  --signer user-account \
  --network mainnet
```

**What happens:**
- Scheduled payment is created in the Pod contract
- Payment ID is returned
- User receives confirmation with automation instructions

### Step 2: User Funds Their Pod

```bash
flow transactions send ./cadence/transactions/FundPod.cdc \
  1 \           # podID
  1000.0 \      # amount
  --signer user-account \
  --network mainnet
```

---

## Flasher/Automation Setup

### Option A: Flow Forte Flasher (Recommended)

**Configuration File: `flasher-config.json`**
```json
{
  "name": "Pod Scheduled Payments Processor",
  "network": "mainnet",
  "interval": "60s",
  "transaction": {
    "path": "./cadence/transactions/TriggerScheduledPayments.cdc",
    "signer": "automation-service-account",
    "args": []
  },
  "monitoring": {
    "enabled": true,
    "alertOnFailure": true,
    "logLevel": "info"
  }
}
```

**Flasher Command:**
```bash
# Start Flasher automation
flasher start --config flasher-config.json
```

### Option B: Custom Automation Script

**Example: Node.js Cron Job**
```javascript
const cron = require('node-cron');
const { exec } = require('child_process');

// Run every minute
cron.schedule('* * * * *', () => {
  console.log('Checking for scheduled payments...');
  
  exec('flow transactions send ./cadence/transactions/TriggerScheduledPayments.cdc --signer service-account --network mainnet', 
    (error, stdout, stderr) => {
      if (error) {
        console.error(`Error: ${error.message}`);
        return;
      }
      console.log(`Output: ${stdout}`);
    }
  );
});
```

### Option C: GitHub Actions (Serverless)

**File: `.github/workflows/scheduled-payments.yml`**
```yaml
name: Process Scheduled Payments

on:
  schedule:
    - cron: '* * * * *'  # Every minute
  workflow_dispatch:     # Manual trigger

jobs:
  process-payments:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Setup Flow CLI
        run: |
          sh -ci "$(curl -fsSL https://storage.googleapis.com/flow-cli/install.sh)"
      
      - name: Execute Scheduled Payments
        env:
          FLOW_PRIVATE_KEY: ${{ secrets.FLOW_PRIVATE_KEY }}
        run: |
          flow transactions send ./cadence/transactions/TriggerScheduledPayments.cdc \
            --signer service-account \
            --network mainnet
```

---

## Monitoring & Viewing

### View All Scheduled Payments
```bash
flow scripts execute ./cadence/scripts/GetAllScheduledPayments.cdc --network mainnet
```

**Output:**
```
[
  {
    "fromPodID": 1,
    "fromPodName": "Marketing Pod",
    "paymentID": 1,
    "toPodID": 2,
    "amount": 50.0,
    "schedule": "daily",
    "nextExecutionAt": 1729800000.0,
    "isActive": true,
    "lastExecutedAt": null
  }
]
```

### View Pod Balances
```bash
flow scripts execute ./cadence/scripts/GetPodDetails.cdc 1 --network mainnet
```

---

## Testing Locally

### 1. Start Emulator
```bash
flow emulator start
```

### 2. Deploy Contracts
```bash
flow project deploy --network=emulator
```

### 3. Create Test Pods
```bash
flow transactions send ./cadence/transactions/CreatePod.cdc "TestPod1" "owner" --signer emulator-account --network emulator
flow transactions send ./cadence/transactions/CreatePod.cdc "TestPod2" "owner" --signer emulator-account --network emulator
```

### 4. Fund Source Pod
```bash
flow transactions send ./cadence/transactions/FundPod.cdc 1 1000.0 --signer emulator-account --network emulator
```

### 5. Register Scheduled Payment
```bash
flow transactions send ./cadence/transactions/RegisterScheduledPayment.cdc 1 2 50.0 "daily" 86400.0 --signer emulator-account --network emulator
```

### 6. Manual Test (Bypass Time Check)
```bash
flow transactions send ./cadence/transactions/ExecuteScheduledPaymentNow.cdc 1 0 --signer emulator-account --network emulator
```

### 7. Verify Balances
```bash
flow scripts execute ./cadence/scripts/GetPodDetails.cdc 1 --network emulator
flow scripts execute ./cadence/scripts/GetPodDetails.cdc 2 --network emulator
```

---

## Security Considerations

1. **Service Account**: The automation service needs its own account with gas fees funded
2. **Permissions**: TriggerScheduledPayments can be called by anyone - it's permissionless
3. **Validation**: All validations happen on-chain (balance checks, pod existence, etc.)
4. **Failed Payments**: Automatically skipped and logged, don't block other payments

---

## Production Deployment

### Prerequisites
- Flow account with sufficient balance for gas fees
- Access to Flow Forte Flasher or automation infrastructure
- Monitoring/alerting system for failures

### Steps
1. Deploy Pod contract to mainnet
2. Configure Flasher with service account credentials
3. Set execution interval (recommended: 60 seconds)
4. Enable monitoring and alerts
5. Test with small amounts first
6. Monitor logs for the first 24 hours

---

## Troubleshooting

**Payment not executing:**
- Check if `nextExecutionAt` time has passed
- Verify pod has sufficient balance
- Check if destination pod still exists

**Flasher not running:**
- Verify service account has gas fees
- Check network connectivity
- Review Flasher logs for errors

**Manual execution:**
Use `ExecuteScheduledPaymentNow.cdc` to bypass time checks for testing

---

## Summary

**User Side:**
- Users create scheduled payments via transactions
- Users fund their pods
- System automatically processes payments

**Admin/Automation Side:**
- Set up Flasher or cron job to call TriggerScheduledPayments
- Monitor execution logs
- Handle alerts for failures

No hardcoded schedules - everything is user-driven! 🚀
