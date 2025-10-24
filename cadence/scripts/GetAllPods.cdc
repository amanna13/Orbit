import Pod from 0xf8d6e0586b0a20c7

// Script to get all pods with their details
// Returns a dictionary mapping podID to pod name
access(all) fun main(): {UInt64: String} {
    let result: {UInt64: String} = {}

    // Loop through all pod IDs in the contract
    for podID in Pod.pods.keys {
        // Borrow a reference to each pod
        if let pod = Pod.borrowPod(podID: podID) {
            // Store the pod name in the result
            result[podID] = pod.name
        }
    }

    return result
}
