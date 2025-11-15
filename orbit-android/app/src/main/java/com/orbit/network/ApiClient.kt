package com.orbit.network

import com.orbit.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit API Client
 * Provides configured Retrofit instance for API calls
 */
object ApiClient {

    /**
     * Base URL for API calls
     * Uses BuildConfig for environment-specific URLs
     */
    private const val BASE_URL = "http://127.0.0.1:4000/"  // Android Emulator localhost mapping
    // For physical device, use your computer's IP: "http://192.168.x.x:4000/"
    // The backend is running on http://127.0.0.1:4000 (localhost)
    // Android emulator maps 10.0.2.2 to host machine's localhost

    /**
     * Moshi instance for JSON serialization/deserialization
     */
    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    /**
     * Logging interceptor for debugging network requests
     * Logs request/response in debug builds
     */
    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    /**
     * OkHttp client with interceptors and timeouts
     */
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Access-Control-Allow-Origin", "*")
                    .build()

                // Log the request URL for debugging
                println("API Request: ${request.method} ${request.url}")

                try {
                    val response = chain.proceed(request)
                    println("API Response: ${response.code} - ${request.url}")
                    response
                } catch (e: Exception) {
                    println("API Error: ${e.message} - ${request.url}")
                    throw e
                }
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Retrofit instance configured with Moshi and OkHttp
     */
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    /**
     * Create API service instance
     * Usage: val podService = ApiClient.createService<PodService>()
     */
    inline fun <reified T> createService(): T {
        return retrofit.create(T::class.java)
    }

    /**
     * Test backend connectivity
     * Call this to verify the app can reach the backend
     */
    fun testConnection(): Boolean {
        return try {
            val response = okHttpClient.newCall(
                okhttp3.Request.Builder()
                    .url(BASE_URL)
                    .get()
                    .build()
            ).execute()

            val isConnected = response.isSuccessful || response.code in 200..499
            if (isConnected) {
                println("Backend Connection Successful! Server is reachable at $BASE_URL")
            } else {
                println("Backend Connection Issue: HTTP ${response.code}")
            }
            isConnected
        } catch (e: Exception) {
            println("Backend Connection Failed: ${e.message} - ${e.cause}")
            println("   Make sure backend is running on http://127.0.0.1:4000")
            println("   URL being used: $BASE_URL")
            false
        }
    }
}

