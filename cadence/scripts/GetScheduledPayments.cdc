import Pod from 0xf8d6e0586b0a20c7

// Script to get all scheduled payments for a specific pod
// Arguments:
//   - podID: The ID of the pod to query
access(all) fun main(podID: UInt64): [Pod.ScheduledPayment] {
    // Borrow a reference to the pod
    if let pod = Pod.borrowPod(podID: podID) {
        return pod.scheduledPayments
    }
    
    // Return empty array if pod not found
    return []
}
