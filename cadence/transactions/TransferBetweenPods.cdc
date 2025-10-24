import Pod from 0xf8d6e0586b0a20c7

// Transaction to transfer funds between two pods
// Transfers amount from sender pod's balance to receiver pod's balance
//
// Arguments:
//   - senderPodID: The ID of the pod sending funds
//   - receiverPodID: The ID of the pod receiving funds
//   - amount: The amount to transfer
transaction(senderPodID: UInt64, receiverPodID: UInt64, amount: UFix64) {
    
    // References to both pods
    let senderPod: &Pod.PodResource
    let receiverPod: &Pod.PodResource
    
    prepare(signer: auth(BorrowValue) &Account) {
        // Borrow reference to the sender pod
        self.senderPod = Pod.borrowPod(podID: senderPodID)
            ?? panic("Sender pod #".concat(senderPodID.toString()).concat(" not found"))
        
        // Borrow reference to the receiver pod
        self.receiverPod = Pod.borrowPod(podID: receiverPodID)
            ?? panic("Receiver pod #".concat(receiverPodID.toString()).concat(" not found"))
        
        // Check if sender pod has sufficient balance
        let senderBalance = self.senderPod.getPodBalance()
        if senderBalance < amount {
            panic("Insufficient funds in sender pod. Available: "
                .concat(senderBalance.toString())
                .concat(", Required: ")
                .concat(amount.toString()))
        }
        
        // Deduct amount from sender pod
        self.senderPod.deductFromPodBalance(amount: amount)
        
        // Add amount to receiver pod
        self.receiverPod.addToPodBalance(amount: amount)
    }
    
    execute {
        log("✅ Pod-to-Pod Transfer Successful")
        log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        log("💸 Transfer Details:")
        log("   From Pod: #".concat(senderPodID.toString()))
        log("   To Pod: #".concat(receiverPodID.toString()))
        log("   Amount: ".concat(amount.toString()).concat(" FLOW"))
        log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        log("📊 Sender Pod New Balance: ".concat(self.senderPod.getPodBalance().toString()).concat(" FLOW"))
        log("📊 Receiver Pod New Balance: ".concat(self.receiverPod.getPodBalance().toString()).concat(" FLOW"))
        log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    }
}
