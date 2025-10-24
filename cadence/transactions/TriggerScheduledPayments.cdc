import Pod from 0xf8d6e0586b0a20c7

// Transaction to trigger scheduled payments across all pods
// This can be executed by Flow Forte Flasher or any automation service
transaction() {
    
    // Variables to track execution results
    var successCount: Int
    var failureCount: Int
    var skippedCount: Int
    var executionLogs: [String]
    
    prepare(signer: &Account) {
        // Initialize counters
        self.successCount = 0
        self.failureCount = 0
        self.skippedCount = 0
        self.executionLogs = []
        
        // Get current block timestamp
        let currentTimestamp = getCurrentBlock().timestamp
        
        // Iterate through all pods
        for fromPodID in Pod.pods.keys {
            // Borrow reference to the source pod
            if let fromPod = Pod.borrowPod(podID: fromPodID) {
                
                // Check each scheduled payment in this pod
                var paymentIndex = 0
                while paymentIndex < fromPod.scheduledPayments.length {
                    let payment = fromPod.scheduledPayments[paymentIndex]
                    
                    // Check if payment is active and due for execution
                    if payment.isActive && currentTimestamp >= payment.nextExecutionAt {
                        
                        // Check if source pod has sufficient balance
                        if fromPod.getPodBalance() < payment.amount {
                            // Insufficient funds - skip and log warning
                            let warningMsg = "⚠️ Skipped: Pod #"
                                .concat(fromPodID.toString())
                                .concat(" has insufficient balance (")
                                .concat(fromPod.getPodBalance().toString())
                                .concat(") for payment of ")
                                .concat(payment.amount.toString())
                                .concat(" to Pod #")
                                .concat(payment.toPodID.toString())
                            
                            self.executionLogs.append(warningMsg)
                            self.skippedCount = self.skippedCount + 1
                            paymentIndex = paymentIndex + 1
                            continue
                        }
                        
                        // Borrow reference to destination pod
                        if let toPod = Pod.borrowPod(podID: payment.toPodID) {
                            // Execute the payment
                            fromPod.deductFromPodBalance(amount: payment.amount)
                            toPod.addToPodBalance(amount: payment.amount)
                            
                            // Update the scheduled payment execution timestamp
                            fromPod.updateScheduledPayment(paymentIndex: paymentIndex, timestamp: currentTimestamp)
                            
                            // Log success
                            let successMsg = "💸 Auto-payment: Pod #"
                                .concat(fromPodID.toString())
                                .concat(" sent ")
                                .concat(payment.amount.toString())
                                .concat(" to Pod #")
                                .concat(payment.toPodID.toString())
                                .concat(" at ")
                                .concat(currentTimestamp.toString())
                                .concat(" (Schedule: ")
                                .concat(payment.schedule)
                                .concat(")")
                            
                            self.executionLogs.append(successMsg)
                            self.successCount = self.successCount + 1
                        } else {
                            // Destination pod not found - deactivate payment and log error
                            fromPod.cancelScheduledPayment(paymentIndex: paymentIndex)
                            
                            let errorMsg = "❌ Error: Destination Pod #"
                                .concat(payment.toPodID.toString())
                                .concat(" not found. Payment deactivated.")
                            
                            self.executionLogs.append(errorMsg)
                            self.failureCount = self.failureCount + 1
                        }
                    }
                    
                    paymentIndex = paymentIndex + 1
                }
            }
        }
    }
    
    execute {
        // Log execution summary
        log("========================================")
        log("🤖 Scheduled Payments Execution Report")
        log("========================================")
        log("Timestamp: ".concat(getCurrentBlock().timestamp.toString()))
        log("✅ Successful Payments: ".concat(self.successCount.toString()))
        log("⚠️ Skipped (Insufficient Funds): ".concat(self.skippedCount.toString()))
        log("❌ Failed (Pod Not Found): ".concat(self.failureCount.toString()))
        log("========================================")
        
        // Log individual payment details
        if self.executionLogs.length > 0 {
            log("📋 Payment Details:")
            var i = 0
            while i < self.executionLogs.length {
                log(self.executionLogs[i])
                i = i + 1
            }
        } else {
            log("📭 No payments were due for execution")
        }
        
        log("========================================")
    }
}
