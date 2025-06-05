package com.moviles.servitech.ui.components

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SignalWifiStatusbarConnectedNoInternet4
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.moviles.servitech.R

@Composable
fun HandleServerError(tag: String, msg: String) {
    Log.d(tag, msg)

    var showDialog by remember { mutableStateOf<Boolean>(true) }
    val connectionErrorMsg = stringResource(R.string.connection_error)
    val isConnectionError = msg.isNotEmpty() && msg == connectionErrorMsg

    val icon =
        if (isConnectionError) Icons.Filled.SignalWifiStatusbarConnectedNoInternet4
        else Icons.Filled.WarningAmber
    val title =
        if (isConnectionError) connectionErrorMsg
        else stringResource(R.string.dialog_error_title)
    val message =
        if (isConnectionError) stringResource(R.string.connection_error_message)
        else msg

    // Handle error message
    if (showDialog) {
        CustomDialog(
            icon = icon,
            title = title,
            message = message,
            cancelButtonText = stringResource(R.string.dialog_cancel),
            confirmButtonText = stringResource(R.string.dialog_confirm),
            showCancelButton = false,
            onConfirm = { showDialog = false },
        )
    }
}