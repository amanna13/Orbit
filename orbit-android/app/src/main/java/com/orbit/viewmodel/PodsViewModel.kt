package com.orbit.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.auth.Web3AuthManager
import com.orbit.data.models.CreatePodRequest
import com.orbit.data.models.Pod
import com.orbit.data.models.Resource
import com.orbit.data.repository.PodRepository
import com.orbit.network.models.ScheduleRequest
import com.orbit.network.models.TransferRequest
import com.orbit.network.models.TriggerRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing Pod operations
 * Uses Hilt for dependency injection
 */
@HiltViewModel
class PodsViewModel @Inject constructor(
    private val podRepository: PodRepository,
    private val web3AuthManager: Web3AuthManager
) : ViewModel() {

    companion object {
        private const val TAG = "PodsViewModel"
    }

    // User wallet address (loaded once on init)
    private val _walletAddress = MutableStateFlow<String?>(null)
    val walletAddress: StateFlow<String?> = _walletAddress.asStateFlow()

    // State for pods list
    private val _podsState = MutableStateFlow<Resource<List<Pod>>>(Resource.Loading())
    val podsState: StateFlow<Resource<List<Pod>>> = _podsState.asStateFlow()

    // State for single pod details
    private val _podDetailsState = MutableStateFlow<Resource<Pod>?>(null)
    val podDetailsState: StateFlow<Resource<Pod>?> = _podDetailsState.asStateFlow()

    // State for create pod operation
    private val _createPodState = MutableStateFlow<Resource<Pod>?>(null)
    val createPodState: StateFlow<Resource<Pod>?> = _createPodState.asStateFlow()

    // State for join pod operation
    private val _joinPodState = MutableStateFlow<Resource<Pod>?>(null)
    val joinPodState: StateFlow<Resource<Pod>?> = _joinPodState.asStateFlow()

    init {
        // Load wallet address on init
        loadWalletAddress()
    }

    /**
     * Load wallet address from Web3AuthManager (called once on init)
     */
    private fun loadWalletAddress() {
        viewModelScope.launch {
            try {
                val address = web3AuthManager.getCurrentAddress()
                _walletAddress.value = address
                Log.d(TAG, "Wallet address loaded: $address")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading wallet address: ${e.message}")
            }
        }
    }

    /**
     * Fetch all pods for current user
     * Uses wallet address stored in ViewModel
     */
    fun getPods() {
        viewModelScope.launch {
            val address = _walletAddress.value
            if (address == null) {
                Log.w(TAG, "⚠️ Cannot fetch pods: No wallet address")
                _podsState.value = Resource.Error("Please connect your wallet first")
                return@launch
            }

            podRepository.getPods(address).collect { resource ->
                _podsState.value = resource
                when (resource) {
                    is Resource.Success -> {
                        Log.d(TAG, "getPods SUCCESS: ${resource.data?.size} pods found")
                        resource.data?.forEach { pod ->
                            Log.d(TAG, "  Pod: ${pod.name} (ID: ${pod.id}, Balance: ${pod.balance})")
                        }
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "getPods ERROR: ${resource.message}")
                    }
                    is Resource.Loading -> {
                        Log.d(TAG, "getPods LOADING...")
                    }
                }
            }
        }
    }

    /**
     * Fetch details of a specific pod
     * @param podId Pod ID
     */
    fun getPodDetails(podId: Long) {
        viewModelScope.launch {
            podRepository.getPodDetails(podId).collect { resource ->
                _podDetailsState.value = resource
                when (resource) {
                    is Resource.Success -> {
                        Log.d(TAG, "getPodDetails SUCCESS: ${resource.data?.name}")
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "getPodDetails ERROR: ${resource.message}")
                    }
                    is Resource.Loading -> {
                        Log.d(TAG, "getPodDetails LOADING...")
                    }
                }
            }
        }
    }

    /**
     * Create a new pod
     * Uses wallet address stored in ViewModel
     *
     * @param name Pod name
     * @param description Pod description (optional)
     */
    fun createPod(name: String, description: String? = null) {
        viewModelScope.launch {
            val address = _walletAddress.value
            if (address == null) {
                Log.w(TAG, "Cannot create pod: No wallet address")
                _createPodState.value = Resource.Error("Please connect your wallet first")
                return@launch
            }

            podRepository.createPod(name, address, description).collect { resource ->
                _createPodState.value = resource
                when (resource) {
                    is Resource.Success -> {
                        Log.d(TAG, "createPod SUCCESS: ${resource.data?.name} created with join code: ${resource.data?.joinCode}")
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "createPod ERROR: ${resource.message}")
                    }
                    is Resource.Loading -> {
                        Log.d(TAG, "createPod LOADING...")
                    }
                }
            }
        }
    }

    /**
     * Join an existing pod
     * Uses wallet address stored in ViewModel
     *
     * @param joinCode Join code from QR (format: ABCD1234 or flowpods://join?code=XXXX)
     */
    fun joinPod(joinCode: String) {
        viewModelScope.launch {
            val address = _walletAddress.value
            if (address == null) {
                Log.w(TAG, "Cannot join pod: No wallet address")
                _joinPodState.value = Resource.Error("Please connect your wallet first")
                return@launch
            }

            podRepository.joinPod(joinCode, address).collect { resource ->
                _joinPodState.value = resource
                when (resource) {
                    is Resource.Success -> {
                        Log.d(TAG, "joinPod SUCCESS: Joined pod ${resource.data?.name}")
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "joinPod ERROR: ${resource.message}")
                    }
                    is Resource.Loading -> {
                        Log.d(TAG, "joinPod LOADING...")
                    }
                }
            }
        }
    }

    /**
     * Transfer funds between pods
     * Uses wallet address stored in ViewModel
     */
    fun transferBetweenPods(fromPodId: Long, toPodId: Long, amount: Double) {
        viewModelScope.launch {
            val address = _walletAddress.value
            if (address == null) {
                Log.w(TAG, "Cannot transfer: No wallet address")
                return@launch
            }

            podRepository.transferBetweenPods(fromPodId, toPodId, amount, address).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        Log.d(TAG, "transferBetweenPods SUCCESS: ${resource.data}")
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "transferBetweenPods ERROR: ${resource.message}")
                    }
                    is Resource.Loading -> {
                        Log.d(TAG, "transferBetweenPods LOADING...")
                    }
                }
            }
        }
    }

    /**
     * Schedule a recurring payment
     * Uses wallet address stored in ViewModel
     */
    fun schedulePayment(
        podId: Long,
        amount: Double,
        frequency: String,
        startDate: String,
        endDate: String? = null,
        recipients: List<String>? = null
    ) {
        viewModelScope.launch {
            val address = _walletAddress.value
            if (address == null) {
                Log.w(TAG, "Cannot schedule payment: No wallet address")
                return@launch
            }

            podRepository.createSchedule(podId, amount, frequency, address, startDate, endDate, recipients).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        Log.d(TAG, "schedulePayment SUCCESS: ${resource.data}")
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "schedulePayment ERROR: ${resource.message}")
                    }
                    is Resource.Loading -> {
                        Log.d(TAG, "schedulePayment LOADING...")
                    }
                }
            }
        }
    }

    /**
     * Trigger an immediate payment
     * Uses wallet address stored in ViewModel
     */
    fun triggerPayment(podId: Long, amount: Double, recipients: List<String>) {
        viewModelScope.launch {
            val address = _walletAddress.value
            if (address == null) {
                Log.w(TAG, "Cannot trigger payment: No wallet address")
                return@launch
            }

            podRepository.triggerSchedule(podId, amount, recipients, address).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        Log.d(TAG, "triggerPayment SUCCESS: ${resource.data}")
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "triggerPayment ERROR: ${resource.message}")
                    }
                    is Resource.Loading -> {
                        Log.d(TAG, "triggerPayment LOADING...")
                    }
                }
            }
        }
    }

    /**
     * Distribute pod funds to all members
     * Uses wallet address stored in ViewModel
     */
    fun distributeToMembers(podId: Long) {
        viewModelScope.launch {
            val address = _walletAddress.value
            if (address == null) {
                Log.w(TAG, "Cannot distribute funds: No wallet address")
                return@launch
            }

            podRepository.distributeFunds(podId, address).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        Log.d(TAG, "distributeToMembers SUCCESS: ${resource.data}")
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "distributeToMembers ERROR: ${resource.message}")
                    }
                    is Resource.Loading -> {
                        Log.d(TAG, "distributeToMembers LOADING...")
                    }
                }
            }
        }
    }

    /**
     * Deposit funds to a pod
     * TODO: Implement depositToPod in PodRepository
     */
    /*
    fun depositToPod(podId: Long, amount: Double) {
        viewModelScope.launch {
            podRepository.depositToPod(podId, amount).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        Log.d(TAG, "depositToPod SUCCESS: ${resource.data}")
                    }
                    is Resource.Error -> {
                        Log.e(TAG, depositToPod ERROR: ${resource.message}")
                    }
                    is Resource.Loading -> {
                        Log.d(TAG, "depositToPod LOADING...")
                    }
                }
            }
        }
    }
    */

    /**
     * Test API call - fetches pods and logs result
     * Call this from UI to test backend connection
     */
    fun testApiConnection() {
        Log.d(TAG, "Testing API connection...")
        getPods()
    }
}

