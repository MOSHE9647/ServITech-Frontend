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
import com.moviles.servitech.viewmodel.auth.LoginViewModel
import com.moviles.servitech.viewmodel.auth.RegisterViewModel

@Composable
fun <T> HandleAuthState (
    state: T?,
    logTag: String,
    navigateTo: () -> Unit = { },
) {
    when (state) {
        is LoginViewModel.LoginState.Loading, is RegisterViewModel.RegisterState.Loading -> {
            LoadingIndicator(
                modifier = Modifier.fillMaxSize(),
                withBlurBackground = true,
                isVisible = true
            )
        }
        is LoginViewModel.LoginState.Success -> {
            handleLoginSuccess(
                logTag = logTag,
                state = state as LoginViewModel.LoginState.Success,
                navigateTo = navigateTo
            )
        }
        is RegisterViewModel.RegisterState.Success -> {
            handleRegisterSuccess(
                logTag = logTag,
                state = state as RegisterViewModel.RegisterState.Success,
                context = LocalContext.current.applicationContext,
                stringResource = R.string.register_success,
                navigateTo = navigateTo
            )
        }
        is LoginViewModel.LoginState.Error, is RegisterViewModel.RegisterState.Error -> {
            HandleServerError(logTag, (state as? LoginViewModel.LoginState.Error)?.message ?: (state as RegisterViewModel.RegisterState.Error).message)
        }
        else -> { }
    }
}

fun handleRegisterSuccess(
    logTag: String,
    state: RegisterViewModel.RegisterState.Success,
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
    state: LoginViewModel.LoginState.Success,
    navigateTo: () -> Unit = { },
) {
    Log.d(logTag, "User: ${state.data.user}")
    navigateTo()
}
