package com.moviles.servitech.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moviles.servitech.view.HomeScreen
import com.moviles.servitech.view.SplashScreen
import com.moviles.servitech.view.auth.LoginScreen

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen { screen -> navController.navigate(screen.toString()) { popUpTo(0) } }
        }
        composable(Screen.Login.route) {
            LoginScreen {
                val screen = Screen.Home.route
                navController.navigate(screen) { popUpTo(0) }
            }
        }
        composable(Screen.Home.route) {
            HomeScreen { screen -> navController.navigate(screen) { popUpTo(0) } }
        }
    }
}