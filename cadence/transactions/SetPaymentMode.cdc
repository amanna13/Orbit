import Pod from 0xf8d6e0586b0a20c7

// Transaction to set the payment mode for a pod
// Arguments:
//   - podID: The ID of the pod
//   - mode: "fixed" or "ratio"
transaction(podID: UInt64, mode: String) {
    
    prepare(signer: &Account) {
        // Borrow reference to the pod
        if let pod = Pod.borrowPod(podID: podID) {
            // Set the payment mode
            pod.setPaymentMode(mode: mode, caller: signer.address)
        } else {
            panic("Pod #".concat(podID.toString()).concat(" not found"))
        }
    }
    
    execute {
        log("✅ Payment mode set to: ".concat(mode))
    }
}
