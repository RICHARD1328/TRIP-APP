package com.tripdirect.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class NetworkStatus {
    ONLINE,
    OFFLINE
}

/**
 * Lightweight, reusable network connectivity monitor for TripDirect.
 * Uses Android ConnectivityManager and NetworkCapabilities without requiring location permissions.
 */
class NetworkConnectivityMonitor(private val context: Context) {

    private val connectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    private val _status = MutableStateFlow(getCurrentStatus())
    val status: StateFlow<NetworkStatus> = _status.asStateFlow()

    val isOnline: Boolean
        get() = _status.value == NetworkStatus.ONLINE

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _status.value = NetworkStatus.ONLINE
        }

        override fun onLost(network: Network) {
            _status.value = getCurrentStatus()
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            _status.value = if (hasInternet) NetworkStatus.ONLINE else NetworkStatus.OFFLINE
        }
    }

    private var isRegistered = false

    fun startMonitoring() {
        if (isRegistered) return
        connectivityManager?.let { cm ->
            try {
                cm.registerDefaultNetworkCallback(callback)
                isRegistered = true
            } catch (e: Exception) {
                try {
                    val request = NetworkRequest.Builder()
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .build()
                    cm.registerNetworkCallback(request, callback)
                    isRegistered = true
                } catch (_: Exception) {
                    isRegistered = false
                }
            }
        }
    }

    fun stopMonitoring() {
        if (!isRegistered) return
        try {
            connectivityManager?.unregisterNetworkCallback(callback)
        } catch (_: Exception) {
        } finally {
            isRegistered = false
        }
    }

    fun getCurrentStatus(): NetworkStatus {
        val cm = connectivityManager ?: return NetworkStatus.OFFLINE
        val activeNetwork = cm.activeNetwork ?: return NetworkStatus.OFFLINE
        val capabilities = cm.getNetworkCapabilities(activeNetwork) ?: return NetworkStatus.OFFLINE
        val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        return if (hasInternet) NetworkStatus.ONLINE else NetworkStatus.OFFLINE
    }

    companion object {
        fun isNetworkAvailable(context: Context): Boolean {
            val cm = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return false
            val activeNetwork = cm.activeNetwork ?: return false
            val capabilities = cm.getNetworkCapabilities(activeNetwork) ?: return false
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
    }
}

/**
 * Reusable Compose helper to observe connectivity status in any Composable.
 */
@Composable
fun rememberNetworkStatus(): State<NetworkStatus> {
    val context = LocalContext.current
    val monitor = remember(context) { NetworkConnectivityMonitor(context) }
    val networkState = remember { mutableStateOf(monitor.getCurrentStatus()) }

    DisposableEffect(monitor) {
        monitor.startMonitoring()
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                networkState.value = NetworkStatus.ONLINE
            }

            override fun onLost(network: Network) {
                networkState.value = monitor.getCurrentStatus()
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                networkState.value = if (hasInternet) NetworkStatus.ONLINE else NetworkStatus.OFFLINE
            }
        }
        val cm = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        try {
            cm?.registerDefaultNetworkCallback(callback)
        } catch (_: Exception) {}

        onDispose {
            try {
                cm?.unregisterNetworkCallback(callback)
            } catch (_: Exception) {}
            monitor.stopMonitoring()
        }
    }

    return networkState
}
