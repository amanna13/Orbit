package com.orbit.data.repository

import android.util.Log
import com.orbit.data.models.ErrorType
import com.orbit.data.models.Pod
import com.orbit.data.models.Resource
import com.orbit.data.models.TransactionItem
import com.orbit.network.PodService
import com.orbit.network.models.ForteScheduleRequest
import com.orbit.network.models.ForteScheduleResponse
import com.orbit.network.models.ImmediatePaymentResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Pod-related operations
 * Handles API calls, data transformation, and error handling
 */
@Singleton
class PodRepository @Inject constructor(
    private val podService: PodService
) {

    companion object {
        private const val TAG = "PodRepository"
    }

    /**
     * Get all pods for the specified user address
     *
     * @param address User's wallet address (required)
     * @return Flow of Resource wrapping list of pods
     */
    fun getPods(address: String): Flow<Resource<List<Pod>>> = flow {
        emit(Resource.Loading())

        try {
            Log.d(TAG, "📡 Fetching pods for address: $address")
            val response = podService.getPodsByAddress(address)

            if (response.success && response.data != null) {
                // Map FlowUserPod to Pod
                val pods = response.data.map { flowUserPod ->
                    Pod(
                        id = flowUserPod.id.toLong(),
                        name = flowUserPod.name,
                        creator = address,
                        balance = flowUserPod.myBalance,
                        memberCount = 1, // Will be updated when fetching full details
                        joinCode = flowUserPod.joinCode
                    )
                }
                Log.d(TAG, "Successfully fetched ${pods.size} pods")
                emit(Resource.Success(pods))
            } else {
                emit(
                    Resource.Error(
                        response.error ?: "Failed to fetch pods", errorType = ErrorType.SERVER
                    )
                )
            }

        } catch (e: UnknownHostException) {
            Log.e(TAG, "Network error: ${e.message}")
            emit(
                Resource.Error(
                    "No internet connection. Please check your network.",
                    errorType = ErrorType.NETWORK
                )
            )
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, "Timeout error: ${e.message}")
            emit(
                Resource.Error(
                    "Request timed out. Please try again.", errorType = ErrorType.TIMEOUT
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching pods: ${e.message}")
            emit(
                Resource.Error(
                    e.message ?: "Failed to fetch pods", errorType = ErrorType.SERVER
                )
            )
        }
    }

    /**
     * Get detailed information about a specific pod
     * @param podId Pod ID
     * @return Flow of Resource wrapping pod details
     */
    fun getPodDetails(podId: Long): Flow<Resource<Pod>> = flow {
        emit(Resource.Loading())

        try {

            Log.d(TAG, "Fetching details for pod: $podId")
            val response = podService.getPodDetails(podId.toInt())

            if (response.success && response.data != null) {
                val details = response.data
                // Map FlowPodDetails to Pod
                val pod = Pod(
                    id = details.id.toLong(),
                    name = details.name,
                    creator = details.members.entries.firstOrNull { it.value == "admin" }?.key
                        ?: "",
                    balance = details.podBalance,
                    memberCount = details.memberCount,
                    joinCode = details.joinCode
                )
                Log.d(TAG, "Successfully fetched pod details: ${pod.name}")
                emit(Resource.Success(pod))
            } else {
                emit(
                    Resource.Error(
                        response.error ?: "Failed to fetch pod details",
                        errorType = ErrorType.SERVER
                    )
                )
            }

        } catch (e: UnknownHostException) {
            emit(Resource.Error("No internet connection", errorType = ErrorType.NETWORK))
        } catch (e: SocketTimeoutException) {
            emit(Resource.Error("Request timed out", errorType = ErrorType.TIMEOUT))
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching pod details: ${e.message}")
            emit(
                Resource.Error(
                    e.message ?: "Failed to fetch pod details", errorType = ErrorType.SERVER
                )
            )
        }
    }

    /**
     * Create a new pod
     *
     * @param name Pod name
     * @param creatorAddress Creator's wallet address (required)
     * @param description Pod description (optional)
     * @return Flow of Resource wrapping created pod
     */
    fun createPod(
        name: String,
        creatorAddress: String,
        description: String? = null
    ): Flow<Resource<Pod>> = flow {
        emit(Resource.Loading())

        try {

            // Validate input
            if (name.isBlank()) {
                emit(
                    Resource.Error(
                        "Pod name cannot be empty", errorType = ErrorType.VALIDATION
                    )
                )
                return@flow
            }

            val request = com.orbit.network.models.FlowCreatePodRequest(
                name = name.trim(), creatorAddress = creatorAddress, role = "admin"
            )

            Log.d(TAG, "Creating pod: $name")
            Log.d(TAG, "  Creator: $creatorAddress")

            val response = podService.createPod(request)

            if (response.success) {
                Log.d(TAG, "Pod created successfully!")
                Log.d(TAG, "  Pod ID: ${response.podID}")
                Log.d(TAG, "  Join Code: ${response.joinCode}")

                // Create Pod object from response
                val pod = Pod(
                    id = response.podID?.toLong() ?: 0L,
                    name = name.trim(),
                    creator = creatorAddress,
                    balance = 0.0,
                    memberCount = 1,
                    joinCode = response.joinCode
                )
                emit(Resource.Success(pod))
            } else {
                emit(
                    Resource.Error(
                        response.message ?: "Failed to create pod", errorType = ErrorType.SERVER
                    )
                )
            }

        } catch (e: UnknownHostException) {
            emit(Resource.Error("No internet connection", errorType = ErrorType.NETWORK))
        } catch (e: SocketTimeoutException) {
            emit(Resource.Error("Request timed out", errorType = ErrorType.TIMEOUT))
        } catch (e: Exception) {
            Log.e(TAG, "Error creating pod: ${e.message}")
            emit(
                Resource.Error(
                    e.message ?: "Failed to create pod", errorType = ErrorType.SERVER
                )
            )
        }
    }

    /**
     * Join an existing pod
     *
     * @param joinCode Join code from QR (format: ABCD1234 or flowpods://join?code=ABCD1234)
     * @param signerAddress User's wallet address (required)
     * @return Flow of Resource wrapping joined pod
     */
    fun joinPod(joinCode: String, signerAddress: String): Flow<Resource<Pod>> = flow {
        emit(Resource.Loading())

        try {

            // Extract code if full URL provided
            val code = if (joinCode.contains("code=")) {
                joinCode.substringAfter("code=").substringBefore("&")
            } else {
                joinCode
            }

            // Validate code
            if (code.isBlank()) {
                emit(
                    Resource.Error(
                        "Invalid join code", errorType = ErrorType.VALIDATION
                    )
                )
                return@flow
            }

            Log.d(TAG, "Joining pod with code: $code")
            Log.d(TAG, "  Signer: $signerAddress")

            val request = com.orbit.network.models.FlowJoinPodRequest(
                joinCode = code, signerAddress = signerAddress
            )

            val response = podService.joinPod(request)

            if (response.success) {
                Log.d(TAG, "Successfully joined pod")
                // Create a basic Pod object (full details will be fetched separately)
                val pod = Pod(
                    id = 0L, // Will be updated after fetching pod list
                    name = "Joined Pod", creator = signerAddress, balance = 0.0, memberCount = 1
                )
                emit(Resource.Success(pod))
            } else {
                emit(
                    Resource.Error(
                        response.message ?: "Failed to join pod", errorType = ErrorType.SERVER
                    )
                )
            }

        } catch (e: UnknownHostException) {
            emit(Resource.Error("No internet connection", errorType = ErrorType.NETWORK))
        } catch (e: SocketTimeoutException) {
            emit(Resource.Error("Request timed out", errorType = ErrorType.TIMEOUT))
        } catch (e: Exception) {
            Log.e(TAG, "Error joining pod: ${e.message}")
            emit(
                Resource.Error(
                    e.message ?: "Failed to join pod", errorType = ErrorType.SERVER
                )
            )
        }
    }

    /**
     * Transfer funds between pods
     *
     * @param fromPodId Source pod ID
     * @param toPodId Destination pod ID
     * @param amount Amount to transfer
     * @param signerAddress User's wallet address (required)
     * @return Flow of Resource wrapping success message
     */
    fun transferBetweenPods(
        fromPodId: Long, toPodId: Long, amount: Double, signerAddress: String
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())

        try {

            // Validate amount
            if (amount <= 0) {
                emit(
                    Resource.Error(
                        "Amount must be greater than zero", errorType = ErrorType.VALIDATION
                    )
                )
                return@flow
            }

            val request = com.orbit.network.models.FlowTransferRequest(
                senderPodID = fromPodId.toInt(), receiverPodID = toPodId.toInt(), amount = amount
            )

            Log.d(TAG, "📡 Transferring $amount from pod $fromPodId to pod $toPodId")

            val response = podService.transferBetweenPods(request)

            if (response.success) {
                Log.d(TAG, "Transfer successful")
                emit(Resource.Success(response.message ?: "Transfer successful"))
            } else {
                emit(
                    Resource.Error(
                        response.error ?: response.message ?: "Transfer failed",
                        errorType = ErrorType.SERVER
                    )
                )
            }

        } catch (e: UnknownHostException) {
            emit(Resource.Error("No internet connection", errorType = ErrorType.NETWORK))
        } catch (e: SocketTimeoutException) {
            emit(Resource.Error("Request timed out", errorType = ErrorType.TIMEOUT))
        } catch (e: Exception) {
            Log.e(TAG, "Error transferring funds: ${e.message}")
            emit(
                Resource.Error(
                    e.message ?: "Transfer failed", errorType = ErrorType.SERVER
                )
            )
        }
    }

    /**
     * Create a scheduled payment (Forte integration)
     *
     * @param podId Pod ID
     * @param amount Amount per payment
     * @param frequency Payment frequency ("DAILY", "WEEKLY", "MONTHLY")
     * @param signerAddress User's wallet address (required)
     * @param startDate Start date (ISO 8601)
     * @param endDate End date (optional)
     * @param recipients List of recipient addresses (optional)
     * @return Flow of Resource wrapping success message
     */
    fun createSchedule(
        podId: Long,
        amount: Double,
        frequency: String,
        signerAddress: String,
        startDate: String,
        endDate: String? = null,
        recipients: List<String>? = null
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())

        try {

            // Validate inputs
            if (amount <= 0) {
                emit(
                    Resource.Error(
                        "Amount must be greater than zero", errorType = ErrorType.VALIDATION
                    )
                )
                return@flow
            }

            val validFrequencies = listOf("DAILY", "WEEKLY", "MONTHLY", "BIWEEKLY", "QUARTERLY")
            if (frequency.uppercase() !in validFrequencies) {
                emit(
                    Resource.Error(
                        "Invalid frequency. Must be one of: ${validFrequencies.joinToString()}",
                        errorType = ErrorType.VALIDATION
                    )
                )
                return@flow
            }

            val request = ForteScheduleRequest(
                payerPodID = podId, receiverPodID = 0L, // Will need to be specified by caller
                amount = amount, intervalDays = when (frequency.uppercase()) {
                    "DAILY" -> 1
                    "WEEKLY" -> 7
                    "BIWEEKLY" -> 14
                    "MONTHLY" -> 30
                    "QUARTERLY" -> 90
                    else -> 7
                }
            )

            Log.d(TAG, "📡 Creating schedule for pod $podId")
            Log.d(TAG, "  Amount: $amount, Frequency: $frequency")

            val response = podService.createScheduledPayment(request)

            if (response.success) {
                Log.d(TAG, "Schedule created successfully")
                emit(Resource.Success(response.message ?: "Payment scheduled successfully"))
            } else {
                emit(
                    Resource.Error(
                        response.error ?: response.message ?: "Failed to schedule payment",
                        errorType = ErrorType.SERVER
                    )
                )
            }

        } catch (e: UnknownHostException) {
            emit(Resource.Error("No internet connection", errorType = ErrorType.NETWORK))
        } catch (e: SocketTimeoutException) {
            emit(Resource.Error("Request timed out", errorType = ErrorType.TIMEOUT))
        } catch (e: Exception) {
            Log.e(TAG, " Error creating schedule: ${e.message}")
            emit(
                Resource.Error(
                    e.message ?: "Failed to schedule payment", errorType = ErrorType.SERVER
                )
            )
        }
    }

    /**
     * Create a scheduled payment using Forte API
     * Simplified version that uses Forte's scheduled payments endpoint
     *
     * @param payerPodId Source pod ID
     * @param receiverPodId Destination pod ID (can be same pod for self-scheduled payments)
     * @param amount Amount per payment
     * @param intervalDays Days between payments
     * @return Flow of Resource wrapping Forte schedule response
     */
    fun createForteSchedule(
        payerPodId: Long, receiverPodId: Long, amount: Double, intervalDays: Int
    ): Flow<Resource<ForteScheduleResponse>> = flow {
        emit(Resource.Loading())

        try {

            if (amount <= 0) {
                emit(
                    Resource.Error(
                        "Amount must be greater than zero", errorType = ErrorType.VALIDATION
                    )
                )
                return@flow
            }

            if (intervalDays <= 0) {
                emit(
                    Resource.Error(
                        "Interval must be at least 1 day", errorType = ErrorType.VALIDATION
                    )
                )
                return@flow
            }

            val request = ForteScheduleRequest(
                payerPodID = payerPodId,
                receiverPodID = receiverPodId,
                amount = amount,
                intervalDays = intervalDays
            )

            Log.d(TAG, "📡 Creating Forte schedule")
            Log.d(TAG, "  Payer Pod: $payerPodId → Receiver Pod: $receiverPodId")
            Log.d(TAG, "  Amount: $amount, Interval: $intervalDays days")

            val response = podService.createScheduledPayment(request)

            if (response.success && response.data != null) {
                Log.d(TAG, "Schedule created successfully")
                Log.d(TAG, "  Schedule ID: ${response.data.scheduleID}")
                emit(Resource.Success(response.data))
            } else {
                emit(
                    Resource.Error(
                        response.error ?: response.message ?: "Failed to create schedule",
                        errorType = ErrorType.SERVER
                    )
                )
            }

        } catch (e: UnknownHostException) {
            emit(Resource.Error("No internet connection", errorType = ErrorType.NETWORK))
        } catch (e: SocketTimeoutException) {
            emit(Resource.Error("Request timed out", errorType = ErrorType.TIMEOUT))
        } catch (e: Exception) {
            Log.e(TAG, "Error creating Forte schedule: ${e.message}")
            emit(
                Resource.Error(
                    e.message ?: "Failed to create schedule", errorType = ErrorType.SERVER
                )
            )
        }
    }

    /**
     * Trigger an immediate payment from a pod
     *
     * @param podId Pod ID
     * @param amount Amount to distribute
     * @param recipients List of recipient addresses
     * @param signerAddress User's wallet address (required)
     * @return Flow of Resource wrapping success message
     */
    fun triggerSchedule(
        podId: Long, amount: Double, recipients: List<String>, signerAddress: String
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())

        try {

            if (amount <= 0) {
                emit(
                    Resource.Error(
                        "Amount must be greater than zero", errorType = ErrorType.VALIDATION
                    )
                )
                return@flow
            }

            if (recipients.isEmpty()) {
                emit(
                    Resource.Error(
                        "At least one recipient is required", errorType = ErrorType.VALIDATION
                    )
                )
                return@flow
            }

            val request = com.orbit.network.models.FlowDisburseRequest(
                podID = podId.toInt(), triggerType = "manual", signerAddress = signerAddress
            )

            Log.d(TAG, "Triggering payment from pod $podId")
            Log.d(TAG, "  Amount: $amount, Recipients: ${recipients.size}")

            val response = podService.disbursePod(request)

            if (response.success) {
                Log.d(TAG, "Payment triggered successfully")
                emit(Resource.Success(response.message ?: "Payment triggered successfully"))
            } else {
                emit(
                    Resource.Error(
                        response.error ?: response.message ?: "Failed to trigger payment",
                        errorType = ErrorType.SERVER
                    )
                )
            }

        } catch (e: UnknownHostException) {
            emit(Resource.Error("No internet connection", errorType = ErrorType.NETWORK))
        } catch (e: SocketTimeoutException) {
            emit(Resource.Error("Request timed out", errorType = ErrorType.TIMEOUT))
        } catch (e: Exception) {
            Log.e(TAG, "Error triggering payment: ${e.message}")
            emit(
                Resource.Error(
                    e.message ?: "Failed to trigger payment", errorType = ErrorType.SERVER
                )
            )
        }
    }

    /**
     * Distribute pod funds to all members
     *
     * @param podId Pod ID
     * @param signerAddress User's wallet address (required)
     * @return Flow of Resource wrapping success message
     */
    fun distributeFunds(podId: Long, signerAddress: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())

        try {

            val request = com.orbit.network.models.FlowDistributeRequest(
                podID = podId.toInt(), signerAddress = signerAddress
            )

            Log.d(TAG, "📡 Distributing funds from pod $podId")

            val response = podService.distributePodFunds(request)

            if (response.success) {
                Log.d(TAG, "Funds distributed successfully")
                emit(Resource.Success(response.message ?: "Funds distributed successfully"))
            } else {
                emit(
                    Resource.Error(
                        response.error ?: response.message ?: "Failed to distribute funds",
                        errorType = ErrorType.SERVER
                    )
                )
            }

        } catch (e: UnknownHostException) {
            emit(Resource.Error("No internet connection", errorType = ErrorType.NETWORK))
        } catch (e: SocketTimeoutException) {
            emit(Resource.Error("Request timed out", errorType = ErrorType.TIMEOUT))
        } catch (e: Exception) {
            Log.e(TAG, "Error distributing funds: ${e.message}")
            emit(
                Resource.Error(
                    e.message ?: "Failed to distribute funds", errorType = ErrorType.SERVER
                )
            )
        }
    }

    /**
     * Distribute funds to members with custom amounts
     * Makes individual transfer calls for each member
     *
     * @param podId Source pod ID
     * @param distributions Map of member address to amount
     * @param signerAddress User's wallet address (required)
     * @return Flow of Resource wrapping success message
     */
    fun distributeCustomAmounts(
        podId: Long, distributions: Map<String, Double>, signerAddress: String
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())

        try {

            if (distributions.isEmpty()) {
                emit(
                    Resource.Error(
                        "No members to distribute to", errorType = ErrorType.VALIDATION
                    )
                )
                return@flow
            }

            val totalAmount = distributions.values.sum()
            if (totalAmount <= 0) {
                emit(
                    Resource.Error(
                        "Total distribution amount must be greater than zero",
                        errorType = ErrorType.VALIDATION
                    )
                )
                return@flow
            }

            Log.d(TAG, "Distributing $totalAmount to ${distributions.size} members")

            var successCount = 0
            var failureCount = 0
            val errors = mutableListOf<String?>()

            // Make individual transfer calls for each member
            distributions.forEach { (memberAddress, amount) ->
                if (amount > 0) {
                    try {
                        Log.d(TAG, "  → Transferring $amount to $memberAddress")

                        val request = com.orbit.network.models.FlowTransferRequest(
                            senderPodID = podId.toInt(),
                            receiverPodID = 0, // Individual transfer, not pod-to-pod
                            amount = amount
                        )

                        val response = podService.transferBetweenPods(request)

                        if (response.success) {
                            successCount++
                            Log.d(TAG, "Transfer to $memberAddress successful")
                        } else {
                            failureCount++
                            val error = response.error ?: "Transfer failed"
                            errors.add("$memberAddress: $error")
                            Log.e(TAG, " Transfer to $memberAddress failed: $error")
                        }
                    } catch (e: Exception) {
                        failureCount++
                        errors.add("$memberAddress: ${e.message}")
                        Log.e(TAG, "Exception transferring to $memberAddress: ${e.message}")
                    }
                }
            }

            // Emit result based on success/failure counts
            when {
                successCount == distributions.size -> {
                    Log.d(TAG, "All distributions successful")
                    emit(Resource.Success("Distributed to all $successCount members"))
                }

                successCount > 0 -> {
                    Log.d(TAG, "Partial success: $successCount succeeded, $failureCount failed")
                    emit(Resource.Success("Distributed to $successCount of ${distributions.size} members. ${failureCount} failed."))
                }

                else -> {
                    Log.e(TAG, "All distributions failed")
                    emit(
                        Resource.Error(
                            "Failed to distribute: ${errors.joinToString(", ")}",
                            errorType = ErrorType.SERVER
                        )
                    )
                }
            }

        } catch (e: UnknownHostException) {
            emit(Resource.Error("No internet connection", errorType = ErrorType.NETWORK))
        } catch (e: SocketTimeoutException) {
            emit(Resource.Error("Request timed out", errorType = ErrorType.TIMEOUT))
        } catch (e: Exception) {
            Log.e(TAG, "Error distributing funds: ${e.message}")
            emit(
                Resource.Error(
                    e.message ?: "Failed to distribute funds", errorType = ErrorType.SERVER
                )
            )
        }
    }

    /**
     * Make an immediate payment using Forte API
     * Used for Scan & Pay functionality
     *
     * @param payerPodId Payer pod ID
     * @param receiverPodId Receiver pod ID (extracted from QR code)
     * @param amount Amount to transfer
     * @return Flow of Resource wrapping payment response
     */
    fun makeImmediatePayment(
        payerPodId: Long, receiverPodId: Long, amount: Double
    ): Flow<Resource<ImmediatePaymentResponse>> = flow {
        emit(Resource.Loading())

        try {

            if (amount <= 0) {
                emit(
                    Resource.Error(
                        "Amount must be greater than zero", errorType = ErrorType.VALIDATION
                    )
                )
                return@flow
            }

            val request = com.orbit.network.models.FlowTransferRequest(
                senderPodID = payerPodId.toInt(),
                receiverPodID = receiverPodId.toInt(),
                amount = amount
            )

            Log.d(TAG, "📡 Making immediate payment via Forte")
            Log.d(TAG, "  From Pod: $payerPodId → To Pod: $receiverPodId")
            Log.d(TAG, "  Amount: $amount")

            val response = podService.transferBetweenPods(request)

            if (response.success) {
                Log.d(TAG, "Payment successful")
                // Create ImmediatePaymentResponse from ApiResponse
                val paymentResponse = ImmediatePaymentResponse(
                    transactionId = "immediate_${System.currentTimeMillis()}",
                    status = "success",
                    amount = amount,
                    timestamp = System.currentTimeMillis().toString()
                )
                emit(Resource.Success(paymentResponse))
            } else {
                emit(
                    Resource.Error(
                        response.message ?: "Payment failed", errorType = ErrorType.SERVER
                    )
                )
            }

        } catch (e: UnknownHostException) {
            emit(Resource.Error("No internet connection", errorType = ErrorType.NETWORK))
        } catch (e: SocketTimeoutException) {
            emit(Resource.Error("Request timed out", errorType = ErrorType.TIMEOUT))
        } catch (e: Exception) {
            Log.e(TAG, "Error making payment: ${e.message}")
            emit(
                Resource.Error(
                    e.message ?: "Payment failed", errorType = ErrorType.SERVER
                )
            )
        }
    }

    /**
     * Get transaction history for a specific pod
     *
     * @param podId Pod ID
     * @param limit Number of transactions to fetch (default: 50)
     * @param offset Pagination offset (default: 0)
     * @return Flow of Resource wrapping list of transactions
     */
    fun getTransactions(
        podId: Long, limit: Int = 50, offset: Int = 0
    ): Flow<Resource<List<TransactionItem>>> = flow {
        emit(Resource.Loading())

        try {

            Log.d(TAG, "📡 Fetching transactions for pod $podId")
            Log.d(TAG, "  Limit: $limit, Offset: $offset")

            val response = podService.getPodTransactions(podId, limit, offset)

            if (response.success && response.data != null) {
                Log.d(TAG, "Successfully fetched ${response.data.size} transactions")
                emit(Resource.Success(response.data))
            } else {
                emit(
                    Resource.Error(
                        response.error ?: response.message ?: "Failed to fetch transactions",
                        errorType = ErrorType.SERVER
                    )
                )
            }

        } catch (e: UnknownHostException) {
            emit(Resource.Error("No internet connection", errorType = ErrorType.NETWORK))
        } catch (e: SocketTimeoutException) {
            emit(Resource.Error("Request timed out", errorType = ErrorType.TIMEOUT))
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching transactions: ${e.message}")
            emit(
                Resource.Error(
                    e.message ?: "Failed to fetch transactions", errorType = ErrorType.SERVER
                )
            )
        }
    }

    /**
     * Get Flow balance for a wallet address
     * @param address Wallet address
     * @return Flow of Resource wrapping balance as Double
     */
    fun getFlowBalance(address: String): Flow<Resource<Double>> = flow {
        emit(Resource.Loading())

        try {
            Log.d(TAG, "Fetching Flow balance for: $address")

            val response = podService.getFlowBalance(address)

            if (response.success && response.data != null) {
                val balance = response.data.balance
                println("Balance: $balance")
                Log.d(TAG, "Successfully fetched Flow balance: $balance FLOW")
                emit(Resource.Success(balance))
            } else {
                Log.e(TAG, "Failed to fetch Flow balance: ${response.error}")
                emit(
                    Resource.Error(
                        response.error ?: "Failed to fetch Flow balance",
                        errorType = ErrorType.SERVER
                    )
                )
            }

        } catch (e: UnknownHostException) {
            Log.e(TAG, "Network error fetching Flow balance: ${e.message}")
            emit(
                Resource.Error(
                    "No internet connection. Please check your network.",
                    errorType = ErrorType.NETWORK
                )
            )
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, "Timeout error fetching Flow balance: ${e.message}")
            emit(
                Resource.Error(
                    "Request timed out. Please try again.", errorType = ErrorType.TIMEOUT
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching Flow balance: ${e.message}")
            emit(
                Resource.Error(
                    e.message ?: "Failed to fetch Flow balance", errorType = ErrorType.SERVER
                )
            )
        }
    }
}
