# Flasher Configuration: Monthly Pod Distribution

## Overview
This configuration sets up an automated monthly distribution that runs every 30 days, distributing pod funds equally among all members.

## Flasher Action Configuration

### Action Name Format
```
MonthlyDistribution_<podID>
```

**Examples:**
- `MonthlyDistribution_1` (for Pod ID 1)
- `MonthlyDistribution_2` (for Pod ID 2)
- `MonthlyDistribution_5` (for Pod ID 5)

## Configuration Template (JSON)

```json
{
  "action_name": "MonthlyDistribution_1",
  "description": "Automatic monthly equal distribution of pod funds to all members",
  "trigger": {
    "type": "time_based",
    "schedule": {
      "interval": "monthly",
      "day_of_month": 1,
      "time": "00:00:00",
      "timezone": "UTC"
    },
    "alternative_cron": "0 0 1 * *"
  },
  "transaction": {
    "cadence_file": "DistributePodFundsToMembers.cdc",
    "network": "mainnet",
    "signer": "pod_admin_account",
    "arguments": [
      {
        "type": "UInt64",
        "value": 1
      }
    ],
    "gas_limit": 1000
  },
  "conditions": {
    "min_balance_required": 0.0,
    "check_member_count": true,
    "min_members": 1
  },
  "notifications": {
    "on_success": true,
    "on_failure": true,
    "email": "admin@example.com",
    "webhook": "https://api.example.com/webhooks/distribution"
  },
  "retry_policy": {
    "max_retries": 3,
    "retry_delay_seconds": 3600
  }
}
```

## Alternative: Every 30 Days (Not Calendar Month)

If you want exactly every 30 days (not on 1st of month):

```json
{
  "trigger": {
    "type": "interval_based",
    "interval_seconds": 2592000,
    "start_date": "2025-11-01T00:00:00Z"
  }
}
```

**Note:** 2,592,000 seconds = 30 days

## Setup Instructions

### Step 1: Install Forte Flasher CLI

```bash
# Install Flasher (if not already installed)
npm install -g @forte/flasher-cli

# Or via pip
pip install forte-flasher
```

### Step 2: Configure Flasher Authentication

```bash
flasher auth login
# Follow prompts to authenticate with your Flow account
```

### Step 3: Create the Scheduled Action

**For Pod ID 1:**
```bash
flasher action create \
  --name "MonthlyDistribution_1" \
  --description "Monthly equal distribution for Pod #1" \
  --transaction "./cadence/transactions/DistributePodFundsToMembers.cdc" \
  --args "1" \
  --schedule "0 0 1 * *" \
  --network "mainnet" \
  --signer "emulator-account"
```

**For Pod ID 2:**
```bash
flasher action create \
  --name "MonthlyDistribution_2" \
  --description "Monthly equal distribution for Pod #2" \
  --transaction "./cadence/transactions/DistributePodFundsToMembers.cdc" \
  --args "2" \
  --schedule "0 0 1 * *" \
  --network "mainnet" \
  --signer "emulator-account"
```

### Step 4: Verify Action is Scheduled

```bash
flasher action list
flasher action show MonthlyDistribution_1
```

### Step 5: Test Manually Before Automation

```bash
# Test the transaction manually first
flow transactions send ./cadence/transactions/DistributePodFundsToMembers.cdc 1 \
  --signer emulator-account \
  --network emulator

# If successful, enable Flasher automation
flasher action enable MonthlyDistribution_1
```

## Cron Schedule Examples

### Monthly on 1st at Midnight
```
0 0 1 * *
```

### Every 30 Days (Alternative)
```
0 0 */30 * *
```
**Note:** This is approximate; use interval_based for exact 30-day intervals

### 15th of Every Month at Noon
```
0 12 15 * *
```

### Last Day of Month
```
0 0 L * *
```

### Quarterly (Every 3 Months, 1st at Midnight)
```
0 0 1 */3 *
```

## Multiple Pods Setup Script

```bash
#!/bin/bash
# setup_monthly_distributions.sh

# Array of pod IDs to configure
POD_IDS=(1 2 3 4 5)

for POD_ID in "${POD_IDS[@]}"
do
  echo "Setting up MonthlyDistribution_$POD_ID..."
  
  flasher action create \
    --name "MonthlyDistribution_$POD_ID" \
    --description "Monthly equal distribution for Pod #$POD_ID" \
    --transaction "./cadence/transactions/DistributePodFundsToMembers.cdc" \
    --args "$POD_ID" \
    --schedule "0 0 1 * *" \
    --network "mainnet" \
    --signer "emulator-account"
  
  echo "✅ MonthlyDistribution_$POD_ID configured"
  echo "---"
done

echo "✅ All monthly distributions configured!"
```

