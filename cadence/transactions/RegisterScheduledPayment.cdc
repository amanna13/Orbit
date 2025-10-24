import Pod from 0xf8d6e0586b0a20c7

// Transaction for users to register a scheduled payment that will be automated
// This creates the scheduled payment in the Pod contract
// External automation services (like Flasher) can then monitor and execute these
transaction(
    fromPodID: UInt64,
    toPodID: UInt64,
    amount: UFix64,
    schedule: String,  // e.g., "daily", "weekly", "0 0 * * *" (cron format)
    intervalSeconds: UFix64  // How often to execute (e.g., 86400.0 for daily)
) {
    
    var registered: Bool
    var paymentID: UInt64?
    
    prepare(signer: &Account) {
        self.registered = false
        self.paymentID = nil
        
        // Borrow reference to the source pod
        if let fromPod = Pod.borrowPod(podID: fromPodID) {
            
            // Verify signer is a member of the source pod
            if !fromPod.members.containsKey(signer.address) {
                panic("You must be a member of Pod #".concat(fromPodID.toString()).concat(" to create scheduled payments"))
            }
            
            // Verify destination pod exists
            if Pod.borrowPod(podID: toPodID) == nil {
                panic("Destination Pod #".concat(toPodID.toString()).concat(" does not exist"))
            }
            
            // Get the payment ID before creating it
            self.paymentID = fromPod.nextScheduledPaymentID
            
            // Create the scheduled payment
            fromPod.addScheduledPayment(
                toPodID: toPodID,
                amount: amount,
                schedule: schedule
            )
            
            self.registered = true
        } else {
            panic("Source Pod #".concat(fromPodID.toString()).concat(" not found"))
        }
    }
    
    execute {
        if self.registered {
            log("✅ Scheduled Payment Registered Successfully!")
            log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            log("📋 Payment Details:")
            log("   Payment ID: ".concat(self.paymentID!.toString()))
            log("   From Pod: #".concat(fromPodID.toString()))
            log("   To Pod: #".concat(toPodID.toString()))
            log("   Amount: ".concat(amount.toString()))
            log("   Schedule: ".concat(schedule))
            log("   Interval: Every ".concat(intervalSeconds.toString()).concat(" seconds"))
            log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            log("🤖 Automation Instructions:")
            log("   This payment will be executed automatically by Flow Forte Flasher")
            log("   or any automation service monitoring scheduled payments.")
            log("   ")
            log("   To execute manually, run:")
            log("   flow transactions send ./cadence/transactions/ExecuteScheduledPaymentNow.cdc "
                .concat(fromPodID.toString())
                .concat(" ")
                .concat(self.paymentID!.toString()))
            log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        }
    }
}
