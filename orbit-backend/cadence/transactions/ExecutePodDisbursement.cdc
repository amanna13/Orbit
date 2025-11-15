import PodContract from 0x01
import FungibleToken from 0xee82856bf20e2aa6
import FlowToken from 0x0ae53cb6e3f42a79

transaction(podID: UInt64, triggerType: String) {
    prepare(acct: AuthAccount) {
        if triggerType != "manual" && triggerType != "flasher" {
            panic("Invalid trigger type")
        }
        
        let pod = PodContract.borrowPod(podID: podID)
            ?? panic("Pod not found")
        
        let disbursements = pod.executeDisbursement(
            caller: acct.address,
            triggerType: triggerType
        )
        
        // Transfer Flow tokens to each sink
        for disbursement in disbursements {
            let receiverAddress = disbursement["receiver"]! as! Address
            let amount = disbursement["amount"]! as! UFix64
            
            let receiverRef = getAccount(receiverAddress)
                .getCapability(/public/flowTokenReceiver)
                .borrow<&{FungibleToken.Receiver}>()
                ?? panic("Could not borrow receiver reference")
            
            let payerVault = acct.borrow<&FlowToken.Vault>(from: /storage/flowTokenVault)
                ?? panic("Could not borrow payer vault")
            
            if payerVault.balance < amount {
                panic("Insufficient Flow tokens")
            }
            
            let tokens <- payerVault.withdraw(amount: amount)
            receiverRef.deposit(from: <-tokens)
        }
    }

    execute {
        // Disbursement completed
    }
}
