package com.moviles.servitech.view.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
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
import com.moviles.servitech.view.auth.components.AuthNavigationMessage
import com.moviles.servitech.view.auth.components.AuthViewContainer
import com.moviles.servitech.view.auth.components.HandleAuthState
import com.moviles.servitech.viewmodel.auth.RegisterViewModel

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel(),
    navigateToLogin: () -> Unit = { }
) {

    val registerState by viewModel.registerState.observeAsState()
    val isLoading = registerState is RegisterViewModel.RegisterState.Loading

    AuthViewContainer(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        authHandler = {
            HandleAuthState(
                state = registerState,
                logTag = "RegisterScreen",
                navigateTo = navigateToLogin
            )
        }
    ) {
        HeaderImage(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 10.dp)
        )

        RegisterForm(viewModel = viewModel, isLoading = isLoading)

        AuthNavigationMessage(
            modifier = Modifier
                .padding(bottom = 24.dp),
            message = stringResource(R.string.have_account),
            actionText = stringResource(R.string.login),
            isClickable = !isLoading,
            onActionClick = { navigateToLogin() }
        )
    }

}

@Composable
fun RegisterForm(viewModel: RegisterViewModel, isLoading: Boolean) {
    val name: String by viewModel.name.observeAsState(initial = "")
    val phone: String by viewModel.phone.observeAsState(initial = "")
    val email: String by viewModel.email.observeAsState(initial = "")
    val password: String by viewModel.password.observeAsState(initial = "")
    val passwordConfirmation: String by viewModel.passwordConfirmation.observeAsState(initial = "")

    val nameError: Boolean by viewModel.nameError.observeAsState(initial = false)
    val nameErrorMsg: String? by viewModel.nameErrorMsg.observeAsState(initial = null)

    val phoneError: Boolean by viewModel.phoneError.observeAsState(initial = false)
    val phoneErrorMsg: String? by viewModel.phoneErrorMsg.observeAsState(initial = null)

    val emailError: Boolean by viewModel.emailError.observeAsState(initial = false)
    val emailErrorMsg: String? by viewModel.emailErrorMsg.observeAsState(initial = null)

    val passwordError: Boolean by viewModel.passwordError.observeAsState(initial = false)
    val passwordErrorMsg: String? by viewModel.passwordErrorMsg.observeAsState(initial = null)

    val passwordConfirmationError: Boolean by viewModel.passwordConfirmationError.observeAsState(initial = false)
    val passwordConfirmationErrorMsg: String? by viewModel.passwordConfirmationErrorMsg.observeAsState(initial = null)

    val registerEnable: Boolean by viewModel.registerEnable.observeAsState(initial = false)
    val focusManager = LocalFocusManager.current

    CustomCard {
        val inputModifier: Modifier = Modifier
            .padding(bottom = 5.dp)

        CustomInputField(
            modifier = inputModifier,
            label = stringResource(R.string.name),
            placeholder = stringResource(R.string.name_hint),
            value = name,
            onValueChange = { viewModel.onNameChanged(it) },
            keyboardType = KeyboardType.Text,
            isError = nameError,
            enabled = !isLoading,
            supportingText = {
                if (nameError) ErrorText(
                    nameErrorMsg ?: stringResource(R.string.name_empty_error)
                )
            },
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        CustomInputField(
            modifier = inputModifier,
            label = stringResource(R.string.phone),
            placeholder = stringResource(R.string.phone_hint),
            value = phone,
            onValueChange = { viewModel.onPhoneChanged(it) },
            keyboardType = KeyboardType.Phone,
            isError = phoneError,
            enabled = !isLoading,
            supportingText = {
                if (phoneError) ErrorText(
                    phoneErrorMsg ?: stringResource(R.string.phone_empty_error)
                )
            },
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        CustomInputField(
            modifier = inputModifier,
            label = stringResource(R.string.email),
            placeholder = stringResource(R.string.email_hint),
            value = email,
            onValueChange = { viewModel.onEmailChanged(it) },
            keyboardType = KeyboardType.Email,
            isError = emailError,
            enabled = !isLoading,
            supportingText = {
                if (emailError) ErrorText(
                    emailErrorMsg ?: stringResource(R.string.email_invalid_error)
                )
            },
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        CustomInputField(
            modifier = inputModifier,
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

        CustomInputField(
            modifier = inputModifier,
            label = stringResource(R.string.confirm_password),
            placeholder = stringResource(R.string.confirm_password_hint),
            value = passwordConfirmation,
            onValueChange = { viewModel.onPasswordConfirmationChanged(it) },
            keyboardType = KeyboardType.Password,
            isPassword = true,
            isError = passwordConfirmationError,
            supportingText = {
                if (passwordConfirmationError) ErrorText(
                    passwordConfirmationErrorMsg ?: stringResource(R.string.confirm_password_length_error)
                )
            },
            enabled = !isLoading,
            imeAction = ImeAction.Done,
            onImeAction = { focusManager.clearFocus() }
        )

        CustomButton(
            text = stringResource(R.string.register),
            enabled = registerEnable && !isLoading,
            onClick = { viewModel.onRegisterSelected() }
        )
    }
}