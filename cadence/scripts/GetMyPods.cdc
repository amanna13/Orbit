import Pod from 0xf8d6e0586b0a20c7

// Struct to hold basic pod information
access(all) struct MyPodInfo {
    access(all) let id: UInt64
    access(all) let name: String
    access(all) let joinCode: String
    access(all) let myBalance: UFix64

    init(id: UInt64, name: String, joinCode: String, myBalance: UFix64) {
        self.id = id
        self.name = name
        self.joinCode = joinCode
        self.myBalance = myBalance
    }
}

// Script to get all pods that a user is a member of
// Arguments:
//   - userAddress: The address of the user to check
access(all) fun main(userAddress: Address): [MyPodInfo] {
    let result: [MyPodInfo] = []

    // Iterate over all pods in the contract
    for podID in Pod.pods.keys {
        // Borrow a reference to the current pod
        if let pod = Pod.borrowPod(podID: podID) {
            // Check if the user is a member of this pod
            if pod.members.containsKey(userAddress) {
                // Get the user's balance in this pod (default to 0.0 if not set)
                let userBalance = pod.getMemberBalance(address: userAddress) ?? 0.0
                
                // Add this pod to the result array with balance
                result.append(MyPodInfo(
                    id: pod.id,
                    name: pod.name,
                    joinCode: pod.joinCode,
                    myBalance: userBalance
                ))
            }
        }
    }

    return result
}
