import Pod from 0xf8d6e0586b0a20c7

// Transaction to deposit funds to a pod member balance
// Arguments:
//   - podID: The ID of the pod to deposit to
//   - amount: The amount to deposit
transaction(podID: UInt64, amount: UFix64) {
    
    // Variables to store transaction data
    var depositAmount: UFix64
    var signerAddress: Address
    
    prepare(signer: &Account) {
        // Initialize variables
        self.depositAmount = amount
        self.signerAddress = signer.address
        
        // Borrow a reference to the pod
        if let pod = Pod.borrowPod(podID: podID) {
            // Check if the signer is a member of the pod
            if !pod.members.containsKey(signer.address) {
                panic("You are not a member of this pod")
            }
            
            // Deposit funds to the member's balance
            pod.depositFunds(address: signer.address, amount: amount)
        } else {
            // Pod does not exist
            panic("Pod not found")
        }
    }
    
    execute {
        // Log confirmation message
        log("✅ Deposited ".concat(self.depositAmount.toString())
            .concat(" to Pod #")
            .concat(podID.toString())
            .concat(" from ")
            .concat(self.signerAddress.toString()))
    }
}
