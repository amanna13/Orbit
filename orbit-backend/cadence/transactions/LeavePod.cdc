import PodContract from 0x01

transaction(podID: UInt64) {
    prepare(acct: AuthAccount) {
        let account = acct.address
        let pod = PodContract.borrowPod(podID: podID)
            ?? panic("Pod not found")
        
        if !pod.members.containsKey(account) {
            panic("You are not a member of this pod")
        }
        
        pod.removeMember(address: account)
    }

    execute {
        // PodLeft event will be emitted by the contract if implemented
    }
}
