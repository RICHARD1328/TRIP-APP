package com.tripdirect.data.network

import android.content.Context
import com.tripdirect.data.UserPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

sealed class ConnectionTestResult {
    data class Success(val url: String, val latencyMs: Long, val message: String) : ConnectionTestResult()
    data class Error(val message: String) : ConnectionTestResult()
}

/**
 * Centralized, thread-safe network client manager.
 * Provides dynamically configurable Retrofit and API service instances bound to
 * the user-configured backend base URL without requiring application restart or reinstallation.
 */
object ApiClient {

    private val lock = Any()

    @Volatile
    private var currentBaseUrl: String = UserPreferencesManager.DEFAULT_BACKEND_URL

    @Volatile
    private var retrofitInstance: Retrofit? = null

    @Volatile
    private var aiPlannerApiInstance: AiPlannerApi? = null

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    private val testClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(4, TimeUnit.SECONDS)
            .readTimeout(4, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Initializes ApiClient with the persistent base URL from UserPreferencesManager.
     */
    fun initialize(context: Context) {
        val prefs = UserPreferencesManager.getInstance(context)
        runBlocking(Dispatchers.IO) {
            try {
                val savedUrl = prefs.getBackendUrl()
                setBaseUrl(savedUrl)
            } catch (e: Exception) {
                // Keep default
            }
        }
    }

    /**
     * Returns the currently active backend base URL.
     */
    fun getBaseUrl(): String = currentBaseUrl

    /**
     * Updates the backend base URL and invalidates cached Retrofit instances.
     * Subsequent API calls immediately use the updated URL.
     */
    fun setBaseUrl(newUrl: String) {
        val normalized = when (val res = BackendUrlValidator.validateAndNormalize(newUrl)) {
            is BackendUrlValidator.ValidationResult.Valid -> res.normalizedUrl
            is BackendUrlValidator.ValidationResult.Invalid -> {
                if (newUrl.endsWith("/")) newUrl else "$newUrl/"
            }
        }

        synchronized(lock) {
            if (currentBaseUrl != normalized || retrofitInstance == null) {
                currentBaseUrl = normalized
                retrofitInstance = null
                aiPlannerApiInstance = null
            }
        }
    }

    /**
     * Obtains the AiPlannerApi instance configured with the current base URL.
     * Dynamically creates a new instance if the base URL changed.
     */
    fun getAiPlannerApi(): AiPlannerApi {
        return aiPlannerApiInstance ?: synchronized(lock) {
            aiPlannerApiInstance ?: createRetrofit(currentBaseUrl).create(AiPlannerApi::class.java).also {
                aiPlannerApiInstance = it
            }
        }
    }

    private fun createRetrofit(baseUrl: String): Retrofit {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitInstance = retrofit
        return retrofit
    }

    /**
     * Verifies connectivity to the specified candidate URL or the currently active URL.
     * Calls the real backend /api/v1/health endpoint (with fallback to /).
     */
    suspend fun testConnection(targetUrl: String? = null): ConnectionTestResult = withContext(Dispatchers.IO) {
        val urlToTest = if (!targetUrl.isNullOrBlank()) {
            when (val v = BackendUrlValidator.validateAndNormalize(targetUrl)) {
                is BackendUrlValidator.ValidationResult.Valid -> v.normalizedUrl
                is BackendUrlValidator.ValidationResult.Invalid -> return@withContext ConnectionTestResult.Error(v.errorMessage)
            }
        } else {
            currentBaseUrl
        }

        val startTime = System.currentTimeMillis()
        try {
            val healthUrl = "${urlToTest}api/v1/health"
            val request = Request.Builder()
                .url(healthUrl)
                .get()
                .build()

            testClient.newCall(request).execute().use { response ->
                val elapsed = System.currentTimeMillis() - startTime
                if (response.isSuccessful) {
                    ConnectionTestResult.Success(
                        url = urlToTest,
                        latencyMs = elapsed,
                        message = "Connected to backend successfully in ${elapsed}ms (Status: HTTP ${response.code})"
                    )
                } else if (response.code == 404) {
                    testRoot(urlToTest, elapsed)
                } else {
                    ConnectionTestResult.Error("Server responded with HTTP ${response.code} ${response.message}")
                }
            }
        } catch (e: ConnectException) {
            ConnectionTestResult.Error("Connection refused at $urlToTest. Check if backend server is running.")
        } catch (e: SocketTimeoutException) {
            ConnectionTestResult.Error("Connection timed out. Server at $urlToTest did not respond within 4s.")
        } catch (e: UnknownHostException) {
            ConnectionTestResult.Error("Unknown host at $urlToTest. Verify the IP address or domain name.")
        } catch (e: Exception) {
            ConnectionTestResult.Error("Connection failed: ${e.message ?: "Network error"}")
        }
    }

    private fun testRoot(url: String, priorElapsed: Long): ConnectionTestResult {
        return try {
            val req = Request.Builder().url(url).get().build()
            testClient.newCall(req).execute().use { resp ->
                if (resp.isSuccessful) {
                    ConnectionTestResult.Success(
                        url = url,
                        latencyMs = priorElapsed,
                        message = "Connected to server root in ${priorElapsed}ms"
                    )
                } else {
                    ConnectionTestResult.Error("Server returned HTTP ${resp.code}")
                }
            }
        } catch (e: Exception) {
            ConnectionTestResult.Error("Server returned HTTP 404 on health check.")
        }
    }
}
