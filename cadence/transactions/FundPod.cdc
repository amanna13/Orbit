import Pod from 0xf8d6e0586b0a20c7

// Transaction to add funds to a pod's balance pool
// Arguments:
//   - podID: The ID of the pod to fund
//   - amount: The amount to add to the pod balance
transaction(podID: UInt64, amount: UFix64) {
    
    // Variables to store transaction data
    var signerAddress: Address
    
    prepare(signer: &Account) {
        // Initialize variables
        self.signerAddress = signer.address
        
        // Borrow a reference to the pod
        if let pod = Pod.borrowPod(podID: podID) {
            // Check if the signer is a member of the pod
            if !pod.members.containsKey(signer.address) {
                panic("You are not a member of Pod #".concat(podID.toString()))
            }
            
            // Add funds to the pod's balance
            pod.addToPodBalance(amount: amount)
        } else {
            panic("Pod #".concat(podID.toString()).concat(" not found"))
        }
    }
    
    execute {
        // Log confirmation message
        log("✅ Added ".concat(amount.toString())
            .concat(" to Pod #")
            .concat(podID.toString())
            .concat(" balance pool from ")
            .concat(self.signerAddress.toString()))
    }
}
