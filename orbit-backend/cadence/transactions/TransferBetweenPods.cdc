import PodContract from 0x01

transaction(senderPodID: UInt64, receiverPodID: UInt64, amount: UFix64) {
    prepare(acct: AuthAccount) {
        let senderPod = PodContract.borrowPod(podID: senderPodID)
            ?? panic("Sender pod not found")
        
        let receiverPod = PodContract.borrowPod(podID: receiverPodID)
            ?? panic("Receiver pod not found")
        
        let senderBalance = senderPod.getPodBalance()
        if senderBalance < amount {
            panic("Insufficient balance in sender pod")
        }
        
        senderPod.deductFromPodBalance(amount: amount)
        receiverPod.addToPodBalance(amount: amount)
    }

    execute {
        // TransferBetweenPods event will be emitted by the contract if implemented
    }
}