**Usage:**
```bash
chmod +x setup_monthly_distributions.sh
./setup_monthly_distributions.sh
```

## Monitoring & Management

### View Action Status
```bash
flasher action status MonthlyDistribution_1
```

### View Execution History
```bash
flasher action history MonthlyDistribution_1 --limit 10
```

### Pause Action
```bash
flasher action pause MonthlyDistribution_1
```

### Resume Action
```bash
flasher action resume MonthlyDistribution_1
```

### Delete Action
```bash
flasher action delete MonthlyDistribution_1
```

## Testing Workflow

### 1. Test with Dry Run
```bash
flasher action test MonthlyDistribution_1 --dry-run
```

### 2. Test Actual Execution (Manual Trigger)
```bash
flasher action execute MonthlyDistribution_1
```

### 3. Monitor First Scheduled Run
```bash
# Check logs after first scheduled execution
flasher action logs MonthlyDistribution_1 --tail
```

## Safety Considerations

### Pre-Conditions
- ✅ Pod must exist
- ✅ Pod must have members
- ✅ Pod must have balance > 0
- ✅ Signer must have authorization

### Idempotence
The transaction is idempotent by design:
- Only distributes if balance exists
- Safe to retry on failure
- No double-distribution risk

### Error Handling
If distribution fails:
1. Flasher will retry (based on retry_policy)
2. Notification sent to admin
3. Balance remains in pod (no partial distributions)
4. Next scheduled run will attempt again

## Integration with Pod Events

### Subscribe to Distribution Events
```bash
# Monitor pod distribution events
flow events get Pod.PodDisbursement --network mainnet

# Or set up webhook in Flasher config
```

## Advanced Configuration

### Conditional Distribution (Only if Balance > Threshold)

```json
{
  "conditions": {
    "script": "GetPodDetails.cdc",
    "script_args": [1],
    "evaluate": "result.podBalance >= 1000.0"
  }
}
```

### Multi-Step Action (Distribute + Notify)

```json
{
  "steps": [
    {
      "name": "distribute",
      "transaction": "DistributePodFundsToMembers.cdc",
      "args": [1]
    },
    {
      "name": "notify_members",
      "webhook": "https://api.example.com/notify-distribution",
      "method": "POST"
    }
  ]
}
```

## Troubleshooting

### Issue: "Pod has no funds to distribute"
**Solution:** Ensure pod receives deposits before distribution runs. Consider adding balance threshold condition.

### Issue: "Pod has no members"
**Solution:** Verify pod configuration, ensure members were added via JoinPod transaction.

### Issue: Flasher action not executing
**Solution:** 
```bash
# Check action status
flasher action status MonthlyDistribution_1

# Verify schedule
flasher action show MonthlyDistribution_1

# Check logs
flasher action logs MonthlyDistribution_1
```

### Issue: Gas limit exceeded
**Solution:** Increase gas_limit in configuration:
```json
{
  "transaction": {
    "gas_limit": 9999
  }
}
```

## Cost Estimation

### Transaction Costs (Mainnet)
- Gas per distribution: ~0.0001 FLOW per member
- Example: 5 members = ~0.0005 FLOW per run
- Monthly cost: ~0.0005 FLOW
- Annual cost: ~0.006 FLOW

### Flasher Service Fees
Consult Forte documentation for Flasher service pricing.

## Example: Complete Setup for Rental Pod

```bash
# 1. Create pod with members
flow transactions send ./cadence/transactions/CreatePod.cdc "Rental Pod" "creator" --signer emulator-account

# 2. Add members
flow transactions send ./cadence/transactions/JoinPod.cdc 1 "ABC123XY" --signer member1
flow transactions send ./cadence/transactions/JoinPod.cdc 1 "ABC123XY" --signer member2

# 3. Setup income source (tenant rent)
flow transactions send ./cadence/transactions/AddSourceToPod.cdc 1 "tenant-rent" 0xTENANT 1500.0 true "monthly" --signer emulator-account

# 4. Schedule monthly distribution
flasher action create \
  --name "MonthlyDistribution_1" \
  --transaction "./cadence/transactions/DistributePodFundsToMembers.cdc" \
  --args "1" \
  --schedule "0 0 5 * *" \
  --network "mainnet"

# 5. Enable action
flasher action enable MonthlyDistribution_1
```

**Result:** Every 5th of the month, rental income is automatically distributed equally among all pod members.

---

## Next Steps

1. ✅ Test transaction manually
2. ✅ Configure Flasher action
3. ✅ Test with dry-run
4. ✅ Enable automation
5. ✅ Monitor first execution
6. ✅ Set up notifications

**Status:** Ready for production deployment with Flasher automation! 🚀
