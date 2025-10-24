import Pod from 0xf8d6e0586b0a20c7

// Transaction to distribute payments from a pod to all sinks
// Arguments:
//   - podID: The ID of the pod
transaction(podID: UInt64) {
    
    var distributions: {Address: UFix64}
    var mode: String
    
    prepare(signer: &Account) {
        self.distributions = {}
        self.mode = "unknown"
        
        // Borrow reference to the pod
        if let pod = Pod.borrowPod(podID: podID) {
            self.mode = pod.getPaymentMode()
            
            // Distribute payments
            self.distributions = pod.distributePayments(caller: signer.address)
        } else {
            panic("Pod #".concat(podID.toString()).concat(" not found"))
        }
    }
    
    execute {
        log("✅ Payment Distribution Complete")
        log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        log("Payment Mode: ".concat(self.mode))
        log("Total Recipients: ".concat(self.distributions.length.toString()))
        log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        
        for receiver in self.distributions.keys {
            let amount = self.distributions[receiver]!
            log("💰 ".concat(receiver.toString())
                .concat(" received ")
                .concat(amount.toString()))
        }
        
        if self.distributions.length == 0 {
            log("No payments were distributed (insufficient funds or no sinks configured)")
        }
    }
}
