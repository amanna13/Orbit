package com.orbit.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.auth.Web3AuthManager
import com.orbit.data.models.ErrorType
import com.orbit.data.models.Pod
import com.orbit.data.models.Resource
import com.orbit.data.repository.PodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Pod List (Home Screen)
 * Manages pods display, loading, and error states
 */
@HiltViewModel
class PodListViewModel @Inject constructor(
    private val podRepository: PodRepository,
    private val web3AuthManager: Web3AuthManager
) : ViewModel() {

    companion object {
        private const val TAG = "PodListViewModel"
    }

    // Pods list state
    private val _podsState = MutableStateFlow<Resource<List<Pod>>>(Resource.Loading())
    val podsState: StateFlow<Resource<List<Pod>>> = _podsState.asStateFlow()

    // Loading state for pull-to-refresh
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // User wallet address
    private val _walletAddress = MutableStateFlow<String?>(null)
    val walletAddress: StateFlow<String?> = _walletAddress.asStateFlow()

    // User info states
    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail.asStateFlow()

    // Flow balance state
    private val _flowBalance = MutableStateFlow<Double?>(null)
    val flowBalance: StateFlow<Double?> = _flowBalance.asStateFlow()

    init {
        // Load user info on init
        loadUserInfo()
        // Auto-load pods
        refreshPods()
        // Load Flow balance
        loadFlowBalance()
    }

    /**
     * Load user information from Web3AuthManager
     */
    private fun loadUserInfo() {
        viewModelScope.launch {
            try {
                val address = web3AuthManager.getCurrentAddress()
                _walletAddress.value = address

                val email = web3AuthManager.getUserEmail()
                _userEmail.value = email

                val name = web3AuthManager.getUserName()
                _userName.value = name

                Log.d(TAG, "User info loaded")
                Log.d(TAG, "  Address: $address")
                Log.d(TAG, "  Email: $email")
                Log.d(TAG, "  Name: $name")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user info: ${e.message}")
            }
        }
    }

    /**
     * Refresh pods from backend
     * Uses wallet address stored in ViewModel
     */
    fun refreshPods() {
        viewModelScope.launch {
            val address = _walletAddress.value
            if (address == null) {
                Log.w(TAG, "Cannot refresh pods: No wallet address")
                _podsState.value = Resource.Error(
                    "Please connect your wallet first",
                    errorType = ErrorType.AUTHENTICATION
                )
                return@launch
            }

            Log.d(TAG, "Refreshing pods for: $address")
            _isRefreshing.value = true

            podRepository.getPods(address).collect { resource ->
                _podsState.value = resource
                _isRefreshing.value = false

                when (resource) {
                    is Resource.Success -> {
                        val count = resource.data?.size ?: 0
                        Log.d(TAG, "Pods refreshed: $count pods")
                        resource.data?.forEach { pod ->
                            Log.d(TAG, "  - ${pod.name} (Balance: ${pod.balance})")
                        }
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "Error refreshing pods: ${resource.message}")
                        Log.e(TAG, "  Error type: ${resource.errorType}")
                    }
                    is Resource.Loading -> {
                        Log.d(TAG, "Loading pods...")
                    }
                }
            }
        }
    }

    /**
     * Handle "Create Pod" button click
     * Triggers navigation to create pod dialog
     */
    fun onCreatePodClicked(): Boolean {
        val address = _walletAddress.value
        if (address == null) {
            Log.w(TAG, "Cannot create pod: User not authenticated")
            return false
        }

        Log.d(TAG, "Create Pod clicked by: $address")
        return true // Return true to show dialog
    }

    /**
     * Handle "Join Pod" button click
     * Triggers navigation to join pod scanner
     */
    fun onJoinClicked(): Boolean {
        val address = _walletAddress.value
        if (address == null) {
            Log.w(TAG, "Cannot join pod: User not authenticated")
            return false
        }

        Log.d(TAG, "Join Pod clicked by: $address")
        return true // Return true to show scanner
    }

    /**
     * Check if user is authenticated
     */
    fun isAuthenticated(): Boolean {
        return _walletAddress.value != null
    }

    /**
     * Get user-friendly error message based on error type
     */
    fun getErrorMessage(resource: Resource<*>): String {
        if (resource !is Resource.Error) return ""

        return when (resource.errorType) {
            ErrorType.NETWORK -> "No internet connection. Please check your network and try again."
            ErrorType.AUTHENTICATION -> "Please connect your wallet to view pods."
            ErrorType.VALIDATION -> resource.message ?: "Invalid input. Please try again."
            ErrorType.SERVER -> "Server error: ${resource.message}. Please try again later."
            ErrorType.TIMEOUT -> "Request timed out. Please try again."
            ErrorType.UNKNOWN -> resource.message ?: "An unknown error occurred."
        }
    }

    /**
     * Retry loading pods (for error state)
     */
    fun retryLoadPods() {
        Log.d(TAG, "Retrying pod load...")
        refreshPods()
    }

    /**
     * Load Flow balance for current user
     * Uses wallet address stored in ViewModel
     */
    fun loadFlowBalance() {
        viewModelScope.launch {
            try {
                val address = _walletAddress.value
                if (address == null) {
                    Log.w(TAG, "Cannot load Flow balance: No wallet address")
                    return@launch
                }

                Log.d(TAG, "Fetching Flow balance for: $address")
                podRepository.getFlowBalance(address).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _flowBalance.value = resource.data
                            Log.d(TAG, "Flow balance: ${resource.data} FLOW")
                        }
                        is Resource.Error -> {
                            Log.e(TAG, "Error fetching Flow balance: ${resource.message}")
                        }
                        is Resource.Loading -> {
                            Log.d(TAG, "⏳ Loading Flow balance...")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading Flow balance: ${e.message}")
            }
        }
    }
}
