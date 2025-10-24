import Pod from 0xf8d6e0586b0a20c7
import FungibleToken from 0xee82856bf20e2aa6
import FlowToken from 0x0ae53cb6e3f42a79

// Transaction to deposit Flow tokens into a Pod through a specific Source
// This simulates a payer sending funds to a Pod via an established income source
//
// Arguments:
//   - podID: The ID of the pod receiving the deposit
//   - sourceID: The ID of the source making the payment
//   - amount: The amount of Flow tokens to deposit
transaction(podID: UInt64, sourceID: String, amount: UFix64) {
    
    // Reference to the payer's Flow Token Vault
    let payerVault: auth(FungibleToken.Withdraw) &FlowToken.Vault
    
    // Reference to the Pod
    let podRef: &Pod.PodResource?
    
    // Store payer address for logging
    let payerAddress: Address
    
    prepare(signer: auth(BorrowValue) &Account) {
        self.payerAddress = signer.address
        
        // Borrow reference to the payer's Flow Token Vault
        self.payerVault = signer.storage.borrow<auth(FungibleToken.Withdraw) &FlowToken.Vault>(
            from: /storage/flowTokenVault
        ) ?? panic("Could not borrow reference to the payer's FlowToken Vault")
        
        // Borrow reference to the Pod
        self.podRef = Pod.borrowPod(podID: podID)
        
        if self.podRef == nil {
            panic("Pod #".concat(podID.toString()).concat(" not found"))
        }
        
        // Verify the source exists in the pod
        let pod = self.podRef!
        let sources = pod.getIncomingSources()
        if !sources.containsKey(sourceID) {
            panic("Source ID '".concat(sourceID).concat("' not found in Pod #").concat(podID.toString()))
        }
        
        // Verify the payer has sufficient balance
        if self.payerVault.balance < amount {
            panic("Insufficient balance. Available: ".concat(self.payerVault.balance.toString())
                .concat(", Required: ")
                .concat(amount.toString()))
        }
        
        // Withdraw tokens from payer's vault
        let tokens <- self.payerVault.withdraw(amount: amount)
        
        // For now, we'll destroy the tokens as we're tracking balances in the Pod
        // In a production implementation, the Pod would have its own vault
        destroy tokens
        
        // Record the deposit in the Pod
        pod.recordSourceDeposit(sourceID: sourceID, amount: amount, from: self.payerAddress)
    }
    
    execute {
        log("✅ Source Deposit Successful")
        log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        log("💰 Deposit Details:")
        log("   Pod ID: #".concat(podID.toString()))
        log("   Source ID: ".concat(sourceID))
        log("   Amount: ".concat(amount.toString()).concat(" FLOW"))
        log("   From: ".concat(self.payerAddress.toString()))
        log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        
        if let pod = self.podRef {
            log("📊 Updated Pod Balance: ".concat(pod.getPodBalance().toString()).concat(" FLOW"))
            log("📊 Updated Total Balance: ".concat(pod.getTotalBalance().toString()).concat(" FLOW"))
        }
        
        log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        log("ℹ️  This deposit is tracked as income from the specified source")
    }
}
