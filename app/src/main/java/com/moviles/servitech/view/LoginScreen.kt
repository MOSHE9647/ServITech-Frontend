package com.moviles.servitech.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviles.servitech.R
import com.moviles.servitech.viewmodel.LoginViewModel

@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    Surface (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 23.dp, bottom = 23.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Login(viewModel)
    }
}

@Composable
fun Login(viewModel: LoginViewModel) {
    val isLoading : Boolean by viewModel.isLoading.observeAsState(initial = false)

    if (isLoading) {
        LoadingIndicator(Modifier.fillMaxSize())
    } else {
        Column {
            HeaderImage(Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 24.dp)
                .size(270.dp)
            )
            LoginForm(Modifier, viewModel)
            RegisterMessage(Modifier)
            CustomButton(
                text = stringResource(R.string.continue_guest),
                onClick = {  },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 44.dp, vertical = 24.dp),
                containerColor = Color(0xFFE3E3E3),
                contentColor = Color(0xFF1E1E1E),
                borderColor = Color(0xFF767676),
            )
        }
    }
}

@Composable
fun LoadingIndicator(modifier: Modifier) {
    Box (modifier) {
        CircularProgressIndicator(
            Modifier.align(Alignment.Center),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun RegisterMessage(modifier: Modifier) {
    val interFont = FontFamily(Font(R.font.inter))
    val textStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp,
        fontFamily = interFont
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.no_account),
            style = textStyle
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = stringResource(R.string.sign_up),
            modifier = Modifier.clickable { },
            style = textStyle,
            textDecoration = TextDecoration.Underline
        )
    }
}

@Composable
fun LoginForm(modifier: Modifier, viewModel: LoginViewModel) {
    val email : String by viewModel.email.observeAsState(initial = "")
    val password : String by viewModel.password.observeAsState(initial = "")
    val loginEnable : Boolean by viewModel.loginEnable.observeAsState(initial = false)

    Box (
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
            .border(
                BorderStroke(
                    1.dp,
                    Color(0xFFD9D9D9)
                ),
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Column (modifier = Modifier
            .fillMaxWidth()
            .padding(22.dp)
        ) {
            CustomInputField(
                label = stringResource(R.string.email),
                placeholder = stringResource(R.string.email_hint),
                value = email,
                onValueChange = { viewModel.onLoginChanged(it, password) },
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.padding(12.dp))

            CustomInputField(
                label = stringResource(R.string.password),
                placeholder = stringResource(R.string.password_hint),
                value = password,
                onValueChange = { viewModel.onLoginChanged(email, it) },
                keyboardType = KeyboardType.Password,
                isPassword = true
            )

            Spacer(modifier = Modifier.padding(14.dp))

            CustomButton(
                text = stringResource(R.string.login),
                enabled = loginEnable,
                onClick = { viewModel.onLoginSelected() }
            )

            Spacer(modifier = Modifier.padding(14.dp))

            ForgotPassword(Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = Color(0xFF2C2C2C),
    contentColor: Color = Color(0xFFF5F5F5),
    borderColor: Color = contentColor,
    disabledContainerColor: Color = Color(0xFFD9D9D9),
    disabledContentColor: Color = Color(0xFFB3B3B3),
    disabledBorderColor: Color = disabledContentColor,
    cornerRadius: Dp = 8.dp,
    height: Dp = 48.dp,
    fontSize: TextUnit = 16.sp
) {
    val interFont = FontFamily(Font(R.font.inter))

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shape = RoundedCornerShape(cornerRadius),
        border = BorderStroke(1.dp, if (enabled) borderColor else disabledBorderColor),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        )
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = fontSize,
                fontWeight = FontWeight.Normal,
                lineHeight = fontSize,
                fontFamily = interFont
            )
        )
    }
}

@Composable
fun ForgotPassword(modifier: Modifier) {
    Text(
        text = stringResource(R.string.forgot_password),
        modifier = modifier.clickable { },
        style = TextStyle(
            fontSize = 16.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily(Font(R.font.inter)),
            textDecoration = TextDecoration.Underline
        )
    )
}

@Composable
fun CustomInputField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
) {
    val interFont = FontFamily(Font(R.font.inter))
    val textStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp,
        fontFamily = interFont
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = label,
            style = textStyle
        )

        Spacer(modifier = Modifier.padding(4.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text (text = placeholder) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color(0xFF1E1E1E),
                unfocusedTextColor = Color(0xFF1E1E1E),
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedIndicatorColor = Color(0xFFB3B3B3),
                unfocusedIndicatorColor = Color(0xFFD9D9D9),
                focusedPlaceholderColor = Color(0xFFB3B3B3),
                unfocusedPlaceholderColor = Color(0xFFB3B3B3),
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            maxLines = 1,
            textStyle = textStyle
        )
    }
}

@Composable
fun HeaderImage(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentScale = ContentScale.Fit,
        contentDescription = "Logo",
        modifier = modifier
    )
}