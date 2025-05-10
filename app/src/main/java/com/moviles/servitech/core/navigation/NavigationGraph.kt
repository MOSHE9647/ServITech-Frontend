package com.moviles.servitech.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.moviles.servitech.core.navigation.Screen.Detail
import com.moviles.servitech.view.SplashScreen
import com.moviles.servitech.view.article.ArticleDetailScreen
import com.moviles.servitech.view.article.CategoryScreen
import com.moviles.servitech.view.auth.LoginScreen
import com.moviles.servitech.view.auth.RegisterScreen
import kotlinx.serialization.Serializable

sealed class Screen() {
    @Serializable object Splash
    @Serializable object Login
    @Serializable object Register
    @Serializable object Category
    @Serializable data class Detail(val articleId: Int)
}

@Composable
fun NavigationGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash) {
        composable<Screen.Splash> {
            SplashScreen(
                navigateToHome = { navController.navigate(Screen.Category) { popUpTo(0) } },
                navigateToLogin = { navController.navigate(Screen.Login) { popUpTo(0) } }
            )
        }
        composable<Screen.Login> {
            LoginScreen (
                navigateToSignUp = { navController.navigate(Screen.Register) },
                navigateToHome = { navController.navigate(Screen.Category) { popUpTo(0) } }
            )
        }
        composable<Screen.Register> {
            RegisterScreen(
                navigateToLogin = { navController.navigate(Screen.Login) { popUpTo(0) } }
            )
        }
        composable<Screen.Category> {
            CategoryScreen(
                navigateToDetail = { articleId -> navController.navigate(Detail(articleId = articleId)) }
            )
        }
        composable<Detail> { backStackEntry ->
            val articleDetail: Detail = backStackEntry.toRoute()
            ArticleDetailScreen(articleDetail.articleId) {
                navController.navigate(Screen.Category) {
                    popUpTo<Screen.Category> { inclusive = true }
                }
            }
        }
    }
}