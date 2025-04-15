package com.moviles.servitech.view

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moviles.servitech.R
import com.moviles.servitech.common.rememberSessionManager
import com.moviles.servitech.core.navigation.Screen
import com.moviles.servitech.ui.components.HandleServerError
import com.moviles.servitech.ui.components.LoadingIndicator
import com.moviles.servitech.viewmodel.auth.LogoutViewModel

@Composable
fun HomeScreen(
    viewModel: LogoutViewModel = hiltViewModel(),
    navigateTo: (String) -> Unit
) {
    val context = LocalContext.current.applicationContext
    val sessionManager = rememberSessionManager(context)

    val logoutState by viewModel.logoutState.observeAsState()

    val user by sessionManager.user.collectAsState(initial = null)
    val token by sessionManager.token.collectAsState(initial = "")
    val expiresAt by sessionManager.expiresAt.collectAsState(initial = 0L)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "Bienvenido a ServITech", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Token: $token")
            Text(text = "Expires At: ${formatDate(expiresAt ?: 0L)}")

            Spacer(modifier = Modifier.height(8.dp))

            if (user != null) {
                Text(text = "Usuario:")
                Text(text = "ID: ${user?.id}")
                Text(text = "Rol: ${user?.role}")
                Text(text = "Nombre: ${user?.name} ${user?.lastName}")
                Text(text = "Email: ${user?.email}")
                Text(text = "Teléfono: ${user?.phone}")
            } else {
                Text("No se encontró información del usuario.")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (token!!.isNotEmpty()) viewModel.logout(token!!)
                    else Toast.makeText(context, "No Token", Toast.LENGTH_SHORT).show()
                }
            ) {
                Text("Cerrar sesión")
            }
        }

        when (val state = logoutState) {
            is LogoutViewModel.LogoutState.Loading -> {
                // Animated loading indicator
                LoadingIndicator(
                    modifier = Modifier.fillMaxSize(),
                    withBlurBackground = true,
                    isVisible = true
                )
            }
            is LogoutViewModel.LogoutState.Success -> {
                Toast.makeText(
                    context,
                    stringResource(R.string.success_logout),
                    Toast.LENGTH_LONG
                ).show()
                navigateTo(Screen.Login.route)
            }
            is LogoutViewModel.LogoutState.Error -> {
                HandleServerError("HomeScreen", state.message)
            }
            else -> { /* No-op */ }
        }
    }
}

@SuppressLint("SimpleDateFormat")
fun formatDate(epochMillis: Long): String {
    return if (epochMillis != 0L) {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        sdf.format(java.util.Date(epochMillis))
    } else {
        "Sin fecha"
    }
}