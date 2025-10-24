import Pod from 0xf8d6e0586b0a20c7

// Script to check Pod disbursement readiness and preview amounts
// Shows if a pod has sufficient balance and what each sink will receive
//
// Arguments:
//   - podID: The ID of the pod to check
//
// Returns detailed disbursement preview
access(all) fun main(podID: UInt64): {String: AnyStruct} {
    // Borrow the pod
    let pod = Pod.borrowPod(podID: podID)
        ?? panic("Pod #".concat(podID.toString()).concat(" not found"))
    
    let paymentMode = pod.getPaymentMode()
    let podBalance = pod.getPodBalance()
    let totalBalance = pod.getTotalBalance()
    let sinks = pod.getSinks()
    
    var totalRequired: UFix64 = 0.0
    let disbursementPreview: [{String: AnyStruct}] = []
    
    // Calculate what each sink will receive
    if paymentMode == "fixed" {
        for receiver in sinks.keys {
            let sink = sinks[receiver]!
            if let amount = sink.amount {
                totalRequired = totalRequired + amount
                disbursementPreview.append({
                    "receiver": receiver,
                    "amount": amount,
                    "type": "fixed"
                })
            }
        }
    } else if paymentMode == "ratio" {
        for receiver in sinks.keys {
            let sink = sinks[receiver]!
            if let ratio = sink.ratio {
                let amount = podBalance * ratio / 100.0
                totalRequired = totalRequired + amount
                disbursementPreview.append({
                    "receiver": receiver,
                    "amount": amount,
                    "ratio": ratio,
                    "type": "ratio"
                })
            }
        }
    }
    
    let isReady = podBalance >= totalRequired
    let shortage = isReady ? 0.0 : totalRequired - podBalance
    
    return {
        "podID": podID,
        "paymentMode": paymentMode,
        "podBalance": podBalance,
        "totalBalance": totalBalance,
        "totalRequired": totalRequired,
        "isReady": isReady,
        "shortage": shortage,
        "sinkCount": sinks.length,
        "disbursements": disbursementPreview,
        "remainingAfter": isReady ? podBalance - totalRequired : 0.0
    }
}
