import Pod from 0xf8d6e0586b0a20c7

// Transaction to pay from one pod to another pod
// Arguments:
//   - fromPodID: The ID of the paying pod
//   - toPodID: The ID of the receiving pod
//   - amount: The amount to transfer
//   - schedule: "manual" for immediate payment, or a cron-like string for scheduled payment
transaction(fromPodID: UInt64, toPodID: UInt64, amount: UFix64, schedule: String?) {
    
    // Variables to store transaction data
    var fromPodRef: &Pod.PodResource?
    var toPodRef: &Pod.PodResource?
    var signerAddress: Address
    var actualSchedule: String
    
    prepare(signer: &Account) {
        // Initialize variables
        self.signerAddress = signer.address
        self.fromPodRef = nil
        self.toPodRef = nil
        self.actualSchedule = schedule ?? "manual"
        
        // Borrow reference to the source pod (fromPod)
        if let fromPod = Pod.borrowPod(podID: fromPodID) {
            self.fromPodRef = fromPod
            
            // Verify that signer is a member of fromPod
            if !fromPod.members.containsKey(signer.address) {
                panic("You are not a member of Pod #".concat(fromPodID.toString()))
            }
            
            // Check if fromPod has sufficient balance
            if fromPod.getPodBalance() < amount {
                panic("Insufficient balance in Pod #".concat(fromPodID.toString())
                    .concat(". Available: ")
                    .concat(fromPod.getPodBalance().toString())
                    .concat(", Required: ")
                    .concat(amount.toString()))
            }
            
            // Deduct amount from fromPod balance
            fromPod.deductFromPodBalance(amount: amount)
        } else {
            panic("Source Pod #".concat(fromPodID.toString()).concat(" not found"))
        }
        
        // Borrow reference to the destination pod (toPod)
        if let toPod = Pod.borrowPod(podID: toPodID) {
            self.toPodRef = toPod
            
            // Check the schedule type
            if self.actualSchedule == "manual" {
                // Manual/immediate payment - add funds to toPod balance
                toPod.addToPodBalance(amount: amount)
            } else {
                // Scheduled/auto payment - create a scheduled payment record
                if let fromPod = self.fromPodRef {
                    fromPod.addScheduledPayment(
                        toPodID: toPodID,
                        amount: amount,
                        schedule: self.actualSchedule
                    )
                }
            }
        } else {
            // If toPod doesn't exist, refund the amount to fromPod
            if let fromPod = self.fromPodRef {
                fromPod.addToPodBalance(amount: amount)
            }
            panic("Destination Pod #".concat(toPodID.toString()).concat(" not found"))
        }
    }
    
    execute {
        // Log confirmation message
        let scheduleType = self.actualSchedule == "manual" ? "immediate" : "scheduled (".concat(self.actualSchedule).concat(")")
        
        log("✅ Pod #".concat(fromPodID.toString())
            .concat(" paid ")
            .concat(amount.toString())
            .concat(" to Pod #")
            .concat(toPodID.toString())
            .concat(" (")
            .concat(scheduleType)
            .concat(")"))
        
        if self.actualSchedule == "manual" {
            log("Payment completed immediately")
        } else {
            log("Payment scheduled for automatic execution")
        }
    }
}
