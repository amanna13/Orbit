package com.orbit.data.models

import com.google.gson.annotations.SerializedName

/**
 * Data model representing a Pod (group wallet)
 * Mirrors backend JSON response for Pod entity
 */
data class Pod(
    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("balance")
    val balance: Double,

    @SerializedName("creator")
    val creator: String,  // Creator's wallet address or user ID

    @SerializedName("memberCount")
    val memberCount: Int,

    @SerializedName("joinCode")
    val joinCode: String? = null,  // Code for joining via QR: flowpods://join?code=XXXXX

    @SerializedName("createdAt")
    val createdAt: String? = null,  // ISO 8601 timestamp

    @SerializedName("updatedAt")
    val updatedAt: String? = null,  // ISO 8601 timestamp

    @SerializedName("emoji")
    val emoji: String? = null,  // Emoji representation for the pod

    @SerializedName("colorTag")
    val colorTag: String? = null,  // Color hex code for UI representation

    @SerializedName("isActive")
    val isActive: Boolean = true,

    @SerializedName("currency")
    val currency: String = "FLOW"  // Currency type (FLOW, USDC, etc.)
)

/**
 * Request body for creating a new Pod
 */
data class CreatePodRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("creatorAddress")
    val creatorAddress: String,

    @SerializedName("initialBalance")
    val initialBalance: Double = 0.0,

    @SerializedName("emoji")
    val emoji: String? = null
)

/**
 * Response after creating a Pod
 */
data class CreatePodResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("pod")
    val pod: Pod,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("joinCode")
    val joinCode: String  // QR code for inviting members
)

/**
 * Request body for joining a Pod
 */
data class JoinPodRequest(
    @SerializedName("code")
    val code: String,  // Join code from QR: flowpods://join?code=XXXXX

    @SerializedName("userAddress")
    val userAddress: String
)

/**
 * Response after joining a Pod
 */
data class JoinPodResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("pod")
    val pod: Pod,

    @SerializedName("message")
    val message: String? = null
)

/**
 * Request body for depositing funds to a Pod
 */
data class DepositRequest(
    @SerializedName("podId")
    val podId: Long,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("sourceWalletId")
    val sourceWalletId: String,

    @SerializedName("timestamp")
    val timestamp: String  // ISO 8601 timestamp
)

/**
 * Request body for transferring funds between Pods
 */
data class TransferRequest(
    @SerializedName("fromPodId")
    val fromPodId: Long,

    @SerializedName("toPodId")
    val toPodId: Long,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("initiatedBy")
    val initiatedBy: String,  // User address who initiated the transfer

    @SerializedName("timestamp")
    val timestamp: String
)

/**
 * List of Pods response
 */
data class PodsListResponse(
    @SerializedName("pods")
    val pods: List<Pod>,

    @SerializedName("totalCount")
    val totalCount: Int
)

