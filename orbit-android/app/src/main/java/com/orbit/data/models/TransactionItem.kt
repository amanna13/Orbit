package com.orbit.data.models

import com.google.gson.annotations.SerializedName

/**
 * Data model representing a Transaction/Activity item
 * Used for activity feed and transaction history
 * Mirrors backend JSON response for Transaction entity
 */
data class TransactionItem(
    @SerializedName("id")
    val id: Long,

    @SerializedName("type")
    val type: TransactionType,  // DEPOSIT, WITHDRAWAL, TRANSFER, DISTRIBUTION, SCHEDULED_PAYOUT

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("currency")
    val currency: String = "FLOW",

    @SerializedName("fromAddress")
    val fromAddress: String? = null,  // Source wallet/pod address

    @SerializedName("toAddress")
    val toAddress: String? = null,  // Destination wallet/pod address

    @SerializedName("podId")
    val podId: Long? = null,  // Associated pod ID

    @SerializedName("podName")
    val podName: String? = null,  // Associated pod name

    @SerializedName("initiatedBy")
    val initiatedBy: String? = null,  // User who initiated the transaction

    @SerializedName("initiatorName")
    val initiatorName: String? = null,  // Display name of initiator

    @SerializedName("status")
    val status: TransactionStatus,  // PENDING, COMPLETED, FAILED, CANCELLED

    @SerializedName("timestamp")
    val timestamp: String,  // ISO 8601 timestamp

    @SerializedName("description")
    val description: String? = null,  // Transaction description

    @SerializedName("txHash")
    val txHash: String? = null,  // Blockchain transaction hash

    @SerializedName("fee")
    val fee: Double? = null,  // Transaction fee

    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null  // Additional transaction data
)

/**
 * Transaction type enum
 */
enum class TransactionType(val value: String) {
    @SerializedName("DEPOSIT")
    DEPOSIT("DEPOSIT"),

    @SerializedName("WITHDRAWAL")
    WITHDRAWAL("WITHDRAWAL"),

    @SerializedName("TRANSFER")
    TRANSFER("TRANSFER"),

    @SerializedName("DISTRIBUTION")
    DISTRIBUTION("DISTRIBUTION"),

    @SerializedName("SCHEDULED_PAYOUT")
    SCHEDULED_PAYOUT("SCHEDULED_PAYOUT"),

    @SerializedName("REFUND")
    REFUND("REFUND"),

    @SerializedName("FEE")
    FEE("FEE")
}

/**
 * Transaction status enum
 */
enum class TransactionStatus(val value: String) {
    @SerializedName("PENDING")
    PENDING("PENDING"),

    @SerializedName("PROCESSING")
    PROCESSING("PROCESSING"),

    @SerializedName("COMPLETED")
    COMPLETED("COMPLETED"),

    @SerializedName("FAILED")
    FAILED("FAILED"),

    @SerializedName("CANCELLED")
    CANCELLED("CANCELLED"),

    @SerializedName("REFUNDED")
    REFUNDED("REFUNDED")
}

/**
 * Response for getting transaction history
 */
data class TransactionHistoryResponse(
    @SerializedName("transactions")
    val transactions: List<TransactionItem>,

    @SerializedName("totalCount")
    val totalCount: Int,

    @SerializedName("page")
    val page: Int? = null,

    @SerializedName("pageSize")
    val pageSize: Int? = null,

    @SerializedName("hasMore")
    val hasMore: Boolean = false
)

/**
 * Request for filtering transactions
 */
data class TransactionFilterRequest(
    @SerializedName("podId")
    val podId: Long? = null,

    @SerializedName("userId")
    val userId: String? = null,

    @SerializedName("type")
    val type: TransactionType? = null,

    @SerializedName("status")
    val status: TransactionStatus? = null,

    @SerializedName("startDate")
    val startDate: String? = null,  // ISO 8601 timestamp

    @SerializedName("endDate")
    val endDate: String? = null,  // ISO 8601 timestamp

    @SerializedName("minAmount")
    val minAmount: Double? = null,

    @SerializedName("maxAmount")
    val maxAmount: Double? = null,

    @SerializedName("page")
    val page: Int = 1,

    @SerializedName("pageSize")
    val pageSize: Int = 20
)

/**
 * Simplified transaction for activity feed
 */
data class ActivityItem(
    @SerializedName("id")
    val id: Long,

    @SerializedName("title")
    val title: String,  // e.g., "Deposit to Team Alpha"

    @SerializedName("description")
    val description: String,  // e.g., "+$500.00"

    @SerializedName("timestamp")
    val timestamp: String,

    @SerializedName("type")
    val type: TransactionType,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("icon")
    val icon: String? = null,  // Icon identifier or emoji

    @SerializedName("isPositive")
    val isPositive: Boolean = true  // true for credit, false for debit
)

