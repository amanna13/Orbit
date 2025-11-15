import PodContract from 0x01

pub fun main(): {UInt64: String} {
    let result: {UInt64: String} = {}
    
    for podID in PodContract.pods.keys {
        if let pod = PodContract.borrowPod(podID: podID) {
            result[podID] = pod.name
        }
    }
    
    return result
}
