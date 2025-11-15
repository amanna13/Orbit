access(all) contract PodContract {

    access(all) struct Pod {
        access(all) let id: UInt64
        access(all) let name: String
        access(all) let creator: Address
        access(all) var members: [Address]
        access(all) let joinHash: String

        init(id: UInt64, name: String, creator: Address, joinHash: String) {
            self.id = id
            self.name = name
            self.creator = creator
            self.members = [creator]
            self.joinHash = joinHash
        }
    }

    access(all) var pods: {UInt64: Pod}
    access(all) var podByJoinHash: {String: UInt64}
    access(all) var nextPodID: UInt64

    access(all) event PodCreated(id: UInt64, creator: Address)
    access(all) event PodJoined(id: UInt64, member: Address)

    init() {
        self.pods = {}
        self.podByJoinHash = {}
        self.nextPodID = 1
    }

    access(all) fun createPod(name: String, creator: Address, joinHash: String): UInt64 {
        let id = self.nextPodID
        self.nextPodID = self.nextPodID + UInt64(1)
        let pod = Pod(id: id, name: name, creator: creator, joinHash: joinHash)
        self.pods[id] = pod
        self.podByJoinHash[joinHash] = id
        emit PodCreated(id: id, creator: creator)
        return id
    }

    access(all) fun joinPod(podID: UInt64, account: Address) {
        let pod = self.pods[podID] ?? panic("Pod not found")
        var already: Bool = false
        for m in pod.members {
            if m == account {
                already = true
                break
            }
        }
        if !already {
            pod.members.append(account)
            self.pods[podID] = pod
            emit PodJoined(id: podID, member: account)
        }
    }

    access(all) fun getPodByJoinHash(joinHash: String): UInt64? {
        return self.podByJoinHash[joinHash]
    }

    access(all) fun getAllPods(): {UInt64: Pod} {
        return self.pods
    }

    access(all) fun getPodDetails(podID: UInt64): Pod? {
        return self.pods[podID]
    }
}
