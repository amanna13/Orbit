import PodContract from 0x01

pub struct PodDetails {
    pub let id: UInt64
    pub let name: String
    pub let joinCode: String
    pub let members: {Address: String}
    pub let memberBalances: {Address: UFix64}
    pub let podBalance: UFix64
    pub let memberCount: Int

    init(id: UInt64, name: String, joinCode: String, members: {Address: String}, memberBalances: {Address: UFix64}, podBalance: UFix64) {
        self.id = id
        self.name = name
        self.joinCode = joinCode
        self.members = members
        self.memberBalances = memberBalances
        self.podBalance = podBalance
        self.memberCount = members.length
    }
}

pub fun main(podID: UInt64): PodDetails? {
    if let pod = PodContract.borrowPod(podID: podID) {
        let membersCopy: {Address: String} = {}
        for address in pod.members.keys {
            membersCopy[address] = pod.members[address]!
        }
        
        let balancesCopy: {Address: UFix64} = {}
        for address in pod.memberBalances.keys {
            balancesCopy[address] = pod.memberBalances[address]!
        }
        
        return PodDetails(
            id: pod.id,
            name: pod.name,
            joinCode: pod.joinCode,
            members: membersCopy,
            memberBalances: balancesCopy,
            podBalance: pod.getPodBalance()
        )
    }
    
    return nil
}
