import Pod from 0xf8d6e0586b0a20c7

// Transaction to remove an incoming source from a pod
// Arguments:
//   - podID: The ID of the pod
//   - sourceID: The ID of the source to remove
transaction(podID: UInt64, sourceID: String) {
    
    prepare(signer: &Account) {
        // Borrow reference to the pod
        if let pod = Pod.borrowPod(podID: podID) {
            // Remove the source
            pod.removeIncomingSource(sourceID: sourceID, caller: signer.address)
        } else {
            panic("Pod #".concat(podID.toString()).concat(" not found"))
        }
    }
    
    execute {
        log("✅ Source '".concat(sourceID).concat("' removed from Pod #").concat(podID.toString()))
    }
}
