package com.moviles.servitech.view.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moviles.servitech.R
import com.moviles.servitech.ui.components.CustomButton
import com.moviles.servitech.ui.components.CustomCard
import com.moviles.servitech.ui.components.CustomInputField
import com.moviles.servitech.ui.components.ErrorText
import com.moviles.servitech.ui.components.HeaderImage
import com.moviles.servitech.view.auth.components.AuthViewContainer
import com.moviles.servitech.view.auth.components.HandleAuthState
import com.moviles.servitech.viewmodel.auth.ForgotPasswordState
import com.moviles.servitech.viewmodel.auth.ForgotPasswordViewModel

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
    navigateToLogin: () -> Unit = { }
) {

    val forgotPasswordState by viewModel.forgotPasswordState.observeAsState()
    val isLoading = forgotPasswordState is ForgotPasswordState.Loading

    AuthViewContainer(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        authHandler = {
            HandleAuthState(
                state = forgotPasswordState,
                logTag = "ForgotPasswordScreen",
                navigateTo = navigateToLogin
            )
        }
    ) {
        HeaderImage(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 10.dp)
        )

        ForgotPasswordForm(viewModel = viewModel, isLoading = isLoading)

        CustomButton(
            text = stringResource(R.string.back_to_login),
            onClick = { navigateToLogin() },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 44.dp, vertical = 24.dp),
            enabled = !isLoading
        )
    }

}

@Composable
fun ForgotPasswordForm(viewModel: ForgotPasswordViewModel, isLoading: Boolean) {
    val email: String by viewModel.email.observeAsState(initial = "")
    val emailError: Boolean by viewModel.emailError.observeAsState(initial = false)
    val emailErrorMsg: String? by viewModel.emailErrorMsg.observeAsState(initial = null)

    val sendLinkEnabled: Boolean by viewModel.sendLinkEnable.observeAsState(initial = false)
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

        CustomButton(
            text = stringResource(R.string.send_link),
            enabled = sendLinkEnabled && !isLoading,
            onClick = {
                viewModel.onSendLinkClicked()
                focusManager.clearFocus()
            },
        )
    }
}