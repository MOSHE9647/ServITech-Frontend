package com.moviles.servitech.view

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.moviles.servitech.common.rememberSessionManager
import com.moviles.servitech.ui.components.LoadingIndicator

@Composable
fun SplashScreen(
    navigateToHome: () -> Unit = {},
    navigateToLogin: () -> Unit = {},
) {
    val context = LocalContext.current.applicationContext
    val sessionManager = rememberSessionManager(context)

    val isSessionValid by sessionManager.isSessionValid.collectAsState(initial = false)
    val hasSession by sessionManager.hasSession.collectAsState(initial = false)
    val sessionMessage by sessionManager.sessionMessage.collectAsState()

    LaunchedEffect(isSessionValid, hasSession, sessionMessage) {
        sessionMessage?.let {
            if (hasSession) {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                sessionManager.clearSessionMessage()
            }
        }

        if (isSessionValid) {
            navigateToHome()
        } else if (!hasSession) {
            navigateToLogin()
        }
    }

    LoadingIndicator(
        modifier = Modifier.fillMaxSize(),
        isVisible = true
    )
}