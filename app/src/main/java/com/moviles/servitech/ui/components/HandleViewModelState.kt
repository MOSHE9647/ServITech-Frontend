package com.moviles.servitech.ui.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.zIndex
import com.moviles.servitech.viewmodel.utils.ViewModelState

@Composable
fun HandleViewModelState(state: ViewModelState?, logTag: String) {
    when (state) {
        is ViewModelState.Loading -> {
            LoadingIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f),
                withBlurBackground = true,
                isVisible = true
            )
        }

        is ViewModelState.Success<*> -> {
            Log.d(logTag, "ViewModelState Data: ${state.data}")
            val context = LocalContext.current
            LaunchedEffect(state) {
                Toast.makeText(
                    context,
                    "Operation successful",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        is ViewModelState.Error -> {
            HandleServerError(logTag, state.message)
        }

        else -> {}
    }
}