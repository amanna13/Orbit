import PodContract from 0x01

transaction(name: String, joinHash: String) {

    prepare(acct: AuthAccount) {
        let creator = acct.address
        PodContract.createPod(name: name, creator: creator, joinHash: joinHash)
    }

    execute {
        // PodCreated event will be emitted by the contract
    }
}
