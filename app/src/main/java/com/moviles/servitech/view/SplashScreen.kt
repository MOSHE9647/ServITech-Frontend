package com.moviles.servitech.view

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.moviles.servitech.common.Utils.rememberSessionManager
import com.moviles.servitech.ui.components.LoadingIndicator

/**
 * SplashScreen is a composable function that serves as the initial screen
 * when the application starts.
 *
 * It checks the validity of the user session and navigates to the appropriate screen
 * based on the session state.
 *
 * If the session is valid, it navigates to the home screen.
 * If the session is not valid and no session exists,
 * it navigates to the login screen.
 */
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