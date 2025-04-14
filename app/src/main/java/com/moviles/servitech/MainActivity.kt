package com.moviles.servitech

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.moviles.servitech.core.navigation.NavigationWrapper
import com.moviles.servitech.ui.theme.ServITechTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ServITechTheme(dynamicColor = false) {
                NavigationWrapper()
            }
        }
    }
}