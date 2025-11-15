package com.orbit.network

import com.orbit.data.models.TransactionItem
import com.orbit.data.models.Member
import com.orbit.network.models.ApiResponse
import com.orbit.network.models.FlowBalanceResponse
import com.orbit.network.models.FlowCreatePodRequest
import com.orbit.network.models.FlowCreatePodResponse
import com.orbit.network.models.FlowJoinPodRequest
import com.orbit.network.models.FlowPodDetailsResponse
import com.orbit.network.models.FlowPodsResponse
import com.orbit.network.models.FlowPodsByAddressResponse
import com.orbit.network.models.FlowTransferRequest
import com.orbit.network.models.FlowDepositRequest
import com.orbit.network.models.FlowDistributeRequest
import com.orbit.network.models.FlowDisburseRequest
import com.orbit.network.models.ForteScheduleRequest
import com.orbit.network.models.ForteScheduleResponse
import retrofit2.http.*

/**
 * Retrofit service interface for Orbit Backend API
 * Based on API_ENDPOINTS_REFERENCE.md
 */
interface PodService {

    // ==================== Health & System ====================

    @GET("health")
    suspend fun healthCheck(): ApiResponse<Map<String, Any>>

    // ==================== Pod Management ====================

    /**
     * Create a new pod
     * POST /api/flow/pods
     */
    @POST("api/flow/pods")
    suspend fun createPod(
        @Body request: FlowCreatePodRequest
    ): FlowCreatePodResponse

    /**
     * Join an existing pod
     * POST /api/flow/pods/join
     */
    @POST("api/flow/pods/join")
    suspend fun joinPod(
        @Body request: FlowJoinPodRequest
    ): ApiResponse<String>

    /**
     * Leave a pod
     * POST /api/flow/pods/leave
     */
    @POST("api/flow/pods/leave")
    suspend fun leavePod(
        @Body request: Map<String, Any>  // podID, signerAddress
    ): ApiResponse<String>

    /**
     * Get all pods
     * GET /api/flow/pods
     */
    @GET("api/flow/pods")
    suspend fun getAllPods(): FlowPodsResponse

    /**
     * Get pod details by ID
     * GET /api/flow/pods/:podID
     */
    @GET("api/flow/pods/{podID}")
    suspend fun getPodDetails(
        @Path("podID") podID: Int
    ): FlowPodDetailsResponse

    /**
     * Get pods by user address
     * GET /api/flow/pods/by-address/:address
     */
    @GET("api/flow/pods/by-address/{address}")
    suspend fun getPodsByAddress(
        @Path("address") address: String
    ): FlowPodsByAddressResponse

    // ==================== Account & Balance ====================

    /**
     * Get Flow balance for an address
     * GET /api/flow/balance/:address
     */
    @GET("api/flow/balance/{address}")
    suspend fun getFlowBalance(
        @Path("address") address: String
    ): FlowBalanceResponse

    // ==================== Transfer & Deposit ====================

    /**
     * Transfer between pods
     * POST /api/flow/transfer
     */
    @POST("api/flow/transfer")
    suspend fun transferBetweenPods(
        @Body request: FlowTransferRequest
    ): ApiResponse<String>

    /**
     * Deposit to pod
     * POST /api/flow/deposit
     */
    @POST("api/flow/deposit")
    suspend fun depositToPod(
        @Body request: FlowDepositRequest
    ): ApiResponse<String>

    // ==================== Distribution & Disbursement ====================

    /**
     * Distribute pod funds (equal share)
     * POST /api/flow/distribute
     */
    @POST("api/flow/distribute")
    suspend fun distributePodFunds(
        @Body request: FlowDistributeRequest
    ): ApiResponse<String>

    /**
     * Execute pod disbursement (sink payouts)
     * POST /api/flow/disburse
     */
    @POST("api/flow/disburse")
    suspend fun disbursePod(
        @Body request: FlowDisburseRequest
    ): ApiResponse<String>

    // ==================== Forte Payments ====================

    /**
     * Create scheduled payment
     * POST /api/forte/scheduled-payments
     */
    @POST("api/forte/scheduled-payments")
    suspend fun createScheduledPayment(
        @Body request: ForteScheduleRequest
    ): ApiResponse<ForteScheduleResponse>

    /**
     * Get scheduled payments by pod
     * GET /api/forte/scheduled-payments?podID=:podID
     */
    @GET("api/forte/scheduled-payments")
    suspend fun getScheduledPayments(
        @Query("podID") podID: Int
    ): ApiResponse<List<ForteScheduleResponse>>

    /**
     * Get specific scheduled payment
     * GET /api/forte/scheduled-payments/:scheduleID
     */
    @GET("api/forte/scheduled-payments/{scheduleID}")
    suspend fun getScheduledPayment(
        @Path("scheduleID") scheduleID: String
    ): ApiResponse<ForteScheduleResponse>

    /**
     * Cancel scheduled payment
     * DELETE /api/forte/scheduled-payments/:scheduleID
     */
    @DELETE("api/forte/scheduled-payments/{scheduleID}")
    suspend fun cancelScheduledPayment(
        @Path("scheduleID") scheduleID: String
    ): ApiResponse<String>

    /**
     * Get members of a specific pod
     * @param podId Pod ID
     * @return List of members
     */
    @GET("api/pods/{podId}/members")
    suspend fun getPodMembers(
        @Path("podId") podId: Long
    ): List<Member>

    /**
     * Get transaction history/activity for a pod
     * @param podId Pod ID
     * @param limit Optional limit for number of transactions (default: 50)
     * @param offset Optional offset for pagination (default: 0)
     * @return API response with list of transactions
     */
    @GET("api/flow/pods/{podId}/transactions")
    suspend fun getPodTransactions(
        @Path("podId") podId: Long,
        @Query("limit") limit: Int? = 50,
        @Query("offset") offset: Int? = 0
    ): ApiResponse<List<TransactionItem>>

    /**
     * Deposit funds to a pod (alternate endpoint)
     * @param podId Pod ID
     * @param amount Amount to deposit
     * @param signerAddress Address making the deposit
     * @return API response with deposit status
     */
    @POST("api/pods/{podId}/deposit")
    suspend fun depositToPod(
        @Path("podId") podId: Long,
        @Query("amount") amount: Double,
        @Query("signerAddress") signerAddress: String
    ): ApiResponse<String>
}
