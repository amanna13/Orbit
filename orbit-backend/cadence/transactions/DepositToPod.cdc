import PodContract from 0x01

transaction(podID: UInt64, amount: UFix64) {
    prepare(acct: AuthAccount) {
        let pod = PodContract.borrowPod(podID: podID)
            ?? panic("Pod not found")
        
        if !pod.members.containsKey(acct.address) {
            panic("You are not a member of this pod")
        }
        
        pod.depositFunds(address: acct.address, amount: amount)
    }

    execute {
        // Deposit completed
    }
}
