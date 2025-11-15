package com.orbit.data.models

import com.google.gson.annotations.SerializedName

/**
 * Data model representing a Scheduled Payment (Forte integration)
 * Mirrors backend JSON response for Schedule entity
 */
data class Schedule(
    @SerializedName("id")
    val id: Long,

    @SerializedName("podId")
    val podId: Long,

    @SerializedName("podName")
    val podName: String? = null,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("frequency")
    val frequency: ScheduleFrequency,  // DAILY, WEEKLY, MONTHLY, CUSTOM

    @SerializedName("startDate")
    val startDate: String,  // ISO 8601 date (YYYY-MM-DD)

    @SerializedName("endDate")
    val endDate: String? = null,  // ISO 8601 date, null for indefinite

    @SerializedName("distributionType")
    val distributionType: DistributionType,  // EQUAL_SPLIT, CUSTOM_ALLOCATION, PROPORTIONAL

    @SerializedName("status")
    val status: ScheduleStatus,  // ACTIVE, PAUSED, COMPLETED, CANCELLED

    @SerializedName("nextExecutionDate")
    val nextExecutionDate: String? = null,  // Next scheduled payout date

    @SerializedName("lastExecutionDate")
    val lastExecutionDate: String? = null,  // Last payout date

    @SerializedName("executionCount")
    val executionCount: Int = 0,  // Number of times executed

    @SerializedName("totalPaidOut")
    val totalPaidOut: Double = 0.0,  // Total amount distributed so far

    @SerializedName("createdBy")
    val createdBy: String,  // User who created the schedule

    @SerializedName("createdAt")
    val createdAt: String,  // ISO 8601 timestamp

    @SerializedName("updatedAt")
    val updatedAt: String? = null,

    @SerializedName("recipients")
    val recipients: List<ScheduleRecipient>? = null,  // List of recipients for custom distribution

    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null  // Additional schedule configuration
)

/**
 * Schedule frequency enum
 */
enum class ScheduleFrequency(val value: String) {
    @SerializedName("DAILY")
    DAILY("DAILY"),

    @SerializedName("WEEKLY")
    WEEKLY("WEEKLY"),

    @SerializedName("BIWEEKLY")
    BIWEEKLY("BIWEEKLY"),

    @SerializedName("MONTHLY")
    MONTHLY("MONTHLY"),

    @SerializedName("QUARTERLY")
    QUARTERLY("QUARTERLY"),

    @SerializedName("CUSTOM")
    CUSTOM("CUSTOM")
}

/**
 * Distribution type enum
 */
enum class DistributionType(val value: String) {
    @SerializedName("EQUAL_SPLIT")
    EQUAL_SPLIT("EQUAL_SPLIT"),  // Split equally among all members

    @SerializedName("CUSTOM_ALLOCATION")
    CUSTOM_ALLOCATION("CUSTOM_ALLOCATION"),  // Custom amounts per recipient

    @SerializedName("PROPORTIONAL")
    PROPORTIONAL("PROPORTIONAL"),  // Based on contribution percentage

    @SerializedName("WEIGHTED")
    WEIGHTED("WEIGHTED")  // Based on custom weights
}

/**
 * Schedule status enum
 */
enum class ScheduleStatus(val value: String) {
    @SerializedName("ACTIVE")
    ACTIVE("ACTIVE"),

    @SerializedName("PAUSED")
    PAUSED("PAUSED"),

    @SerializedName("COMPLETED")
    COMPLETED("COMPLETED"),

    @SerializedName("CANCELLED")
    CANCELLED("CANCELLED"),

    @SerializedName("PENDING")
    PENDING("PENDING")
}

/**
 * Recipient for scheduled payouts
 */
data class ScheduleRecipient(
    @SerializedName("address")
    val address: String,  // Wallet address

    @SerializedName("displayName")
    val displayName: String? = null,

    @SerializedName("amount")
    val amount: Double? = null,  // Fixed amount (for CUSTOM_ALLOCATION)

    @SerializedName("percentage")
    val percentage: Double? = null,  // Percentage (for PROPORTIONAL)

    @SerializedName("weight")
    val weight: Int? = null  // Weight (for WEIGHTED)
)

/**
 * Request body for creating a scheduled payout
 */
data class CreateScheduleRequest(
    @SerializedName("podId")
    val podId: Long,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("frequency")
    val frequency: String,  // "DAILY", "WEEKLY", "MONTHLY"

    @SerializedName("startDate")
    val startDate: String,  // YYYY-MM-DD

    @SerializedName("endDate")
    val endDate: String? = null,

    @SerializedName("distributionType")
    val distributionType: String = "EQUAL_SPLIT",

    @SerializedName("recipients")
    val recipients: List<ScheduleRecipient>? = null,

    @SerializedName("createdBy")
    val createdBy: String
)

/**
 * Request body for updating a schedule
 */
data class UpdateScheduleRequest(
    @SerializedName("scheduleId")
    val scheduleId: Long,

    @SerializedName("amount")
    val amount: Double? = null,

    @SerializedName("frequency")
    val frequency: String? = null,

    @SerializedName("endDate")
    val endDate: String? = null,

    @SerializedName("status")
    val status: String? = null,  // "ACTIVE", "PAUSED", "CANCELLED"

    @SerializedName("updatedBy")
    val updatedBy: String
)

/**
 * Response for getting schedules
 */
data class SchedulesListResponse(
    @SerializedName("schedules")
    val schedules: List<Schedule>,

    @SerializedName("totalCount")
    val totalCount: Int,

    @SerializedName("activeCount")
    val activeCount: Int? = null
)

/**
 * Response after creating/updating a schedule
 */
data class ScheduleResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("schedule")
    val schedule: Schedule,

    @SerializedName("message")
    val message: String? = null
)

/**
 * Execution record for a scheduled payout
 */
data class ScheduleExecution(
    @SerializedName("id")
    val id: Long,

    @SerializedName("scheduleId")
    val scheduleId: Long,

    @SerializedName("executedAt")
    val executedAt: String,  // ISO 8601 timestamp

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("status")
    val status: String,  // "SUCCESS", "FAILED", "PARTIAL"

    @SerializedName("transactionId")
    val transactionId: Long? = null,

    @SerializedName("errorMessage")
    val errorMessage: String? = null
)

