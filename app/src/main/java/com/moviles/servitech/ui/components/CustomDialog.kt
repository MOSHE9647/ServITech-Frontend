package com.moviles.servitech.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SignalWifiStatusbarConnectedNoInternet4
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties

@Composable
fun CustomDialog(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    title: String? = null,
    message: String,
    shape: Shape = MaterialTheme.shapes.large,
    showCancelButton: Boolean = true,
    cancelButtonText: String,
    cancelButtonColor: Color = MaterialTheme.colorScheme.onSurface,
    onCancel: () -> Unit = {},
    showConfirmButton: Boolean = true,
    confirmButtonText: String,
    confirmButtonColor: Color = MaterialTheme.colorScheme.primary,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    AlertDialog(
        modifier = modifier,
        shape = shape,
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(28.dp)
                    )
                }
                title?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 22.sp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start
            )
        },
        confirmButton = {
            if (showConfirmButton) {
                Button(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = confirmButtonColor
                    )
                ) {
                    Text(
                        text = confirmButtonText,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        },
        dismissButton = {
            if (showCancelButton) {
                TextButton(
                    onClick = onCancel,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = cancelButtonColor
                    )
                ) {
                    Text(
                        text = cancelButtonText,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
fun CustomDialogPreview() {
    CustomDialog(
        icon = Icons.Filled.SignalWifiStatusbarConnectedNoInternet4,
        title = "Error de conexión",
        message = "Ocurrió un error al intentar conectar con el servidor. Por favor, verifica tu conexión a internet.",
        cancelButtonText = "Cancel",
        confirmButtonText = "Confirm",
        showCancelButton = false,
        onCancel = {},
        onConfirm = {}
    )
}