package com.moviles.servitech.view

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moviles.servitech.R
import com.moviles.servitech.view.components.AuthNavigationMessage
import com.moviles.servitech.view.components.CustomButton
import com.moviles.servitech.view.components.CustomInputField
import com.moviles.servitech.view.components.HeaderImage
import com.moviles.servitech.ui.components.LoadingIndicator
import com.moviles.servitech.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val loginState by viewModel.loginState.observeAsState()
    val isLoading = loginState is LoginViewModel.LoginState.Loading

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Main content
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 23.dp, bottom = 23.dp)
        ) {
            Column {
                HeaderImage(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 24.dp)
                        .size(270.dp)
                )

                LoginForm(viewModel = viewModel, isLoading = isLoading)

                AuthNavigationMessage(
                    message = stringResource(R.string.no_account),
                    actionText = stringResource(R.string.sign_up),
                    isClickable = !isLoading
                ) {
                    Log.d("LoginScreen", "Sign Up clicked")
                }

                CustomButton(
                    text = stringResource(R.string.continue_guest),
                    onClick = { },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 44.dp, vertical = 24.dp),
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    borderColor = MaterialTheme.colorScheme.outlineVariant,
                    enabled = !isLoading
                )
            }
        }

        when (val state = loginState) {
            is LoginViewModel.LoginState.Loading -> {
                // Animated loading indicator
                LoadingIndicator(
                    modifier = Modifier.fillMaxSize(),
                    withBlurBackground = true,
                    isVisible = true
                )
            }

            is LoginViewModel.LoginState.Error -> Log.d("LoginScreen", state.message)
            is LoginViewModel.LoginState.Success -> Log.d("LoginScreen", "Success: ${state.data}")
            else -> {
                Log.d("LoginScreen", "No login state")
            }
        }
    }
}

@Composable
fun LoginForm(modifier: Modifier = Modifier, viewModel: LoginViewModel, isLoading: Boolean) {
    val email: String by viewModel.email.observeAsState(initial = "")
    val password: String by viewModel.password.observeAsState(initial = "")

    val emailError: Boolean by viewModel.emailError.observeAsState(initial = false)
    val passwordError: Boolean by viewModel.passwordError.observeAsState(initial = false)

    val emailErrorMsg: String? by viewModel.emailErrorMsg.observeAsState(initial = null)
    val passwordErrorMsg: String? by viewModel.passwordErrorMsg.observeAsState(initial = null)
    val loginEnable: Boolean by viewModel.loginEnable.observeAsState(initial = false)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = MaterialTheme.shapes.medium
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            CustomInputField(
                label = stringResource(R.string.email),
                placeholder = stringResource(R.string.email_hint),
                value = email,
                onValueChange = { viewModel.onEmailChanged(it) },
                keyboardType = KeyboardType.Email,
                isError = emailError,
                enabled = !isLoading,
                supportingText = {
                    if (emailError) {
                        Text(
                            text = emailErrorMsg ?: stringResource(R.string.email_error),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            CustomInputField(
                label = stringResource(R.string.password),
                placeholder = stringResource(R.string.password_hint),
                value = password,
                onValueChange = { viewModel.onPasswordChanged(it) },
                keyboardType = KeyboardType.Password,
                isPassword = true,
                isError = passwordError,
                supportingText = {
                    if (passwordError) {
                        Text(
                            text = passwordErrorMsg ?: stringResource(R.string.password_error),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(10.dp))

            val context = LocalContext.current
            CustomButton(
                text = stringResource(R.string.login),
                enabled = loginEnable && !isLoading,
                onClick = { viewModel.onLoginSelected(context = context) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            ForgotPassword(Modifier.align(Alignment.CenterHorizontally), !isLoading) {
                Log.d("LoginScreen", "Forgot Password clicked")
            }
        }
    }
}

@Composable
fun ForgotPassword(modifier: Modifier, isClickable: Boolean = true, onActionClick: () -> Unit) {
    Text(
        text = stringResource(R.string.forgot_password),
        modifier = modifier
            .clickable(enabled = isClickable) { onActionClick() }
            .padding(8.dp),
        style = MaterialTheme.typography.bodyLarge.copy(
            textDecoration = TextDecoration.Underline
        )
    )
}