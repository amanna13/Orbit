import Pod from 0xf8d6e0586b0a20c7

// Transaction to deposit funds into a pod using the Source-Sink model
// Arguments:
//   - podID: The ID of the pod to deposit into
//   - amount: The amount to deposit
transaction(podID: UInt64, amount: UFix64) {
    
    var signerAddress: Address
    
    prepare(signer: &Account) {
        self.signerAddress = signer.address
        
        // Borrow reference to the pod
        if let pod = Pod.borrowPod(podID: podID) {
            // Check if signer is a member
            if !pod.members.containsKey(signer.address) {
                panic("You must be a member of Pod #".concat(podID.toString()).concat(" to deposit funds"))
            }
            
            // Deposit funds into the pod
            pod.deposit(amount: amount, from: signer.address)
        } else {
            panic("Pod #".concat(podID.toString()).concat(" not found"))
        }
    }
    
    execute {
        log("✅ Deposited ".concat(amount.toString())
            .concat(" to Pod #")
            .concat(podID.toString())
            .concat(" from ")
            .concat(self.signerAddress.toString()))
        log("This contribution is tracked in the Source-Sink model")
    }
}
