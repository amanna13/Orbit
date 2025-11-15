import PodContract from 0x01

transaction(podID: UInt64) {
    prepare(acct: AuthAccount) {
        let account = acct.address
        PodContract.joinPod(podID: podID, account: account)
    }

    execute {
        // PodJoined event will be emitted by the contract
    }
}
