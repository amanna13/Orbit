package com.orbit.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.auth.UserPreferences
import com.orbit.auth.Web3AuthManager
import com.web3auth.core.types.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for authentication and user session management
 * Integrates with Web3AuthManager for wallet address persistence
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val web3AuthManager: Web3AuthManager
) : ViewModel() {

    companion object {
        private const val TAG = "AuthViewModel"
    }

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo.asStateFlow()

    private val _userPreferences = MutableStateFlow<UserPreferences?>(null)
    val userPreferences: StateFlow<UserPreferences?> = _userPreferences.asStateFlow()

    private val _walletAddress = MutableStateFlow<String?>(null)
    val walletAddress: StateFlow<String?> = _walletAddress.asStateFlow()

    init {
        // Load user preferences on init
        viewModelScope.launch {
            web3AuthManager.getUserPreferences().collect { prefs ->
                _userPreferences.value = prefs
                _walletAddress.value = prefs.walletAddress
                Log.d(TAG, "User preferences loaded: ${prefs.walletAddress}")
            }
        }
    }

    /**
     * Set user info after Web3Auth login
     * Automatically saves to DataStore via Web3AuthManager
     */
    fun setUserInfo(userInfo: UserInfo?) {
        _userInfo.value = userInfo

        if (userInfo != null) {
            viewModelScope.launch {
                // Save to DataStore
                web3AuthManager.saveUserInfo(userInfo)

                // Update wallet address
                val address = web3AuthManager.getCurrentAddress()
                _walletAddress.value = address

                Log.d(TAG, " User authenticated")
                Log.d(TAG, " Email: ${userInfo.email}")
                Log.d(TAG, " Address: $address")
            }
        }
    }

    /**
     * Logout user and clear session
     */
    fun logout(onLogout: () -> Unit) {
        viewModelScope.launch {
            web3AuthManager.logout()
            _userInfo.value = null
            _walletAddress.value = null
            _userPreferences.value = null
            Log.d(TAG, " User logged out")
            onLogout()
        }
    }

    /**
     * Get current wallet address
     */
    suspend fun getCurrentAddress(): String? {
        return web3AuthManager.getCurrentAddress()
    }

    /**
     * Check if user is authenticated
     */
    suspend fun isAuthenticated(): Boolean {
        return web3AuthManager.isAuthenticated()
    }

    /**
     * Set Web3Auth instance (call after Web3Auth initialization)
     */
    fun setWeb3AuthInstance(web3Auth: com.web3auth.core.Web3Auth) {
        web3AuthManager.setWeb3AuthInstance(web3Auth)
    }
}
