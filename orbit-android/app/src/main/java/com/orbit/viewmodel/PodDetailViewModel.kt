package com.orbit.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.data.models.ErrorType
import com.orbit.data.models.Member
import com.orbit.data.models.Pod
import com.orbit.data.models.Resource
import com.orbit.data.models.TransactionItem
import com.orbit.data.repository.PodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Pod Details Screen
 * Manages pod details, members, activity, and actions
 */

@HiltViewModel
class PodDetailViewModel @Inject constructor(
    private val podRepository: PodRepository
) : ViewModel() {

    companion object {
        private const val TAG = "PodDetailViewModel"
    }

    // Pod details state
    private val _podState = MutableStateFlow<Resource<Pod>>(Resource.Loading())
    val podState: StateFlow<Resource<Pod>> = _podState.asStateFlow()

    // Members list state
    private val _membersState = MutableStateFlow<Resource<List<Member>>>(Resource.Loading())
    val membersState: StateFlow<Resource<List<Member>>> = _membersState.asStateFlow()

    // Activity/Transactions state
    private val _activityState = MutableStateFlow<Resource<List<TransactionItem>>>(Resource.Loading())
    val activityState: StateFlow<Resource<List<TransactionItem>>> = _activityState.asStateFlow()

    // Schedules state
    private val _schedulesState = MutableStateFlow<Resource<List<Any>>>(Resource.Loading())
    val schedulesState: StateFlow<Resource<List<Any>>> = _schedulesState.asStateFlow()

    // Current pod ID
    private val _currentPodId = MutableStateFlow<Long?>(null)
    val currentPodId: StateFlow<Long?> = _currentPodId.asStateFlow()

    // User's wallet address
    private val _walletAddress = MutableStateFlow<String?>(null)
    val walletAddress: StateFlow<String?> = _walletAddress.asStateFlow()

    // Action states for bottom sheets
    private val _showDepositSheet = MutableStateFlow(false)
    val showDepositSheet: StateFlow<Boolean> = _showDepositSheet.asStateFlow()

    private val _showScanPaySheet = MutableStateFlow(false)
    val showScanPaySheet: StateFlow<Boolean> = _showScanPaySheet.asStateFlow()

    private val _showScheduleSheet = MutableStateFlow(false)
    val showScheduleSheet: StateFlow<Boolean> = _showScheduleSheet.asStateFlow()

    private val _showDistributeSheet = MutableStateFlow(false)
    val showDistributeSheet: StateFlow<Boolean> = _showDistributeSheet.asStateFlow()

    private val _showLeaveSheet = MutableStateFlow(false)
    val showLeaveSheet: StateFlow<Boolean> = _showLeaveSheet.asStateFlow()

    // Loading states for actions
    private val _depositLoading = MutableStateFlow(false)
    val depositLoading: StateFlow<Boolean> = _depositLoading.asStateFlow()

    private val _scanPayLoading = MutableStateFlow(false)
    val scanPayLoading: StateFlow<Boolean> = _scanPayLoading.asStateFlow()

    private val _scheduleLoading = MutableStateFlow(false)
    val scheduleLoading: StateFlow<Boolean> = _scheduleLoading.asStateFlow()

    private val _distributeLoading = MutableStateFlow(false)
    val distributeLoading: StateFlow<Boolean> = _distributeLoading.asStateFlow()

    /**
     * Set wallet address (called from UI when screen is opened)
     * @param address User's wallet address from parent ViewModel
     */
    fun setWalletAddress(address: String) {
        _walletAddress.value = address
        Log.d(TAG, "Wallet address set: $address")
    }

    /**
     * Load pod details and related data
     * @param podId Pod ID to load
     */
    fun loadPodDetails(podId: Long) {
        _currentPodId.value = podId
        Log.d(TAG, "📡 Loading details for pod: $podId")

        // Load pod details
        loadPod(podId)

        // Load members (mock for now)
        loadMembers(podId)

        // Load activity (mock for now)
        loadActivity(podId)

        // Load schedules (mock for now)
        loadSchedules(podId)
    }


     // Load pod details from repository
    private fun loadPod(podId: Long) {
        viewModelScope.launch {
            podRepository.getPodDetails(podId).collect { resource ->
                _podState.value = resource

                when (resource) {
                    is Resource.Success -> {
                        Log.d(TAG, "Pod details loaded: ${resource.data?.name}")
                        Log.d(TAG, "Balance: ${resource.data?.balance}")
                        Log.d(TAG, "Members: ${resource.data?.memberCount}")
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "Error loading pod: ${resource.message}")
                    }
                    is Resource.Loading -> {
                        Log.d(TAG, "Loading pod details...")
                    }
                }
            }
        }
    }

    /**
     * Load pod members (mock implementation for now)
     * TODO: Replace with actual API call when backend endpoint is ready
     */
    private fun loadMembers(podId: Long) {
        viewModelScope.launch {
            try {
                // Mock members data
                val mockMembers = listOf(
                    Member(
                        address = "0x123abc...def",
                        role = "CREATOR",
                        contributed = 500.0,
                        displayName = "John Doe",
                        joinedAt = "2025-11-01T10:00:00Z"
                    ),
                    Member(
                        address = "0x456def...ghi",
                        role = "MEMBER",
                        contributed = 300.0,
                        displayName = "Jane Smith",
                        joinedAt = "2025-11-02T12:00:00Z"
                    ),
                    Member(
                        address = "0x789ghi...jkl",
                        role = "ADMIN",
                        contributed = 200.0,
                        displayName = "Bob Wilson",
                        joinedAt = "2025-11-03T14:00:00Z"
                    )
                )

                _membersState.value = Resource.Success(mockMembers)
                Log.d(TAG, "Members loaded: ${mockMembers.size}")
            } catch (e: Exception) {
                _membersState.value = Resource.Error("Failed to load members")
                Log.e(TAG, "Error loading members: ${e.message}")
            }
        }
    }

    /**
     * Load pod activity/transactions from backend
     */
    private fun loadActivity(podId: Long) {
        viewModelScope.launch {
            podRepository.getTransactions(podId).collect { resource ->
                _activityState.value = resource
                when (resource) {
                    is Resource.Success -> {
                        Log.d(TAG, "Activity loaded: ${resource.data?.size ?: 0} transactions")
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "Error loading activity: ${resource.message}")
                    }
                    is Resource.Loading -> {
                        Log.d(TAG, "Loading activity...")
                    }
                }
            }
        }
    }

    /**
     * Load pod schedules (mock implementation for now)
     * TODO: Replace with actual API call when backend endpoint is ready
     */
    private fun loadSchedules(podId: Long) {
        viewModelScope.launch {
            try {
                // Mock schedules data
                val mockSchedules = emptyList<Any>()

                _schedulesState.value = Resource.Success(mockSchedules)
                Log.d(TAG, "Schedules loaded: ${mockSchedules.size}")
            } catch (e: Exception) {
                _schedulesState.value = Resource.Error("Failed to load schedules")
                Log.e(TAG, "Error loading schedules: ${e.message}")
            }
        }
    }

    /**
     * Refresh all pod data
     */
    fun refresh() {
        _currentPodId.value?.let { podId ->
            Log.d(TAG, "Refreshing pod data...")
            loadPodDetails(podId)
        }
    }

    // ========== Action Functions ==========

    /**
     * Show deposit bottom sheet
     */
    fun showDepositSheet() {
        Log.d(TAG, "Opening deposit sheet")
        _showDepositSheet.value = true
    }

    fun hideDepositSheet() {
        _showDepositSheet.value = false
    }

    /**
     * Deposit funds to pod
     * @param amount Amount to deposit
     */
    fun deposit(amount: Double) {
        viewModelScope.launch {
            _currentPodId.value?.let { podId ->
                Log.d(TAG, "Depositing $amount to pod $podId")

                // TODO: Implement depositToPod in PodRepository
                // For now, just log and hide the sheet
                Log.w(TAG, "Deposit not implemented - depositToPod missing in repository")
                _depositLoading.value = false
                hideDepositSheet()
            }
        }
    }

    /**
     * Show scan and pay bottom sheet
     */
    fun showScanPaySheet() {
        Log.d(TAG, "Opening scan & pay modal")
        _showScanPaySheet.value = true
    }

    fun hideScanPaySheet() {
        _showScanPaySheet.value = false
    }

    /**
     * Process payment after QR scan
     * @param qrCode Scanned QR code data (contains receiver pod ID)
     * @param amount Amount to pay
     */
    fun scanAndPay(qrCode: String, amount: Double) {
        viewModelScope.launch {
            _currentPodId.value?.let { payerPodId ->
                Log.d(TAG, "📱 Scan & Pay: $amount from pod $payerPodId")
                Log.d(TAG, "  QR Data: $qrCode")

                // Extract receiver pod ID from QR code
                // Format: "POD_123" or "flowpods://pay?podId=123" or just "123"
                val receiverPodId = try {
                    when {
                        qrCode.startsWith("POD_") -> qrCode.substringAfter("POD_").toLongOrNull()
                        qrCode.contains("podId=") -> qrCode.substringAfter("podId=").substringBefore("&").toLongOrNull()
                        else -> qrCode.toLongOrNull()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to parse receiver pod ID from QR: ${e.message}")
                    null
                }

                if (receiverPodId == null) {
                    Log.e(TAG, "Invalid QR code format")
                    // TODO: Show error to user
                    hideScanPaySheet()
                    return@launch
                }

                Log.d(TAG, "  Receiver Pod ID: $receiverPodId")

                podRepository.makeImmediatePayment(
                    payerPodId = payerPodId,
                    receiverPodId = receiverPodId,
                    amount = amount
                ).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            Log.d(TAG, "Payment successful")
                            Log.d(TAG, "  Transaction ID: ${resource.data?.transactionId}")
                            _scanPayLoading.value = false
                            hideScanPaySheet()
                            refresh() // Refresh to show updated balance
                        }
                        is Resource.Error -> {
                            Log.e(TAG, "Payment failed: ${resource.message}")
                            _scanPayLoading.value = false
                            // TODO: Show error to user
                        }
                        is Resource.Loading -> {
                            Log.d(TAG, "⏳ Processing payment...")
                            _scanPayLoading.value = true
                        }
                    }
                }
            }
        }
    }

    /**
     * Show schedule payout bottom sheet
     */
    fun showScheduleSheet() {
        Log.d(TAG, "📅 Opening schedule payout sheet")
        _showScheduleSheet.value = true
    }

    fun hideScheduleSheet() {
        _showScheduleSheet.value = false
    }

    /**
     * Schedule a recurring payout
     * Uses Forte API for scheduled payments
     *
     * @param amount Amount per payout
     * @param frequency Payment frequency (DAILY, WEEKLY, BIWEEKLY, MONTHLY)
     * @param startDate Start date (not used by Forte API currently)
     * @param endDate End date (not used by Forte API currently)
     */
    fun schedulePayout(
        amount: Double,
        frequency: String,
        startDate: String,
        endDate: String? = null
    ) {
        viewModelScope.launch {
            _currentPodId.value?.let { podId ->
                Log.d(TAG, "📅 Scheduling payout: $amount $frequency from pod $podId")

                // Convert frequency to interval days for Forte API
                val intervalDays = when (frequency.uppercase()) {
                    "DAILY" -> 1
                    "WEEKLY" -> 7
                    "BIWEEKLY" -> 14
                    "MONTHLY" -> 30
                    else -> 7 // Default to weekly
                }

                Log.d(TAG, "  Interval days: $intervalDays")

                // Use Forte API - for now, use same pod as both payer and receiver
                // TODO: Add UI to select receiver pod
                podRepository.createForteSchedule(
                    payerPodId = podId,
                    receiverPodId = podId, // Self-scheduled payment for now
                    amount = amount,
                    intervalDays = intervalDays
                ).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            Log.d(TAG, "Schedule created successfully")
                            Log.d(TAG, "  Schedule ID: ${resource.data?.scheduleID}")
                            _scheduleLoading.value = false
                            hideScheduleSheet()
                            refresh()
                        }
                        is Resource.Error -> {
                            Log.e(TAG, "Schedule failed: ${resource.message}")
                            _scheduleLoading.value = false
                        }
                        is Resource.Loading -> {
                            Log.d(TAG, "Creating schedule...")
                            _scheduleLoading.value = true
                        }
                    }
                }
            }
        }
    }

    /**
     * Show distribute funds bottom sheet
     */
    fun showDistributeSheet() {
        Log.d(TAG, "Opening distribute funds sheet")
        _showDistributeSheet.value = true
    }

    fun hideDistributeSheet() {
        _showDistributeSheet.value = false
    }

    /**
     * Distribute funds to all pod members
     * Supports both equal and custom distribution modes
     * @param mode Distribution mode: "EQUAL" or "CUSTOM"
     * @param customDistributions Optional map of member address to amount (for CUSTOM mode)
     */
    fun distributeNow(mode: String = "EQUAL", customDistributions: Map<String, Double>? = null) {
        viewModelScope.launch {
            _currentPodId.value?.let { podId ->
                val address = _walletAddress.value
                if (address == null) {
                    Log.w(TAG, "⚠️ Cannot distribute: No wallet address")
                    _distributeLoading.value = false
                    return@launch
                }

                Log.d(TAG, "💸 Distributing funds from pod $podId (mode: $mode)")

                when (mode) {
                    "EQUAL" -> {
                        // Use the simple distributeFunds endpoint (backend handles equal split)
                        podRepository.distributeFunds(podId, address).collect { resource ->
                            when (resource) {
                                is Resource.Success -> {
                                    Log.d(TAG, "Distribution successful")
                                    _distributeLoading.value = false
                                    hideDistributeSheet()
                                    refresh()
                                }
                                is Resource.Error -> {
                                    Log.e(TAG, "Distribution failed: ${resource.message}")
                                    _distributeLoading.value = false
                                }
                                is Resource.Loading -> {
                                    Log.d(TAG, "⏳ Processing distribution...")
                                    _distributeLoading.value = true
                                }
                            }
                        }
                    }
                    "CUSTOM" -> {
                        if (customDistributions.isNullOrEmpty()) {
                            Log.e(TAG, "Custom distributions not provided")
                            return@launch
                        }

                        // Use custom distribution method
                        podRepository.distributeCustomAmounts(podId, customDistributions, address).collect { resource ->
                            when (resource) {
                                is Resource.Success -> {
                                    Log.d(TAG, "Custom distribution successful: ${resource.data}")
                                    _distributeLoading.value = false
                                    hideDistributeSheet()
                                    refresh()
                                }
                                is Resource.Error -> {
                                    Log.e(TAG, "Custom distribution failed: ${resource.message}")
                                    _distributeLoading.value = false
                                }
                                is Resource.Loading -> {
                                    Log.d(TAG, "Processing custom distribution...")
                                    _distributeLoading.value = true
                                }
                            }
                        }
                    }
                    else -> {
                        Log.e(TAG, "Invalid distribution mode: $mode")
                    }
                }
            }
        }
    }

    /**
     * Show leave pod bottom sheet
     */
    fun showLeaveSheet() {
        Log.d(TAG, "Opening leave pod sheet")
        _showLeaveSheet.value = true
    }

    fun hideLeaveSheet() {
        _showLeaveSheet.value = false
    }

    /**
     * Leave pod (remove current user from pod)
     */
    fun leavePod(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _currentPodId.value?.let { podId ->
                Log.d(TAG, "Leaving pod $podId")

                // TODO: Implement actual leave pod logic when backend is ready
                // For now, just log and call success callback
                hideLeaveSheet()
                onSuccess()
            }
        }
    }

    /**
     * Check if current user is pod creator
     */
    fun isCreator(): Boolean {
        val pod = (_podState.value as? Resource.Success)?.data
        val userAddress = _walletAddress.value
        return pod?.creator == userAddress
    }

    /**
     * Check if current user is pod admin or creator
     */
    fun isAdminOrCreator(): Boolean {
        // TODO: Check member role from members list
        return isCreator()
    }
}

