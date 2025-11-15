import PodContract from 0x01

transaction(podID: UInt64) {
    prepare(acct: AuthAccount) {
        let pod = PodContract.borrowPod(podID: podID)
            ?? panic("Pod not found")
        
        let members = pod.members
        if members.length == 0 {
            panic("Pod has no members to distribute funds to")
        }
        
        let podBalance = pod.getPodBalance()
        if podBalance == 0.0 {
            panic("Pod has no funds to distribute")
        }
        
        let memberCount = UInt64(members.length)
        let sharePerMember = podBalance / UFix64(memberCount)
        
        for memberAddress in members.keys {
            pod.deductFromPodBalance(amount: sharePerMember)
            pod.depositFunds(address: memberAddress, amount: sharePerMember)
        }
    }

    execute {
        // Distribution completed
    }
}
