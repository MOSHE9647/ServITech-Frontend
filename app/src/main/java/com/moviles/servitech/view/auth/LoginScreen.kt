package com.moviles.servitech.view.auth

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moviles.servitech.R
import com.moviles.servitech.ui.components.CustomButton
import com.moviles.servitech.ui.components.CustomCard
import com.moviles.servitech.ui.components.CustomInputField
import com.moviles.servitech.ui.components.ErrorText
import com.moviles.servitech.ui.components.HeaderImage
import com.moviles.servitech.view.auth.components.AuthNavigationMessage
import com.moviles.servitech.view.auth.components.AuthViewContainer
import com.moviles.servitech.view.auth.components.HandleAuthState
import com.moviles.servitech.viewmodel.auth.LoginState
import com.moviles.servitech.viewmodel.auth.LoginViewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToForgotPassword: () -> Unit = { },
    navigateToSignUp: () -> Unit = { },
    navigateToHome: () -> Unit = { }
) {

    val loginState by viewModel.loginState.observeAsState()
    val isLoading = loginState is LoginState.Loading

    AuthViewContainer(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        authHandler = {
            HandleAuthState (
                state = loginState,
                logTag = "LoginScreen",
                navigateTo = navigateToHome
            )
        }
    ) {
        HeaderImage(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 10.dp)
        )

        LoginForm(viewModel = viewModel, isLoading = isLoading, onForgotPassword = {
            Log.d("LoginScreen", "Forgot Password clicked")
            navigateToForgotPassword()
        })

        AuthNavigationMessage(
            message = stringResource(R.string.no_account),
            actionText = stringResource(R.string.sign_up),
            isClickable = !isLoading
        ) {
            // Navigate to Sign Up screen
            navigateToSignUp()
        }

        CustomButton(
            text = stringResource(R.string.continue_guest),
            onClick = {
                Log.d("LoginScreen", "Continue as Guest clicked")
                viewModel.onGuestSelected()
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 44.dp, vertical = 24.dp),
            enabled = !isLoading
        )
    }

}

@Composable
fun LoginForm(viewModel: LoginViewModel, isLoading: Boolean, onForgotPassword: () -> Unit) {
    val email: String by viewModel.email.observeAsState(initial = "")
    val password: String by viewModel.password.observeAsState(initial = "")

    val emailError: Boolean by viewModel.emailError.observeAsState(initial = false)
    val passwordError: Boolean by viewModel.passwordError.observeAsState(initial = false)

    val emailErrorMsg: String? by viewModel.emailErrorMsg.observeAsState(initial = null)
    val passwordErrorMsg: String? by viewModel.passwordErrorMsg.observeAsState(initial = null)
    val loginEnable: Boolean by viewModel.loginEnable.observeAsState(initial = false)

    val focusManager = LocalFocusManager.current

    CustomCard {
        CustomInputField(
            label = stringResource(R.string.email),
            placeholder = stringResource(R.string.email_hint),
            value = email,
            onValueChange = { viewModel.onEmailChanged(it) },
            keyboardType = KeyboardType.Email,
            isError = emailError,
            enabled = !isLoading,
            supportingText = {
                if (emailError) ErrorText(
                    emailErrorMsg ?: stringResource(R.string.email_invalid_error),
                )
            },
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
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
                if (passwordError) ErrorText(
                    passwordErrorMsg ?: stringResource(R.string.password_length_error)
                )
            },
            enabled = !isLoading,
            imeAction = ImeAction.Done,
            onImeAction = { focusManager.clearFocus() }
        )

        Spacer(modifier = Modifier.height(10.dp))

        CustomButton(
            text = stringResource(R.string.login),
            enabled = loginEnable && !isLoading,
            onClick = { viewModel.onLoginSelected() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        ForgotPassword(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onActionClick = { onForgotPassword() },
            isClickable = !isLoading
        )
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