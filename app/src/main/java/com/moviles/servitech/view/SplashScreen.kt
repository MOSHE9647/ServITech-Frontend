package com.moviles.servitech.view

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.moviles.servitech.common.rememberSessionManager
import com.moviles.servitech.core.navigation.Screen
import com.moviles.servitech.ui.components.LoadingIndicator

@Composable
fun SplashScreen(navigateTo: (String) -> Unit) {
    val context = LocalContext.current.applicationContext
    val sessionManager = rememberSessionManager(context)

    val isSessionValid by sessionManager.isSessionValid.collectAsState(initial = false)
    val hasSession by sessionManager.hasSession.collectAsState(initial = false)
    val sessionMessage by sessionManager.sessionMessage.collectAsState()
    val tokenChecked = remember { mutableStateOf(false) }

    // Show the Toast message if it exists
    LaunchedEffect(sessionMessage, hasSession) {
        if (hasSession && sessionMessage != null) {
            Toast.makeText(context, sessionMessage, Toast.LENGTH_LONG).show()
            sessionManager.clearSessionMessage()
        }
    }

    // Manage the navigation based on session validity
    LaunchedEffect(isSessionValid) {
        if (!tokenChecked.value) {
            val destination = if (isSessionValid) Screen.Home.route else Screen.Login.route
            navigateTo(destination)
            tokenChecked.value = true
        }
    }

    LoadingIndicator(
        modifier = Modifier.fillMaxSize(),
        withBlurBackground = true,
        isVisible = true
    )
}