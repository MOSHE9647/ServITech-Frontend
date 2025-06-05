package com.moviles.servitech.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tracks the network connectivity status of the device.
 * This class uses the ConnectivityManager to listen for changes in network connectivity
 * and updates a StateFlow that can be observed by other components in the application.
 * It provides a simple way to check if the device is currently connected to the internet.
 *
 * Usage:
 * ```kotlin
 * val networkStatusTracker: NetworkStatusTracker = // get instance from DI
 * networkStatusTracker.isConnected.collect { isConnected ->
 *     if (isConnected) {
 *         // Device is connected to the internet
 *     } else {
 *         // Device is not connected to the internet
 *     }
 * }
 * ```
 */
@Singleton
class NetworkStatusTracker @Inject constructor(
    connectivityManager: ConnectivityManager
) {

    /**
     * A StateFlow that emits the current network connectivity status.
     * It will emit `true` if the device is connected to the internet, and `false` otherwise.
     */
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    init {
        // Check initial connectivity status and update the StateFlow
        connectivityManager.registerDefaultNetworkCallback(
            object: ConnectivityManager.NetworkCallback() {
                // This method is called when the network is available
                override fun onAvailable(network: Network) {
                    _isConnected.value = true
                }

                // This method is called when the network is lost or becomes unavailable
                override fun onLost(network: Network) {
                    _isConnected.value = false
                }

                // This method is called when the capabilities of the network change
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    // Update the connectivity status based on the capabilities of the network
                    _isConnected.value = networkCapabilities.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_INTERNET
                    )
                }
            }
        )
    }

}