import Pod from 0xf8d6e0586b0a20c7

// Struct to hold Source-Sink pod details
access(all) struct SourceSinkDetails {
    access(all) let podID: UInt64
    access(all) let podName: String
    access(all) let paymentMode: String
    access(all) let totalBalance: UFix64
    access(all) let sources: {Address: Pod.PaymentSource}
    access(all) let sinks: {Address: Pod.PaymentSink}
    access(all) let sourceCount: Int
    access(all) let sinkCount: Int

    init(
        podID: UInt64,
        podName: String,
        paymentMode: String,
        totalBalance: UFix64,
        sources: {Address: Pod.PaymentSource},
        sinks: {Address: Pod.PaymentSink}
    ) {
        self.podID = podID
        self.podName = podName
        self.paymentMode = paymentMode
        self.totalBalance = totalBalance
        self.sources = sources
        self.sinks = sinks
        self.sourceCount = sources.length
        self.sinkCount = sinks.length
    }
}

// Script to get Source-Sink details for a pod
// Arguments:
//   - podID: The ID of the pod to query
access(all) fun main(podID: UInt64): SourceSinkDetails? {
    // Borrow a reference to the pod
    if let pod = Pod.borrowPod(podID: podID) {
        return SourceSinkDetails(
            podID: pod.id,
            podName: pod.name,
            paymentMode: pod.getPaymentMode(),
            totalBalance: pod.getTotalBalance(),
            sources: pod.getSources(),
            sinks: pod.getSinks()
        )
    }
    
    return nil
}
