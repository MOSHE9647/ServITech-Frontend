package com.moviles.servitech.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moviles.servitech.view.HomeScreen
import com.moviles.servitech.view.SplashScreen
import com.moviles.servitech.view.auth.LoginScreen
import com.moviles.servitech.view.auth.RegisterScreen
import kotlinx.serialization.Serializable

sealed class Screen() {
    @Serializable object Splash
    @Serializable object Login
    @Serializable object Register
    @Serializable object Home
}

@Composable
fun NavigationGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash) {
        composable<Screen.Splash> {
            SplashScreen(
                navigateToHome = { navController.navigate(Screen.Home) { popUpTo(0) } },
                navigateToLogin = { navController.navigate(Screen.Login) { popUpTo(0) } }
            )
        }
        composable<Screen.Login> {
            LoginScreen (
                navigateToSignUp = { navController.navigate(Screen.Register) },
                navigateToHome = { navController.navigate(Screen.Home) { popUpTo(0) } }
            )
        }
        composable<Screen.Register> {
            RegisterScreen(
                navigateToLogin = { navController.navigate(Screen.Login) { popUpTo(0) } },
//                navigateToHome = { navController.navigate(Screen.Home) { popUpTo(0) } }
            )
        }
        composable<Screen.Home> {
            HomeScreen(
                navigateToLogin = { navController.navigate(Screen.Login) { popUpTo(0) } }
            )
        }
    }
}