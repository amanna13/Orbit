package com.orbit.data.models

import com.google.gson.annotations.SerializedName

/**
 * Generic API Response wrapper
 */
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: T? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("error")
    val error: ErrorResponse? = null,

    @SerializedName("timestamp")
    val timestamp: String? = null
)

/**
 * Error response from API
 */
data class ErrorResponse(
    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("details")
    val details: Map<String, Any>? = null
)

/**
 * Resource wrapper for UI state management
 * Used to represent loading, success, and error states
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null, val errorType: ErrorType = ErrorType.UNKNOWN) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}

/**
 * Error types for better error handling
 */
enum class ErrorType {
    NETWORK,           // Network connectivity issues
    AUTHENTICATION,    // Auth/wallet issues
    VALIDATION,        // Input validation errors
    SERVER,            // Backend server errors
    TIMEOUT,           // Request timeout
    UNKNOWN            // Unknown errors
}

/**
 * User/Wallet model
 */
data class User(
    @SerializedName("id")
    val id: String,

    @SerializedName("walletAddress")
    val walletAddress: String,

    @SerializedName("displayName")
    val displayName: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("avatarUrl")
    val avatarUrl: String? = null,

    @SerializedName("balance")
    val balance: Double? = null,

    @SerializedName("currency")
    val currency: String = "FLOW",

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("isVerified")
    val isVerified: Boolean = false
)

/**
 * Wallet balance information
 */
data class WalletBalance(
    @SerializedName("address")
    val address: String,

    @SerializedName("balance")
    val balance: Double,

    @SerializedName("currency")
    val currency: String = "FLOW",

    @SerializedName("lockedBalance")
    val lockedBalance: Double? = null,  // Balance locked in pods

    @SerializedName("availableBalance")
    val availableBalance: Double? = null,  // Available to withdraw

    @SerializedName("lastUpdated")
    val lastUpdated: String? = null
)

/**
 * Pagination metadata
 */
data class PaginationMeta(
    @SerializedName("page")
    val page: Int,

    @SerializedName("pageSize")
    val pageSize: Int,

    @SerializedName("totalPages")
    val totalPages: Int,

    @SerializedName("totalItems")
    val totalItems: Int,

    @SerializedName("hasNext")
    val hasNext: Boolean,

    @SerializedName("hasPrevious")
    val hasPrevious: Boolean
)

/**
 * Paginated response wrapper
 */
data class PaginatedResponse<T>(
    @SerializedName("data")
    val data: List<T>,

    @SerializedName("pagination")
    val pagination: PaginationMeta
)

/**
 * Stats/Analytics for a Pod
 */
data class PodStats(
    @SerializedName("podId")
    val podId: Long,

    @SerializedName("totalDeposits")
    val totalDeposits: Double,

    @SerializedName("totalWithdrawals")
    val totalWithdrawals: Double,

    @SerializedName("totalTransactions")
    val totalTransactions: Int,

    @SerializedName("activeMembers")
    val activeMembers: Int,

    @SerializedName("averageContribution")
    val averageContribution: Double? = null,

    @SerializedName("lastActivityAt")
    val lastActivityAt: String? = null,

    @SerializedName("growthRate")
    val growthRate: Double? = null  // Percentage growth
)

/**
 * Notification model
 */
data class Notification(
    @SerializedName("id")
    val id: Long,

    @SerializedName("userId")
    val userId: String,

    @SerializedName("type")
    val type: String,  // "POD_INVITE", "DEPOSIT", "DISTRIBUTION", etc.

    @SerializedName("title")
    val title: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("isRead")
    val isRead: Boolean = false,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("actionUrl")
    val actionUrl: String? = null,  // Deep link URL

    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null
)

