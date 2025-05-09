package com.moviles.servitech.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.moviles.servitech.view.auth.LoginScreen
import com.moviles.servitech.view.auth.RegisterScreen
import com.moviles.servitech.view.SplashScreen
import com.moviles.servitech.view.article.CategoryScreen
import com.moviles.servitech.view.article.ArticleDetailScreen

// … tus imports …

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(
                navigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                navigateToHome = {
                    navController.navigate(Screen.Category.createRoute("tecnologia")) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // 2) Login
        composable(Screen.Login.route) {
            LoginScreen(
                navigateToSignUp = {
                    navController.navigate(Screen.Register.route)
                },
                navigateToHome = {
                    navController.navigate(Screen.Category.createRoute("tecnologia")) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // 3) Register
        composable(Screen.Register.route) {
            RegisterScreen(
                navigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // 4) Category (lista con tabs + búsqueda)
        composable(
            Screen.Category.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { back ->
            val cat = back.arguments!!.getString("category")!!
            CategoryScreen(
                category         = cat,
                navigateToDetail = { id ->
                    navController.navigate(Screen.Detail.createRoute(id))
                }
            )
        }

        // 5) Detalle de artículo
        composable(
            Screen.Detail.route,
            arguments = listOf(navArgument("articleId") { type = NavType.IntType })
        ) { back ->
            val id = back.arguments!!.getInt("articleId")
            ArticleDetailScreen(
                articleId   = id,
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}
