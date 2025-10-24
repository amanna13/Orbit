import Pod from 0xf8d6e0586b0a20c7

// Test transaction to manually execute a specific scheduled payment
// For testing purposes - bypasses the time check
transaction(fromPodID: UInt64, paymentIndex: Int) {
    
    var executed: Bool
    var errorMessage: String?
    
    prepare(signer: &Account) {
        self.executed = false
        self.errorMessage = nil
        
        // Borrow the source pod
        if let fromPod = Pod.borrowPod(podID: fromPodID) {
            
            // Validate payment index
            if paymentIndex >= fromPod.scheduledPayments.length {
                self.errorMessage = "Payment index out of bounds"
                return
            }
            
            let payment = fromPod.scheduledPayments[paymentIndex]
            
            // Check if payment is active
            if !payment.isActive {
                self.errorMessage = "Payment is not active"
                return
            }
            
            // Check balance
            if fromPod.getPodBalance() < payment.amount {
                self.errorMessage = "Insufficient balance"
                return
            }
            
            // Borrow destination pod
            if let toPod = Pod.borrowPod(podID: payment.toPodID) {
                // Execute the payment
                fromPod.deductFromPodBalance(amount: payment.amount)
                toPod.addToPodBalance(amount: payment.amount)
                
                // Update execution timestamp
                let currentTimestamp = getCurrentBlock().timestamp
                fromPod.updateScheduledPayment(paymentIndex: paymentIndex, timestamp: currentTimestamp)
                
                self.executed = true
                
                log("💸 Manual execution: Pod #"
                    .concat(fromPodID.toString())
                    .concat(" sent ")
                    .concat(payment.amount.toString())
                    .concat(" to Pod #")
                    .concat(payment.toPodID.toString()))
            } else {
                self.errorMessage = "Destination pod not found"
            }
        } else {
            self.errorMessage = "Source pod not found"
        }
    }
    
    execute {
        if self.executed {
            log("✅ Payment executed successfully")
        } else {
            log("❌ Payment failed: ".concat(self.errorMessage ?? "Unknown error"))
        }
    }
}
