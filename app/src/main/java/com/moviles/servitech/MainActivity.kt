package com.moviles.servitech

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.moviles.servitech.ui.theme.ServITechTheme
import com.moviles.servitech.view.LoginScreen
import com.moviles.servitech.viewmodel.LoginViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ServITechTheme {
                LoginScreen(LoginViewModel())
            }
        }
    }
}