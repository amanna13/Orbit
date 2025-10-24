access(all) contract Pod {

    // Events
    access(all) event SourceAdded(podID: UInt64, sourceID: String, rate: UFix64, isFixedRate: Bool, sourceAddress: Address)
    access(all) event SinkAdded(podID: UInt64, sinkID: String, receiver: Address)
    access(all) event SourceDeposit(podID: UInt64, sourceID: String, amount: UFix64, from: Address)
    access(all) event PodDisbursement(podID: UInt64, sinkAddress: Address, amount: UFix64, triggerType: String)

    // Struct to hold scheduled payment information
    access(all) struct ScheduledPayment {
        access(all) let id: UInt64
        access(all) let toPodID: UInt64
        access(all) let amount: UFix64
        access(all) let schedule: String
        access(all) let createdAt: UFix64
        access(all) var lastExecutedAt: UFix64?
        access(all) var nextExecutionAt: UFix64
        access(all) var isActive: Bool

        init(id: UInt64, toPodID: UInt64, amount: UFix64, schedule: String, createdAt: UFix64) {
            self.id = id
            self.toPodID = toPodID
            self.amount = amount
            self.schedule = schedule
            self.createdAt = createdAt
            self.lastExecutedAt = nil
            // For simplicity, set next execution to creation time + 60 seconds
            // In production, parse the schedule string to calculate proper interval
            self.nextExecutionAt = createdAt + 60.0
            self.isActive = true
        }

        // Update execution timestamp
        access(all) fun markExecuted(timestamp: UFix64) {
            self.lastExecutedAt = timestamp
            // Calculate next execution based on schedule
            // For now, add 60 seconds (simplified - would parse schedule string in production)
            self.nextExecutionAt = timestamp + 60.0
        }

        // Deactivate the scheduled payment
        access(all) fun deactivate() {
            self.isActive = false
        }
    }

    // Struct for payment sink (receiver)
    access(all) struct PaymentSink {
        access(all) let receiver: Address
        access(all) let amount: UFix64?      // For fixed mode
        access(all) let ratio: UFix64?       // For ratio mode (percentage)

        init(receiver: Address, amount: UFix64?, ratio: UFix64?) {
            self.receiver = receiver
            self.amount = amount
            self.ratio = ratio
        }
    }

    // Struct for payment source (contributor)
    access(all) struct PaymentSource {
        access(all) let contributor: Address
        access(all) var contributedAmount: UFix64

        init(contributor: Address, contributedAmount: UFix64) {
            self.contributor = contributor
            self.contributedAmount = contributedAmount
        }

        // Update contributed amount
        access(all) fun addContribution(amount: UFix64) {
            self.contributedAmount = self.contributedAmount + amount
        }
    }

    // Struct for incoming Source (income stream)
    access(all) struct Source {
        access(all) let id: String
        access(all) let sourceAddress: Address    // The payer Pod or user
        access(all) let rate: UFix64               // Fixed amount or percentage
        access(all) let isFixedRate: Bool          // true = fixed amount, false = ratio
        access(all) let scheduleID: String?        // Optional link to scheduled transaction
        access(all) let createdAt: UFix64

        init(id: String, sourceAddress: Address, rate: UFix64, isFixedRate: Bool, scheduleID: String?) {
            self.id = id
            self.sourceAddress = sourceAddress
            self.rate = rate
            self.isFixedRate = isFixedRate
            self.scheduleID = scheduleID
            self.createdAt = getCurrentBlock().timestamp
        }
    }

    // PodResource - represents a pod with members
    access(all) resource PodResource {
        access(all) let id: UInt64
        access(all) let name: String
        access(all) let joinCode: String
        access(all) let creator: Address
        access(all) var members: {Address: String}
        access(all) var memberBalances: {Address: UFix64}
        access(all) var podBalance: UFix64
        access(all) var scheduledPayments: [ScheduledPayment]
        access(all) var nextScheduledPaymentID: UInt64
        
        // Source-Sink Model fields
        access(all) var paymentMode: String              // "fixed" or "ratio"
        access(all) var sources: {Address: PaymentSource}
        access(all) var sinks: {Address: PaymentSink}
        access(all) var totalBalance: UFix64
        
        // Incoming Sources (income streams)
        access(all) var incomingSources: {String: Source}

        init(id: UInt64, name: String, joinCode: String, creator: Address, role: String) {
            self.id = id
            self.name = name
            self.joinCode = joinCode
            self.creator = creator
            self.members = {creator: role}
            // Initialize creator's balance to 0
            self.memberBalances = {creator: 0.0}
            // Initialize pod-level balance to 0
            self.podBalance = 0.0
            // Initialize scheduled payments array
            self.scheduledPayments = []
            self.nextScheduledPaymentID = 1
            
            // Initialize Source-Sink model fields
            self.paymentMode = "fixed"  // Default to fixed mode
            self.sources = {}
            self.sinks = {}
            self.totalBalance = 0.0
            self.incomingSources = {}
        }

        // Function to add a member to the pod
        access(all) fun addMember(address: Address, role: String) {
            // Dictionary insert returns the old value if key existed, discard it
            let _ = self.members.insert(key: address, role)
            // Initialize the new member's balance to 0 if not already set
            if self.memberBalances[address] == nil {
                self.memberBalances[address] = 0.0
            }
        }

        // Function to remove a member from the pod
        access(all) fun removeMember(address: Address) {
            self.members.remove(key: address)
            self.memberBalances.remove(key: address)
        }

        // Function to deposit funds for a member
        access(all) fun depositFunds(address: Address, amount: UFix64) {
            // Check if the member exists in the members mapping
            if !self.members.containsKey(address) {
                panic("Member not found")
            }
            
            // Get current balance or 0 if not set
            let currentBalance = self.memberBalances[address] ?? 0.0
            
            // Increment the member's balance by the amount
            self.memberBalances[address] = currentBalance + amount
        }

        // Getter function to retrieve a member's balance
        access(all) fun getMemberBalance(address: Address): UFix64? {
            return self.memberBalances[address]
        }

        // Function to get the pod's total balance
        access(all) fun getPodBalance(): UFix64 {
            return self.podBalance
        }

        // Function to add funds to the pod balance
        access(all) fun addToPodBalance(amount: UFix64) {
            self.podBalance = self.podBalance + amount
        }

        // Function to deduct funds from the pod balance
        access(all) fun deductFromPodBalance(amount: UFix64) {
            if self.podBalance < amount {
                panic("Insufficient pod balance")
            }
            self.podBalance = self.podBalance - amount
        }

        // Function to add a scheduled payment
        access(all) fun addScheduledPayment(toPodID: UInt64, amount: UFix64, schedule: String) {
            let payment = ScheduledPayment(
                id: self.nextScheduledPaymentID,
                toPodID: toPodID,
                amount: amount,
                schedule: schedule,
                createdAt: getCurrentBlock().timestamp
            )
            self.scheduledPayments.append(payment)
            self.nextScheduledPaymentID = self.nextScheduledPaymentID + 1
        }

        // Function to update a scheduled payment after execution
        access(all) fun updateScheduledPayment(paymentIndex: Int, timestamp: UFix64) {
            if paymentIndex < self.scheduledPayments.length {
                self.scheduledPayments[paymentIndex].markExecuted(timestamp: timestamp)
            }
        }

        // Function to cancel a scheduled payment
        access(all) fun cancelScheduledPayment(paymentIndex: Int) {
            if paymentIndex < self.scheduledPayments.length {
                self.scheduledPayments[paymentIndex].deactivate()
            }
        }

        // ========================================
        // Source-Sink Payment Model Functions
        // ========================================

        // Check if an address is authorized (creator or has specific role)
        access(all) fun isAuthorized(address: Address): Bool {
            if address == self.creator {
                return true
            }
            // Check if member has admin or owner role
            if let role = self.members[address] {
                return role == "owner" || role == "admin" || role == "creator"
            }
            return false
        }

        // Set payment mode
        access(all) fun setPaymentMode(mode: String, caller: Address) {
            if !self.isAuthorized(address: caller) {
                panic("Only authorized members can change payment mode")
            }
            if mode != "fixed" && mode != "ratio" {
                panic("Invalid payment mode. Must be 'fixed' or 'ratio'")
            }
            self.paymentMode = mode
        }

        // Deposit funds into the pod (Source-Sink model)
        access(all) fun deposit(amount: UFix64, from: Address) {
            // Add to total balance
            self.totalBalance = self.totalBalance + amount
            
            // Update or create PaymentSource entry
            if let existingSource = self.sources[from] {
                existingSource.addContribution(amount: amount)
                self.sources[from] = existingSource
            } else {
                let newSource = PaymentSource(contributor: from, contributedAmount: amount)
                self.sources[from] = newSource
            }
        }

        // Add a payment sink (receiver)
        access(all) fun addSink(receiver: Address, amount: UFix64?, ratio: UFix64?, caller: Address) {
            if !self.isAuthorized(address: caller) {
                panic("Only authorized members can add sinks")
            }
            
            // Validate based on payment mode
            if self.paymentMode == "fixed" {
                if amount == nil {
                    panic("Fixed mode requires a non-nil amount")
                }
            } else if self.paymentMode == "ratio" {
                if ratio == nil {
                    panic("Ratio mode requires a non-nil ratio")
                }
                if ratio! > 100.0 {
                    panic("Ratio cannot exceed 100%")
                }
            }
            
            let sink = PaymentSink(receiver: receiver, amount: amount, ratio: ratio)
            self.sinks[receiver] = sink
        }

        // Remove a payment sink
        access(all) fun removeSink(receiver: Address, caller: Address) {
            if !self.isAuthorized(address: caller) {
                panic("Only authorized members can remove sinks")
            }
            self.sinks.remove(key: receiver)
        }

        // Distribute payments to all sinks
        access(all) fun distributePayments(caller: Address): {Address: UFix64} {
            if !self.isAuthorized(address: caller) {
                panic("Only authorized members can distribute payments")
            }
            
            let distributions: {Address: UFix64} = {}
            var totalDistributed: UFix64 = 0.0
            
            if self.paymentMode == "fixed" {
                // Fixed mode: distribute exact amounts
                for receiver in self.sinks.keys {
                    let sink = self.sinks[receiver]!
                    if let fixedAmount = sink.amount {
                        // Check if we have sufficient balance
                        if self.totalBalance >= fixedAmount {
                            distributions[receiver] = fixedAmount
                            totalDistributed = totalDistributed + fixedAmount
                        } else {
                            // Insufficient funds, skip this sink
                            log("Warning: Insufficient funds to pay ".concat(receiver.toString()))
                        }
                    }
                }
            } else if self.paymentMode == "ratio" {
                // Ratio mode: distribute based on percentages
                for receiver in self.sinks.keys {
                    let sink = self.sinks[receiver]!
                    if let percentage = sink.ratio {
                        let paymentAmount = self.totalBalance * percentage / 100.0
                        distributions[receiver] = paymentAmount
                        totalDistributed = totalDistributed + paymentAmount
                    }
                }
            }
            
            // Deduct distributed amount from total balance
            self.totalBalance = self.totalBalance - totalDistributed
            
            return distributions
        }

        // Get payment mode
        access(all) fun getPaymentMode(): String {
            return self.paymentMode
        }

        // Get total balance (Source-Sink)
        access(all) fun getTotalBalance(): UFix64 {
            return self.totalBalance
        }

        // Get all sources
        access(all) fun getSources(): {Address: PaymentSource} {
            return self.sources
        }

        // Get all sinks
        access(all) fun getSinks(): {Address: PaymentSink} {
            return self.sinks
        }

        // Add an incoming source to the pod
        access(all) fun addIncomingSource(
            sourceID: String,
            sourceAddress: Address,
            rate: UFix64,
            isFixedRate: Bool,
            scheduleID: String?,
            caller: Address
        ) {
            // Check authorization
            if !self.isAuthorized(address: caller) {
                panic("Only authorized members can add incoming sources")
            }
            
            // Check if source ID already exists
            if self.incomingSources.containsKey(sourceID) {
                panic("Source ID '".concat(sourceID).concat("' already exists in this pod"))
            }
            
            // Create new source
            let newSource = Source(
                id: sourceID,
                sourceAddress: sourceAddress,
                rate: rate,
                isFixedRate: isFixedRate,
                scheduleID: scheduleID
            )
            
            // Add to pod
            self.incomingSources[sourceID] = newSource
            
            // Emit event
            emit SourceAdded(
                podID: self.id,
                sourceID: sourceID,
                rate: rate,
                isFixedRate: isFixedRate,
                sourceAddress: sourceAddress
            )
        }

        // Get all incoming sources
        access(all) fun getIncomingSources(): {String: Source} {
            return self.incomingSources
        }

        // Remove an incoming source
        access(all) fun removeIncomingSource(sourceID: String, caller: Address) {
            if !self.isAuthorized(address: caller) {
                panic("Only authorized members can remove incoming sources")
            }
            
            if !self.incomingSources.containsKey(sourceID) {
                panic("Source ID '".concat(sourceID).concat("' not found"))
            }
            
            self.incomingSources.remove(key: sourceID)
        }

        // Record a deposit from a source
        access(all) fun recordSourceDeposit(sourceID: String, amount: UFix64, from: Address) {
            // Verify source exists
            if !self.incomingSources.containsKey(sourceID) {
                panic("Source ID '".concat(sourceID).concat("' not found in this pod"))
            }
            
            // Update pod balance
            self.podBalance = self.podBalance + amount
            self.totalBalance = self.totalBalance + amount
            
            // Emit event
            emit SourceDeposit(
                podID: self.id,
                sourceID: sourceID,
                amount: amount,
                from: from
            )
        }

        // Execute disbursement to all active sinks
        access(all) fun executeDisbursement(caller: Address, triggerType: String): [{String: AnyStruct}] {
            if !self.isAuthorized(address: caller) {
                panic("Only authorized members can execute disbursement")
            }
            
            let disbursements: [{String: AnyStruct}] = []
            var totalDisbursed: UFix64 = 0.0
            
            // Calculate disbursement for each sink
            if self.paymentMode == "fixed" {
                // Fixed mode: pay exact amounts
                for receiver in self.sinks.keys {
                    let sink = self.sinks[receiver]!
                    if let fixedAmount = sink.amount {
                        // Check if we have sufficient balance in podBalance
                        if self.podBalance >= fixedAmount {
                            // Record the disbursement
                            disbursements.append({
                                "receiver": receiver,
                                "amount": fixedAmount
                            })
                            totalDisbursed = totalDisbursed + fixedAmount
                            
                            // Emit event for each disbursement
                            emit PodDisbursement(
                                podID: self.id,
                                sinkAddress: receiver,
                                amount: fixedAmount,
                                triggerType: triggerType
                            )
                        } else {
                            panic("Insufficient pod balance to disburse to ".concat(receiver.toString()))
                        }
                    }
                }
            } else if self.paymentMode == "ratio" {
                // Ratio mode: distribute based on percentages
                for receiver in self.sinks.keys {
                    let sink = self.sinks[receiver]!
                    if let percentage = sink.ratio {
                        let paymentAmount = self.podBalance * percentage / 100.0
                        
                        // Record the disbursement
                        disbursements.append({
                            "receiver": receiver,
                            "amount": paymentAmount
                        })
                        totalDisbursed = totalDisbursed + paymentAmount
                        
                        // Emit event for each disbursement
                        emit PodDisbursement(
                            podID: self.id,
                            sinkAddress: receiver,
                            amount: paymentAmount,
                            triggerType: triggerType
                        )
                    }
                }
            }
            
            // Deduct total disbursed from pod balance
            if self.podBalance < totalDisbursed {
                panic("Insufficient pod balance for total disbursement")
            }
            self.podBalance = self.podBalance - totalDisbursed
            self.totalBalance = self.totalBalance - totalDisbursed
            
            return disbursements
        }
    }

    // Storage for all pods
    access(all) var pods: @{UInt64: PodResource}
    access(all) var nextPodID: UInt64

    // Generate a random alphanumeric join code
    access(all) fun generateJoinCode(): String {
        let characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        var joinCode = ""
        var i = 0
        
        // Using block height and timestamp as pseudo-random source
        let blockHeight = getCurrentBlock().height
        let timestamp = getCurrentBlock().timestamp
        let seed = UInt64(blockHeight) + UInt64(timestamp)
        
        while i < 8 {
            let randomIndex = (seed + UInt64(i)) % UInt64(characters.length)
            joinCode = joinCode.concat(characters.slice(from: Int(randomIndex), upTo: Int(randomIndex) + 1))
            i = i + 1
        }
        
        return joinCode
    }

    // Create a new pod
    access(all) fun createPod(name: String, creator: Address, role: String): UInt64 {
        let podID = self.nextPodID
        let joinCode = self.generateJoinCode()
        
        let newPod <- create PodResource(
            id: podID,
            name: name,
            joinCode: joinCode,
            creator: creator,
            role: role
        )
        
        self.pods[podID] <-! newPod
        self.nextPodID = self.nextPodID + 1
        
        return podID
    }

    // Borrow a reference to a pod
    access(all) fun borrowPod(podID: UInt64): &PodResource? {
        return &self.pods[podID]
    }

    init() {
        self.pods <- {}
        self.nextPodID = 1
    }
}
