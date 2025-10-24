import Pod from 0xf8d6e0586b0a20c7

// Transaction to distribute pod funds equally among all members
// This performs an automatic monthly disbursement where each member receives
// an equal share of the pod's current balance
//
// Use Case:
//   - Equal profit sharing among pod members
//   - Monthly dividend distribution
//   - Automated member payouts
//
// Flasher Integration:
//   Schedule this transaction to run automatically every 30 days
//   Action Name: MonthlyDistribution_<podID>
//   Trigger: Time-based (monthly)
//
// Arguments:
//   - podID: The ID of the pod to distribute funds from
transaction(podID: UInt64) {
    
    // Reference to the pod
    let pod: &Pod.PodResource
    
    // Store distribution details
    var distributions: {Address: UFix64}
    var totalDistributed: UFix64
    var sharePerMember: UFix64
    
    prepare(signer: auth(BorrowValue) &Account) {
        // Initialize variables
        self.distributions = {}
        self.totalDistributed = 0.0
        self.sharePerMember = 0.0
        
        // Borrow reference to the pod
        self.pod = Pod.borrowPod(podID: podID)
            ?? panic("Pod #".concat(podID.toString()).concat(" not found"))
        
        // Verify pod has members
        let members = self.pod.members
        if members.length == 0 {
            panic("Pod has no members to distribute funds to")
        }
        
        // Get current pod balance
        let podBalance = self.pod.getPodBalance()
        
        // Check if there are funds to distribute
        if podBalance == 0.0 {
            panic("Pod has no funds to distribute. Current balance: 0.0 FLOW")
        }
        
        // Calculate each member's equal share
        let memberCount = UInt64(members.length)
        self.sharePerMember = podBalance / UFix64(memberCount)
        
        // Distribute funds to each member
        for memberAddress in members.keys {
            // Deduct from pod balance
            self.pod.deductFromPodBalance(amount: self.sharePerMember)
            
            // Credit to member's balance in the pod
            self.pod.depositFunds(address: memberAddress, amount: self.sharePerMember)
            
            // Record the distribution
            self.distributions[memberAddress] = self.sharePerMember
            self.totalDistributed = self.totalDistributed + self.sharePerMember
        }
    }
    
    execute {
        log("✅ Pod Funds Distribution Successful")
        log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        log("💰 Monthly Distribution Details:")
        log("   Pod ID: #".concat(podID.toString()))
        log("   Pod Name: ".concat(self.pod.name))
        log("   Distribution Type: Equal Share (Member Dividend)")
        log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        log("📊 Distribution Summary:")
        log("   Total Members: ".concat(self.distributions.length.toString()))
        log("   Share Per Member: ".concat(self.sharePerMember.toString()).concat(" FLOW"))
        log("   Total Distributed: ".concat(self.totalDistributed.toString()).concat(" FLOW"))
        log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        log("👥 Member Distributions:")
        
        var memberNum = 1
        for memberAddress in self.distributions.keys {
            let share = self.distributions[memberAddress]!
            let role = self.pod.members[memberAddress] ?? "member"
            log("   Member #".concat(memberNum.toString())
                .concat(" (")
                .concat(role)
                .concat("): ")
                .concat(memberAddress.toString())
                .concat(" → ")
                .concat(share.toString())
                .concat(" FLOW"))
            memberNum = memberNum + 1
        }
        
        log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        log("📊 Remaining Pod Balance: ".concat(self.pod.getPodBalance().toString()).concat(" FLOW"))
        log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        log("🤖 Flasher Integration:")
        log("   This transaction is designed for automated monthly execution")
        log("   Flasher Action Name: MonthlyDistribution_".concat(podID.toString()))
        log("   Schedule: Every 30 days")
        log("   Trigger Type: Time-based (automatic)")
        log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        log("ℹ️  Funds have been credited to each member's balance within the pod")
        log("ℹ️  Members can withdraw their share using withdrawal transactions")
    }
}
