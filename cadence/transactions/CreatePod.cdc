import Pod from 0xf8d6e0586b0a20c7

// Transaction to create a new pod
// Arguments:
//   - podName: The name of the pod to create
//   - userRole: The role of the creator (e.g., "owner", "admin")
transaction(podName: String, userRole: String) {
    
    // Variable to store the created pod ID
    var createdPodID: UInt64?
    var generatedJoinCode: String?
    
    prepare(signer: &Account) {
        // Initialize variables
        self.createdPodID = nil
        self.generatedJoinCode = nil
        
        // Get the signer's address (creator of the pod)
        let creatorAddress = signer.address
        
        // Create a new pod using the Pod contract's createPod function
        // This automatically:
        //   - Generates a unique podID
        //   - Generates a random alphanumeric joinCode
        //   - Creates and stores the PodResource
        //   - Adds the creator to the members dictionary
        let podID = Pod.createPod(
            name: podName,
            creator: creatorAddress,
            role: userRole
        )
        
        // Store the created pod ID
        self.createdPodID = podID
        
        // Borrow the pod reference to get the join code
        if let pod = Pod.borrowPod(podID: podID) {
            self.generatedJoinCode = pod.joinCode
        }
    }
    
    execute {
        // Log the pod creation details for the user
        if let podID = self.createdPodID {
            log("✅ Pod created successfully!")
            log("Pod ID: ".concat(podID.toString()))
            
            if let joinCode = self.generatedJoinCode {
                log("Join Code: ".concat(joinCode))
                log("Share this code with others to let them join your pod!")
            }
            
            // Optionally get and log additional pod details
            if let pod = Pod.borrowPod(podID: podID) {
                log("Pod Name: ".concat(pod.name))
                log("Creator Role: ".concat(userRole))
                log("Total Members: ".concat(pod.members.length.toString()))
            }
        }
    }
}
