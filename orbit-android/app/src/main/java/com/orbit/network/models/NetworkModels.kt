package com.orbit.network.models

import com.google.gson.annotations.SerializedName

/**
 * Request body for transferring funds between pods
 */
data class TransferRequest(
    @SerializedName("fromPodId")
    val fromPodId: Long,

    @SerializedName("toPodId")
    val toPodId: Long,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("signerAddress")
    val signerAddress: String
)

/**
 * Request body for scheduling a payment (OLD - kept for compatibility)
 */
data class ScheduleRequest(
    @SerializedName("podId")
    val podId: Long,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("frequency")
    val frequency: String,  // "DAILY", "WEEKLY", "MONTHLY"

    @SerializedName("startDate")
    val startDate: String,  // ISO 8601 format

    @SerializedName("endDate")
    val endDate: String? = null,

    @SerializedName("recipients")
    val recipients: List<String>? = null,  // List of wallet addresses

    @SerializedName("signerAddress")
    val signerAddress: String
)

/**
 * Request body for Forte scheduled payments
 */
data class ForteScheduleRequest(
    @SerializedName("payerPodID")
    val payerPodID: Long,

    @SerializedName("receiverPodID")
    val receiverPodID: Long,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("intervalDays")
    val intervalDays: Int
)

/**
 * Response from Forte scheduled payment creation
 */
data class ForteScheduleResponse(
    @SerializedName("scheduleID")
    val scheduleID: String,

    @SerializedName("actionType")
    val actionType: String,

    @SerializedName("metadata")
    val metadata: ScheduleMetadata,

    @SerializedName("status")
    val status: String? = "active",

    @SerializedName("nextExecution")
    val nextExecution: String? = null
)

/**
 * Metadata for scheduled payment
 */
data class ScheduleMetadata(
    @SerializedName("payerPodID")
    val payerPodID: Long,

    @SerializedName("receiverPodID")
    val receiverPodID: Long,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("intervalDays")
    val intervalDays: Int
)

/**
 * Request body for triggering a manual payment
 */
data class TriggerRequest(
    @SerializedName("podId")
    val podId: Long,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("recipients")
    val recipients: List<String>,

    @SerializedName("signerAddress")
    val signerAddress: String
)

/**
 * Request body for Forte immediate payment
 */
data class ImmediatePaymentRequest(
    @SerializedName("payerPodID")
    val payerPodID: Long,

    @SerializedName("receiverPodID")
    val receiverPodID: Long,

    @SerializedName("amount")
    val amount: Double
)

/**
 * Response from Forte immediate payment
 */
data class ImmediatePaymentResponse(
    @SerializedName("transactionId")
    val transactionId: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("timestamp")
    val timestamp: String? = null
)

/**
 * Request body for distributing funds to members
 */
data class DistributeRequest(
    @SerializedName("podId")
    val podId: Long,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("recipients")
    val recipients: List<String>,  // List of wallet addresses

    @SerializedName("signerAddress")
    val signerAddress: String
)

/**
 * Generic API response wrapper
 */
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: T? = null,

    @SerializedName("error")
    val error: String? = null
)

// ==================== Flow Blockchain API Models ====================

/**
 * Request: Create Pod
 * POST /api/flow/pods
 */
data class FlowCreatePodRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("creatorAddress")
    val creatorAddress: String,

    @SerializedName("role")
    val role: String = "admin"
)

/**
 * Response: Create Pod
 */
data class FlowCreatePodResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("transactionId")
    val transactionId: String? = null,

    @SerializedName("podID")
    val podID: Int? = null,

    @SerializedName("joinCode")
    val joinCode: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("error")
    val error: String? = null
)

/**
 * Request: Join Pod
 * POST /api/flow/pods/join
 */
data class FlowJoinPodRequest(
    @SerializedName("joinCode")
    val joinCode: String,

    @SerializedName("signerAddress")
    val signerAddress: String
)

/**
 * Response: Get All Pods
 * GET /api/flow/pods
 */
data class FlowPodsResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: Map<String, String>? = null,  // podID -> podName

    @SerializedName("error")
    val error: String? = null
)

/**
 * Response: Get Pod Details
 * GET /api/flow/pods/:podID
 */
data class FlowPodDetailsResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: FlowPodDetails? = null,

    @SerializedName("error")
    val error: String? = null
)

data class FlowPodDetails(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("joinCode")
    val joinCode: String,

    @SerializedName("members")
    val members: Map<String, String>,  // address -> role

    @SerializedName("memberBalances")
    val memberBalances: Map<String, Double>,  // address -> balance

    @SerializedName("podBalance")
    val podBalance: Double,

    @SerializedName("memberCount")
    val memberCount: Int
)

/**
 * Response: Get Pods by Address
 * GET /api/flow/pods/by-address/:address
 */
data class FlowPodsByAddressResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: List<FlowUserPod>? = null,

    @SerializedName("error")
    val error: String? = null
)

data class FlowUserPod(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("joinCode")
    val joinCode: String,

    @SerializedName("myBalance")
    val myBalance: Double
)

/**
 * Response: Get Flow Balance
 * GET /api/flow/balance/:address
 */
data class FlowBalanceResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: FlowBalanceData? = null,

    @SerializedName("error")
    val error: String? = null
)

data class FlowBalanceData(
    @SerializedName("address")
    val address: String,

    @SerializedName("balance")
    val balance: Double,

    @SerializedName("formatted")
    val formatted: String
)

/**
 * Request: Transfer Between Pods
 * POST /api/flow/transfer
 */
data class FlowTransferRequest(
    @SerializedName("senderPodID")
    val senderPodID: Int,

    @SerializedName("receiverPodID")
    val receiverPodID: Int,

    @SerializedName("amount")
    val amount: Double
)

/**
 * Request: Deposit to Pod
 * POST /api/flow/deposit
 */
data class FlowDepositRequest(
    @SerializedName("podID")
    val podID: Int,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("signerAddress")
    val signerAddress: String
)

/**
 * Request: Distribute Pod Funds
 * POST /api/flow/distribute
 */
data class FlowDistributeRequest(
    @SerializedName("podID")
    val podID: Int,

    @SerializedName("signerAddress")
    val signerAddress: String
)

/**
 * Request: Disburse Pod
 * POST /api/flow/disburse
 */
data class FlowDisburseRequest(
    @SerializedName("podID")
    val podID: Int,

    @SerializedName("triggerType")
    val triggerType: String,  // "manual" or "flasher"

    @SerializedName("signerAddress")
    val signerAddress: String
)
