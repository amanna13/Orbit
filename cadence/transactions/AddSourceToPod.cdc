import Pod from 0xf8d6e0586b0a20c7

// Transaction to add an incoming Source to a Pod
// A Source represents an income stream (like rent, bill payments, etc.)
//
// Arguments:
//   - podID: The ID of the pod to add the source to
//   - sourceID: Unique identifier for this source
//   - sourceAddress: The address of the payer (Pod or user)
//   - rate: The amount (fixed) or percentage (ratio)
//   - isFixedRate: true = fixed amount, false = percentage-based
//   - scheduleID: Optional link to a scheduled transaction
transaction(
    podID: UInt64,
    sourceID: String,
    sourceAddress: Address,
    rate: UFix64,
    isFixedRate: Bool,
    scheduleID: String?
) {
    
    var signerAddress: Address
    var addedSourceID: String
    
    prepare(signer: &Account) {
        self.signerAddress = signer.address
        self.addedSourceID = sourceID
        
        // Borrow a reference to the Pod
        if let pod = Pod.borrowPod(podID: podID) {
            // Add the incoming source
            pod.addIncomingSource(
                sourceID: sourceID,
                sourceAddress: sourceAddress,
                rate: rate,
                isFixedRate: isFixedRate,
                scheduleID: scheduleID,
                caller: signer.address
            )
        } else {
            panic("Pod #".concat(podID.toString()).concat(" not found"))
        }
    }
    
    execute {
        log("✅ Source added successfully to Pod #".concat(podID.toString()))
        log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        log("📥 Incoming Source Details:")
        log("   Source ID: ".concat(self.addedSourceID))
        log("   Source Address: ".concat(sourceAddress.toString()))
        log("   Rate: ".concat(rate.toString()))
        
        if isFixedRate {
            log("   Type: Fixed Amount")
        } else {
            log("   Type: Percentage-based (Ratio)")
        }
        
        if let schedule = scheduleID {
            log("   Schedule ID: ".concat(schedule))
        } else {
            log("   Schedule ID: None (Manual payments)")
        }
        
        log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        log("This source is now tracked as an income stream for the pod")
    }
}
