package com.orbit.data.models

import com.google.gson.annotations.SerializedName

/**
 * Data model representing a Member of a Pod
 * Mirrors backend JSON response for Member entity
 */
data class Member(
    @SerializedName("address")
    val address: String,  // Wallet address or user ID

    @SerializedName("role")
    val role: String,  // "CREATOR", "ADMIN", "MEMBER"

    @SerializedName("contributed")
    val contributed: Double? = null,  // Total amount contributed to the pod

    @SerializedName("displayName")
    val displayName: String? = null,  // User's display name

    @SerializedName("avatarUrl")
    val avatarUrl: String? = null,  // Profile picture URL

    @SerializedName("joinedAt")
    val joinedAt: String? = null,  // ISO 8601 timestamp when member joined

    @SerializedName("isActive")
    val isActive: Boolean = true,

    @SerializedName("permissions")
    val permissions: List<String>? = null  // List of permissions: ["DEPOSIT", "WITHDRAW", "INVITE"]
)

/**
 * Member roles enum
 */
enum class MemberRole(val value: String) {
    CREATOR("CREATOR"),
    ADMIN("ADMIN"),
    MEMBER("MEMBER")
}

/**
 * Request body for adding a member to a Pod
 */
data class AddMemberRequest(
    @SerializedName("podId")
    val podId: Long,

    @SerializedName("memberId")
    val memberId: String,  // Wallet address of the new member

    @SerializedName("invitedBy")
    val invitedBy: String,  // Wallet address of who invited them

    @SerializedName("role")
    val role: String = "MEMBER"
)

/**
 * Request body for removing a member from a Pod
 */
data class RemoveMemberRequest(
    @SerializedName("podId")
    val podId: Long,

    @SerializedName("memberId")
    val memberId: String,

    @SerializedName("removedBy")
    val removedBy: String  // Wallet address of admin removing the member
)

/**
 * Distribution allocation for a specific member
 * Used when distributing pod funds to members
 */
data class MemberDistribution(
    @SerializedName("memberId")
    val memberId: String,  // Wallet address

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("percentage")
    val percentage: Double? = null  // Optional: percentage of total distribution
)

/**
 * Request body for distributing funds to members
 */
data class DistributeFundsRequest(
    @SerializedName("podId")
    val podId: Long,

    @SerializedName("distributions")
    val distributions: List<MemberDistribution>,

    @SerializedName("totalAmount")
    val totalAmount: Double,

    @SerializedName("initiatedBy")
    val initiatedBy: String,  // Wallet address of initiator

    @SerializedName("timestamp")
    val timestamp: String
)

/**
 * Response for getting Pod members
 */
data class MembersListResponse(
    @SerializedName("podId")
    val podId: Long,

    @SerializedName("members")
    val members: List<Member>,

    @SerializedName("totalCount")
    val totalCount: Int
)

