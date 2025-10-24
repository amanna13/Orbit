import Pod from 0xf8d6e0586b0a20c7

// Struct to hold pod details for return
access(all) struct PodDetails {
    access(all) let id: UInt64
    access(all) let name: String
    access(all) let joinCode: String
    access(all) let members: {Address: String}
    access(all) let memberBalances: {Address: UFix64}
    access(all) let podBalance: UFix64
    access(all) let memberCount: Int

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

// Script to get detailed information about a specific pod
// Arguments:
//   - podID: The ID of the pod to query
access(all) fun main(podID: UInt64): PodDetails? {
    // Borrow a reference to the pod
    if let pod = Pod.borrowPod(podID: podID) {
        // Create a copy of the members dictionary
        let membersCopy: {Address: String} = {}
        for address in pod.members.keys {
            membersCopy[address] = pod.members[address]!
        }
        
        // Create a copy of the member balances dictionary
        let balancesCopy: {Address: UFix64} = {}
        for address in pod.memberBalances.keys {
            balancesCopy[address] = pod.memberBalances[address]!
        }
        
        // Return detailed pod information
        return PodDetails(
            id: pod.id,
            name: pod.name,
            joinCode: pod.joinCode,
            members: membersCopy,
            memberBalances: balancesCopy,
            podBalance: pod.getPodBalance()
        )
    }
    
    // Return nil if pod not found
    return nil
}
