package com.moviles.servitech.common

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.moviles.servitech.core.di.SessionManagerEntryPoint
import dagger.hilt.android.EntryPointAccessors
import com.moviles.servitech.core.session.SessionManager

@Composable
fun rememberSessionManager(context: Context): SessionManager {
    return remember {
        EntryPointAccessors.fromApplication(
            context,
            SessionManagerEntryPoint::class.java
        ).sessionManager()
    }
}