import Pod from 0xf8d6e0586b0a20c7

// Transaction to leave a pod
// Arguments:
//   - podID: The ID of the pod to leave
transaction(podID: UInt64) {
    
    // Variables to store pod reference
    var podRef: &Pod.PodResource?
    var signerAddress: Address?
    
    prepare(signer: &Account) {
        // Initialize variables
        self.podRef = nil
        self.signerAddress = signer.address
        
        // Borrow a reference to the pod
        if let pod = Pod.borrowPod(podID: podID) {
            // Check if the signer is a member of the pod
            if pod.members.containsKey(signer.address) {
                // Store the pod reference
                self.podRef = pod
            } else {
                // User is not a member, abort transaction
                panic("You are not a member of Pod #".concat(podID.toString()))
            }
        } else {
            // Pod does not exist
            panic("Pod with ID ".concat(podID.toString()).concat(" does not exist"))
        }
        
        // Remove the signer from the pod members
        if let pod = self.podRef {
            pod.removeMember(address: signer.address)
        }
    }
    
    execute {
        // Log confirmation message
        if let address = self.signerAddress {
            log("✅ Successfully left Pod #".concat(podID.toString()))
            log("Address ".concat(address.toString()).concat(" has been removed from the pod"))
        }
    }
}
