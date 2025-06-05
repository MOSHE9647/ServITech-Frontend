package com.moviles.servitech

import android.app.Application
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.moviles.servitech.core.navigation.NavigationGraph
import com.moviles.servitech.network.NetworkStatusTracker
import com.moviles.servitech.services.PendingOperationService
import com.moviles.servitech.ui.theme.ServITechTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The `MainActivityApp` class is annotated with `@HiltAndroidApp` to initialize Hilt dependency injection.
 * This triggers Hilt's code generation and serves as the base application class for dependency injection.
 * This class is empty, but it is necessary to set up Hilt in the application
 */
@HiltAndroidApp
class MainActivityApp : Application()

/**
 * The `MainActivity` class is the main entry point of the application.
 * It extends `ComponentActivity` and is annotated with `@AndroidEntryPoint` to enable Hilt dependency injection.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject // Inject the NetworkStatusTracker using Hilt
    lateinit var networkStatusTracker: NetworkStatusTracker

    @Inject // Inject the PendingOperationService using Hilt
    lateinit var pendingOperationService: PendingOperationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ServITechTheme(dynamicColor = false) {
                NavigationGraph()
            }
        }

        // Observe network status changes
        lifecycleScope.launch {
            /**
             * Collect the network status updates from the NetworkStatusTracker.
             * This will trigger whenever the network connectivity changes.
             */
            networkStatusTracker.isConnected.collect { isConnected ->
                // Show a Toast message based on the network connectivity status
                val messageResId = when (isConnected) {
                    // If connected, sync pending operations and show a success message
                    true -> {
                        pendingOperationService.syncPendingOperations()
                        R.string.network_available_msg
                    }
                    // If not connected, show a different message
                    false -> R.string.network_unavailable_msg
                }
                // Display the Toast message with the appropriate string resource
                Toast.makeText(this@MainActivity, getString(messageResId), Toast.LENGTH_SHORT).show()
            }
        }
    }
}