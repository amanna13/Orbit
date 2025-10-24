import Pod from 0xf8d6e0586b0a20c7

// Script to get all incoming sources for a pod
// Arguments:
//   - podID: The ID of the pod to query
access(all) fun main(podID: UInt64): {String: Pod.Source} {
    // Borrow a reference to the pod
    if let pod = Pod.borrowPod(podID: podID) {
        return pod.getIncomingSources()
    }
    
    // Return empty dictionary if pod not found
    return {}
}
