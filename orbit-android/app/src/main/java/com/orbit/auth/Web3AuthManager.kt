package com.orbit.auth

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.web3auth.core.Web3Auth
import com.web3auth.core.types.UserInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Extension property to create DataStore instance
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Web3Auth Manager
 * Manages wallet session, user authentication, and address persistence
 *
 * Features:
 * - Get current wallet address
 * - Sign messages with Web3Auth
 * - Persist user data to DataStore
 * - Logout functionality
 */
@Singleton
class Web3AuthManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "Web3AuthManager"

        // DataStore keys
        private val KEY_WALLET_ADDRESS = stringPreferencesKey("wallet_address")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
    }

    // Reference to Web3Auth instance (set from UI/Activity)
    private var web3AuthInstance: Web3Auth? = null

    // Cached user info
    private var cachedUserInfo: UserInfo? = null

    /**
     * Set Web3Auth instance (call from Activity/Screen after initialization)
     */
    fun setWeb3AuthInstance(web3Auth: Web3Auth) {
        this.web3AuthInstance = web3Auth
        this.cachedUserInfo = web3Auth.getUserInfo()
        Log.d(TAG, "Web3Auth instance set. User info: ${cachedUserInfo?.email}")
    }

    /**
     * Get current wallet address
     * Priority: Web3Auth -> DataStore cache
     *
     * @return Wallet address or null if not authenticated
     */
    suspend fun getCurrentAddress(): String? {
        val web3flow = Web3j.build(HttpService("https://testnet.evm.nodes.onflow.org"))

        // Try from Web3Auth first
        val web3Address = web3AuthInstance?.getUserInfo()?.let { userInfo ->
            extractAddressFromUserInfo(userInfo)
        }

        val privateKey = web3AuthInstance?.getPrivkey()
        println (web3flow.ethAccounts())

        println("Web3AuthManager - getCurrentAddress: Web3Auth address = $web3Address")

        if (web3Address != null) {
            return web3Address
        }

        // Fallback to DataStore
        return context.dataStore.data.map { preferences ->
            preferences[KEY_WALLET_ADDRESS]
        }.first()
    }

    /**
     * Get current wallet address as Flow (reactive)
     */
    fun getCurrentAddressFlow(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_WALLET_ADDRESS]
        }
    }

    /**
     * Get current user info from Web3Auth
     */
    fun getCurrentUserInfo(): UserInfo? {
        return cachedUserInfo ?: web3AuthInstance?.getUserInfo()
    }

    /**
     * Check if user is authenticated
     */
    suspend fun isAuthenticated(): Boolean {
        return getCurrentAddress() != null
    }

    /**
     * Save user info to DataStore after successful authentication
     * Call this after Web3Auth login succeeds
     */
    suspend fun saveUserInfo(userInfo: UserInfo) {
        cachedUserInfo = userInfo
        val address = extractAddressFromUserInfo(userInfo)

        context.dataStore.edit { preferences ->
            address?.let { preferences[KEY_WALLET_ADDRESS] = it }
            userInfo.email?.let { preferences[KEY_USER_EMAIL] = it }
            userInfo.name?.let { preferences[KEY_USER_NAME] = it }
            userInfo.aggregateVerifier?.let { preferences[KEY_USER_ID] = it }
        }

        Log.d(TAG, "User info saved to DataStore")
        Log.d(TAG, "  Address: $address")
        Log.d(TAG, "  Email: ${userInfo.email}")
        Log.d(TAG, "  Name: ${userInfo.name}")
    }

    /**
     * Extract wallet address from UserInfo
     * Web3Auth may store address in different fields depending on provider
     */
    private fun extractAddressFromUserInfo(userInfo: UserInfo): String? {
        // Try different possible locations for address
        return when {
            // For Flow/EVM chains, address might be in verifierId or aggregateVerifier
            userInfo.verifierId?.startsWith("0x") == true -> userInfo.verifierId
            userInfo.aggregateVerifier?.startsWith("0x") == true -> userInfo.aggregateVerifier

            // Fallback: generate from email (you may need to adjust this)
            userInfo.email != null -> {
                // For testing: generate a mock address from email
                // In production, Web3Auth should provide the actual blockchain address
                "0x${userInfo.email.hashCode().toString(16).padStart(40, '0')}"
            }

            else -> null
        }
    }

    /**
     * Sign a message using Web3Auth
     *
     * @param message Message to sign
     * @return Signed message or null if failed
     */
    suspend fun signMessage(message: String): String? {
        return try {
            // Web3Auth signing implementation
            // This depends on your Web3Auth setup and provider
            val userInfo = getCurrentUserInfo()
            if (userInfo != null) {
                // TODO: Implement actual signing with Web3Auth SDK
                // For now, return a mock signature
                Log.d(TAG, "⚠️  Signing not fully implemented. Message: $message")
                "0x_mock_signature_$message"
            } else {
                Log.e(TAG, "Cannot sign: User not authenticated")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error signing message: ${e.message}")
            null
        }
    }

    /**
     * Logout user and clear DataStore
     */
    suspend fun logout() {
        try {
            // Logout from Web3Auth
            web3AuthInstance?.logout()

            // Clear DataStore
            context.dataStore.edit { preferences ->
                preferences.clear()
            }

            // Clear cache
            cachedUserInfo = null

            Log.d(TAG, "User logged out successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error during logout: ${e.message}")
        }
    }

    /**
     * Get user preferences flow
     */
    fun getUserPreferences(): Flow<UserPreferences> {
        return context.dataStore.data.map { preferences ->
            UserPreferences(
                walletAddress = preferences[KEY_WALLET_ADDRESS],
                email = preferences[KEY_USER_EMAIL],
                name = preferences[KEY_USER_NAME],
                userId = preferences[KEY_USER_ID]
            )
        }
    }

    /**
     * Get user email
     */
    suspend fun getUserEmail(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_USER_EMAIL]
        }.first()
    }

    /**
     * Get user name
     */
    suspend fun getUserName(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_USER_NAME]
        }.first()
    }
}

/**
 * Data class for user preferences
 */
data class UserPreferences(
    val walletAddress: String? = null,
    val email: String? = null,
    val name: String? = null,
    val userId: String? = null
)

