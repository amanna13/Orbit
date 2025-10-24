import Pod from 0xf8d6e0586b0a20c7
import FungibleToken from 0xee82856bf20e2aa6
import FlowToken from 0x0ae53cb6e3f42a79

// Transaction to execute automated payouts from a Pod to its Sinks
// Can be triggered manually by admin or automatically via Flasher
//
// Arguments:
//   - podID: The ID of the pod executing disbursement
//   - triggerType: "manual" or "flasher" to indicate how it was triggered
//
// Flow:
//   1. Borrow the Pod reference
//   2. Verify authorization
//   3. Calculate amounts for each sink based on payment mode (fixed/ratio)
//   4. Transfer Flow tokens from pod to each sink
//   5. Emit PodDisbursement events
//   6. Update pod balances
transaction(podID: UInt64, triggerType: String) {
    
    // Reference to the Pod
    let podRef: &Pod.PodResource?
    
    // Store caller address
    let callerAddress: Address
    
    // Store disbursement details
    var disbursements: [{String: AnyStruct}]
    
    prepare(signer: auth(BorrowValue) &Account) {
        self.callerAddress = signer.address
        self.disbursements = []
        
        // Validate trigger type
        if triggerType != "manual" && triggerType != "flasher" {
            panic("Invalid trigger type. Must be 'manual' or 'flasher'")
        }
        
        // Borrow reference to the Pod
        self.podRef = Pod.borrowPod(podID: podID)
        
        if self.podRef == nil {
            panic("Pod #".concat(podID.toString()).concat(" not found"))
        }
        
        let pod = self.podRef!
        
        // Execute disbursement calculation and get details
        self.disbursements = pod.executeDisbursement(
            caller: self.callerAddress,
            triggerType: triggerType
        )
        
        // Now perform the actual Flow token transfers
        for disbursement in self.disbursements {
            let receiverAddress = disbursement["receiver"]! as! Address
            let amount = disbursement["amount"]! as! UFix64
            
            // Get receiver's FlowToken Receiver capability
            let receiverRef = getAccount(receiverAddress)
                .capabilities.get<&{FungibleToken.Receiver}>(/public/flowTokenReceiver)
                .borrow()
                ?? panic("Could not borrow receiver reference for ".concat(receiverAddress.toString()))
            
            // For now, we withdraw from signer's vault (Pod admin)
            // In production, the Pod would have its own vault
            let payerVault = signer.storage.borrow<auth(FungibleToken.Withdraw) &FlowToken.Vault>(
                from: /storage/flowTokenVault
            ) ?? panic("Could not borrow payer's FlowToken Vault")
            
            // Verify sufficient balance
            if payerVault.balance < amount {
                panic("Insufficient Flow tokens. Available: ".concat(payerVault.balance.toString())
                    .concat(", Required: ")
                    .concat(amount.toString()))
            }
            
            // Withdraw and deposit tokens
            let tokens <- payerVault.withdraw(amount: amount)
            receiverRef.deposit(from: <-tokens)
        }
    }
    
    execute {
        log("✅ Pod Disbursement Executed Successfully")
        log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        log("💸 Disbursement Details:")
        log("   Pod ID: #".concat(podID.toString()))
        log("   Trigger Type: ".concat(triggerType))
        log("   Payment Mode: ".concat(self.podRef!.getPaymentMode()))
        log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        
        var totalDisbursed: UFix64 = 0.0
        var sinkCount = 0
        
        for disbursement in self.disbursements {
            let receiverAddress = disbursement["receiver"]! as! Address
            let amount = disbursement["amount"]! as! UFix64
            
            sinkCount = sinkCount + 1
            totalDisbursed = totalDisbursed + amount
            
            log("   Sink #".concat(sinkCount.toString())
                .concat(": ")
                .concat(receiverAddress.toString())
                .concat(" → ")
                .concat(amount.toString())
                .concat(" FLOW"))
        }
        
        log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        log("📊 Total Disbursed: ".concat(totalDisbursed.toString()).concat(" FLOW"))
        log("📊 Number of Sinks: ".concat(sinkCount.toString()))
        
        if let pod = self.podRef {
            log("📊 Remaining Pod Balance: ".concat(pod.getPodBalance().toString()).concat(" FLOW"))
            log("📊 Remaining Total Balance: ".concat(pod.getTotalBalance().toString()).concat(" FLOW"))
        }
        
        log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        
        if triggerType == "flasher" {
            log("🤖 This disbursement was triggered automatically by Flasher")
            log("ℹ️  Ensure idempotence checks are in place for scheduled triggers")
        } else {
            log("👤 This disbursement was triggered manually by: ".concat(self.callerAddress.toString()))
        }
        
        log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    }
}
