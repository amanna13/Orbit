package com.orbit

import android.app.Application
import android.util.Log
import com.orbit.network.ApiClient
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@HiltAndroidApp
class OrbitApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        // Test backend connectivity on startup
        applicationScope.launch {
            try {
                Log.d("OrbitApp", "Testing backend connection...")
                val isConnected = ApiClient.testConnection()
                if (isConnected) {
                    Log.d("OrbitApp", "Backend is connected and ready!")
                } else {
                    Log.w("OrbitApp", "⚠Backend connection issue. Please check if server is running.")
                }
            } catch (e: Exception) {
                Log.e("OrbitApp", "Failed to test backend connection: ${e.message}")
            }
        }
    }
}