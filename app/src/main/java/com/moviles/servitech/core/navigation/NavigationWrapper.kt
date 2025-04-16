package com.moviles.servitech.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moviles.servitech.view.HomeScreen
import com.moviles.servitech.view.SplashScreen
import com.moviles.servitech.view.auth.LoginScreen
import com.moviles.servitech.view.auth.RegisterScreen

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Splash) {
        composable<Splash> {
            SplashScreen(
                navigateToHome = { navController.navigate(Home) { popUpTo(0) } },
                navigateToLogin = { navController.navigate(Login) { popUpTo(0) } }
            )
        }
        composable<Login> {
            LoginScreen (
                navigateToSignUp = { navController.navigate(Register) },
                navigateToHome = { navController.navigate(Home) { popUpTo(0) } }
            )
        }
        composable<Register> {
            RegisterScreen(
                navigateToLogin = { navController.navigate(Login) { popUpTo(0) } },
//                navigateToHome = { navController.navigate(Home) { popUpTo(0) } }
            )
        }
        composable<Home> {
            HomeScreen(
                navigateToLogin = { navController.navigate(Login) { popUpTo(0) } }
            )
        }
    }
}