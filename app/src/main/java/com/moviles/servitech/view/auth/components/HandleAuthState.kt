package com.moviles.servitech.view.auth.components

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.moviles.servitech.R
import com.moviles.servitech.ui.components.HandleServerError
import com.moviles.servitech.ui.components.LoadingIndicator
import com.moviles.servitech.viewmodel.auth.LoginState
import com.moviles.servitech.viewmodel.auth.RegisterState

@Composable
fun <T> HandleAuthState (
    state: T?,
    logTag: String,
    navigateTo: () -> Unit = { },
) {
    when (state) {
        is LoginState.Loading, is RegisterState.Loading -> {
            LoadingIndicator(
                modifier = Modifier.fillMaxSize(),
                withBlurBackground = true,
                isVisible = true
            )
        }
        is LoginState.Success -> {
            handleLoginSuccess(
                logTag = logTag,
                state = state as LoginState.Success,
                navigateTo = navigateTo
            )
        }
        is RegisterState.Success -> {
            handleRegisterSuccess(
                logTag = logTag,
                state = state as RegisterState.Success,
                context = LocalContext.current.applicationContext,
                stringResource = R.string.register_success,
                navigateTo = navigateTo
            )
        }
        is LoginState.Error, is RegisterState.Error -> {
            HandleServerError(logTag, (state as? LoginState.Error)?.message ?: (state as RegisterState.Error).message)
        }
        else -> { }
    }
}

fun handleRegisterSuccess(
    logTag: String,
    state: RegisterState.Success,
    context: Context,
    stringResource: Int,
    navigateTo: () -> Unit
) {
    Log.d(logTag, "User: ${state.data}")
    Toast.makeText(
        context,
        context.getString(stringResource),
        Toast.LENGTH_LONG
    ).show()
    navigateTo()
}

fun handleLoginSuccess(
    logTag: String,
    state: LoginState.Success,
    navigateTo: () -> Unit = { },
) {
    Log.d(logTag, "User: ${state.data.user}")
    navigateTo()
}
