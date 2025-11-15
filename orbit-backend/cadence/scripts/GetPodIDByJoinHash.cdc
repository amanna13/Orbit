import PodContract from 0x01

pub fun main(joinHash: String): UInt64? {
    return PodContract.getPodByJoinHash(joinHash: joinHash)
}
