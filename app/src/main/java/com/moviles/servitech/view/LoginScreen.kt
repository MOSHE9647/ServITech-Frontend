package com.moviles.servitech.view

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.moviles.servitech.R
import com.moviles.servitech.ui.components.AuthNavigationMessage
import com.moviles.servitech.ui.components.CustomButton
import com.moviles.servitech.ui.components.CustomInputField
import com.moviles.servitech.ui.components.HeaderImage
import com.moviles.servitech.ui.components.LoadingIndicator
import com.moviles.servitech.viewmodel.LoginViewModel

@Composable
fun LoginScreen(viewModel: LoginViewModel, modifier: Modifier = Modifier) {
    val isLoading by viewModel.isLoading.observeAsState(initial = false)

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

        // Animated loading indicator
        LoadingIndicator(
            modifier = Modifier.fillMaxSize(),
            withBlurBackground = true,
            isVisible = isLoading
        )
    }
}

@Composable
fun LoginForm(modifier: Modifier = Modifier, viewModel: LoginViewModel, isLoading: Boolean) {
    val email: String by viewModel.email.observeAsState(initial = "")
    val password: String by viewModel.password.observeAsState(initial = "")

    val emailError: Boolean by viewModel.emailError.observeAsState(initial = false)
    val passwordError: Boolean by viewModel.passwordError.observeAsState(initial = false)
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
                onValueChange = { viewModel.onLoginChanged(it, password) },
                keyboardType = KeyboardType.Email,
                isError = emailError,
                enabled = !isLoading,
                supportingText = {
                    if (emailError) {
                        Text(
                            text = stringResource(R.string.email_error),
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
                onValueChange = { viewModel.onLoginChanged(email, it) },
                keyboardType = KeyboardType.Password,
                isPassword = true,
                isError = passwordError,
                supportingText = {
                    if (passwordError) {
                        Text(
                            text = stringResource(R.string.password_error),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(10.dp))

            CustomButton(
                text = stringResource(R.string.login),
                enabled = loginEnable && !isLoading,
                onClick = { viewModel.onLoginSelected() }
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