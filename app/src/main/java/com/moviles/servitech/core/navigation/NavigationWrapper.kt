package com.moviles.servitech.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moviles.servitech.view.LoginScreen
import com.moviles.servitech.viewmodel.LoginViewModel

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Login) {
        composable<Login> {
            LoginScreen(LoginViewModel())
        }

        composable<Home> {
            // HomeScreen()
        }
    }
}