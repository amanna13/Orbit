import Pod from 0xf8d6e0586b0a20c7

// Transaction to add a payment sink to a pod
// Arguments:
//   - podID: The ID of the pod
//   - receiver: The address that will receive payments
//   - amount: Fixed amount (for "fixed" mode) - optional
//   - ratio: Percentage ratio (for "ratio" mode) - optional
transaction(podID: UInt64, receiver: Address, amount: UFix64?, ratio: UFix64?) {
    
    var signerAddress: Address
    var mode: String?
    
    prepare(signer: &Account) {
        self.signerAddress = signer.address
        self.mode = nil
        
        // Borrow reference to the pod
        if let pod = Pod.borrowPod(podID: podID) {
            self.mode = pod.getPaymentMode()
            
            // Add the sink
            pod.addSink(receiver: receiver, amount: amount, ratio: ratio, caller: signer.address)
        } else {
            panic("Pod #".concat(podID.toString()).concat(" not found"))
        }
    }
    
    execute {
        log("✅ Payment sink added successfully")
        log("Receiver: ".concat(receiver.toString()))
        
        if let currentMode = self.mode {
            if currentMode == "fixed" && amount != nil {
                log("Mode: Fixed")
                log("Amount: ".concat(amount!.toString()))
            } else if currentMode == "ratio" && ratio != nil {
                log("Mode: Ratio")
                log("Percentage: ".concat(ratio!.toString()).concat("%"))
            }
        }
    }
}
