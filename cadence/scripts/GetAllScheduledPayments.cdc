import Pod from 0xf8d6e0586b0a20c7

// Struct to hold scheduled payment info with pod details
access(all) struct ScheduledPaymentInfo {
    access(all) let fromPodID: UInt64
    access(all) let fromPodName: String
    access(all) let paymentID: UInt64
    access(all) let toPodID: UInt64
    access(all) let amount: UFix64
    access(all) let schedule: String
    access(all) let nextExecutionAt: UFix64
    access(all) let isActive: Bool
    access(all) let lastExecutedAt: UFix64?

    init(
        fromPodID: UInt64,
        fromPodName: String,
        paymentID: UInt64,
        toPodID: UInt64,
        amount: UFix64,
        schedule: String,
        nextExecutionAt: UFix64,
        isActive: Bool,
        lastExecutedAt: UFix64?
    ) {
        self.fromPodID = fromPodID
        self.fromPodName = fromPodName
        self.paymentID = paymentID
        self.toPodID = toPodID
        self.amount = amount
        self.schedule = schedule
        self.nextExecutionAt = nextExecutionAt
        self.isActive = isActive
        self.lastExecutedAt = lastExecutedAt
    }
}

// Script to get all active scheduled payments across all pods
access(all) fun main(): [ScheduledPaymentInfo] {
    let result: [ScheduledPaymentInfo] = []
    
    // Iterate through all pods
    for podID in Pod.pods.keys {
        if let pod = Pod.borrowPod(podID: podID) {
            // Check each scheduled payment
            for payment in pod.scheduledPayments {
                // Only include active payments
                if payment.isActive {
                    result.append(ScheduledPaymentInfo(
                        fromPodID: pod.id,
                        fromPodName: pod.name,
                        paymentID: payment.id,
                        toPodID: payment.toPodID,
                        amount: payment.amount,
                        schedule: payment.schedule,
                        nextExecutionAt: payment.nextExecutionAt,
                        isActive: payment.isActive,
                        lastExecutedAt: payment.lastExecutedAt
                    ))
                }
            }
        }
    }
    
    return result
}
