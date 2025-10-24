import Pod from 0xf8d6e0586b0a20c7

transaction(joinCode: String) {
    
    // Variables to store found pod information
    var foundPodID: UInt64?
    var podRef: &Pod.PodResource?
    
    prepare(signer: &Account) {
        // Initialize variables
        self.foundPodID = nil
        self.podRef = nil
        
        // Get the signer's address
        let signerAddress = signer.address
        
        // Loop through all pods in the Pod contract to find matching join code
        for podID in Pod.pods.keys {
            // Borrow a reference to the current pod
            if let pod = Pod.borrowPod(podID: podID) {
                // Check if the join code matches
                if pod.joinCode == joinCode {
                    // Check if the user is already a member
                    if pod.members.containsKey(signerAddress) {
                        panic("You are already a member of this pod")
                    }
                    
                    // Store the found pod ID and reference
                    self.foundPodID = podID
                    self.podRef = pod
                    break
                }
            }
        }
        
        // If no matching pod was found, abort the transaction
        if self.foundPodID == nil {
            panic("No pod found with the provided join code")
        }
        
        // Add the signer as a member with role "member"
        if let pod = self.podRef {
            pod.addMember(address: signerAddress, role: "member")
        }
    }
    
    execute {
        // Log confirmation message
        if let podID = self.foundPodID {
            log("Successfully joined Pod ".concat(podID.toString()))
        }
    }
}
