import PodContract from 0x01

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

access(all) fun main(userAddress: Address): [MyPodInfo] {
    let result: [MyPodInfo] = []

    for podID in PodContract.pods.keys {
        if let pod = PodContract.borrowPod(podID: podID) {
            if pod.members.containsKey(userAddress) {
                let userBalance = pod.getMemberBalance(address: userAddress) ?? 0.0
                
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
