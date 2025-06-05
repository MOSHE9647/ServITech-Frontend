package com.moviles.servitech.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun PhoneNumberField(
    label: String,
    placeholder: String,
    phoneValue: String,
    onPhoneChanged: (String) -> Unit,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onImeAction: () -> Unit = {},
    imeAction: ImeAction = ImeAction.Next
) {
    CustomInputField(
        label = label,
        placeholder = placeholder,
        value = phoneValue,
        onValueChange = { onPhoneChanged(it) },
        keyboardType = KeyboardType.Phone,
        onImeAction = onImeAction,
        imeAction = imeAction,
        isError = isError,
        supportingText = supportingText,
        enabled = enabled,
        modifier = modifier
    )
}