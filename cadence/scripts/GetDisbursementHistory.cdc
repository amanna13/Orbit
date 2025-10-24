import Pod from 0xf8d6e0586b0a20c7

// Script to retrieve disbursement history for a specific Pod
// Shows all historical disbursements with timestamps
//
// Note: This script structure is prepared for event querying
// Actual event retrieval requires Flow CLI event query commands
//
// Arguments:
//   - podID: The ID of the pod to check disbursement history
access(all) fun main(podID: UInt64): {String: AnyStruct} {
    // Borrow the pod to verify it exists
    let pod = Pod.borrowPod(podID: podID)
        ?? panic("Pod #".concat(podID.toString()).concat(" not found"))
    
    // Return pod context information
    // Actual event history retrieved via: flow events get Pod.PodDisbursement
    return {
        "podID": podID,
        "name": pod.name,
        "creator": pod.creator,
        "currentBalance": pod.getPodBalance(),
        "totalBalance": pod.getTotalBalance(),
        "sinkCount": pod.getSinks().length,
        "paymentMode": pod.getPaymentMode(),
        "note": "Use 'flow events get Pod.PodDisbursement --network emulator' to view disbursement events"
    }
}
