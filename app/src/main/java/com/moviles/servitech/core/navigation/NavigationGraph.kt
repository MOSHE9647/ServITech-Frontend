package com.moviles.servitech.core.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.moviles.servitech.common.Constants.CAT_TECHNOLOGY
import com.moviles.servitech.core.navigation.Screen.Detail
import com.moviles.servitech.core.navigation.Screen.ForgotPassword
import com.moviles.servitech.core.navigation.Screen.Home
import com.moviles.servitech.core.navigation.Screen.Login
import com.moviles.servitech.core.navigation.Screen.Register
import com.moviles.servitech.core.navigation.Screen.Splash
import com.moviles.servitech.view.RepairRequestScreen
import com.moviles.servitech.view.SplashScreen
import com.moviles.servitech.view.article.ArticleDetailScreen
import com.moviles.servitech.view.article.CategoryScreen
import com.moviles.servitech.view.auth.ForgotPasswordScreen
import com.moviles.servitech.view.auth.LoginScreen
import com.moviles.servitech.view.auth.RegisterScreen
import com.moviles.servitech.view.support.SupportRequestScreen
import kotlinx.serialization.Serializable

/**
 * The `Screen` sealed class defines the different screens in the application.
 * Each screen is represented as an object or data class.
 * The `@Serializable` annotation is used to indicate that these classes can be serialized.
 * This is useful for passing arguments between screens in the navigation graph.
 */
sealed class Screen() {
    @Serializable object Splash
    @Serializable object Login
    @Serializable object Register
    @Serializable object Home

    @Serializable
    object RepairRequest

    @Serializable
    data class Detail(val articleId: Int, val categoryName: String)

    @Serializable
    object ForgotPassword
    object SupportRequest
}

/**
 * This function sets up the navigation graph for the application.
 * It defines the different screens and their corresponding routes.
 * The `NavHost` is used to manage the navigation within the app.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph() {
    // NavController instance that will be used to manage navigation within the app.
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Splash) {
        /**
         * This is the splash screen of the application.
         * It is the first screen that users see when they open the app.
         * Here it checks if the user is logged in and navigates to the appropriate screen.
         *
         * If the user is logged in, it navigates to the `Home` (Category) screen.
         * If the user is not logged in, it navigates to the `Login` screen.
         *
         * The `Splash` object is used to define the route for this screen.
         * The `composable` function is used to define the screen and its corresponding UI.
         */
        composable<Splash> {
            SplashScreen(
                navigateToHome = { navController.navigate(Home) { popUpTo(0) } },
                navigateToLogin = { navController.navigate(Login) { popUpTo(0) } }
            )
        }

        /**
         * This is the login screen of the application.
         * It allows users to log in to their accounts.
         * If the user is not registered, they can navigate to the `Register` screen.
         * If the login is successful, it navigates to the `Home` screen.
         *
         * The `Login` object is used to define the route for this screen.
         * The `composable` function is used to define the screen and its corresponding UI.
         */
        composable<Login> {
            LoginScreen (
                navigateToSignUp = { navController.navigate(Register) },
                navigateToHome = { navController.navigate(Home) { popUpTo(0) } },
                navigateToForgotPassword = { navController.navigate(ForgotPassword) }
            )
        }

        /**
         * This is the registration screen of the application.
         * It allows users to create a new account.
         * After successful registration, users can navigate to the `Login` screen.
         *
         * The `Register` object is used to define the route for this screen.
         * The `composable` function is used to define the screen and its corresponding UI.
         */
        composable<Register> {
            RegisterScreen(
                navigateToLogin = { navController.navigate(Login) { popUpTo(0) } }
            )
        }

        /**
         * This is the forgot password screen of the application.
         * It allows users to reset their password if they have forgotten it.
         * After resetting the password, users can navigate back to the `Login` screen.
         *
         * The `ForgotPassword` object is used to define the route for this screen.
         * The `composable` function is used to define the screen and its corresponding UI.
         */
        composable<ForgotPassword> {
            ForgotPasswordScreen(
                navigateToLogin = { navController.navigate(Login) { popUpTo(0) } }
            )
        }

        /**
         * This is the Home screen of the application.
         * It displays a list of articles categorized by different topics.
         * Users can navigate to the `Detail` screen by clicking on an article.
         *
         * The `Home` object is used to define the route for this screen.
         * The `composable` function is used to define the screen and its corresponding UI.
         */
        composable<Home> {
            var selectedCategory by remember { mutableStateOf(CAT_TECHNOLOGY) }

            CategoryScreen(
                selectedCategory = selectedCategory,
                onCategoryChange = { selectedCategory = it },
                navigateToRepairRequests = {
                    navController.navigate(Screen.RepairRequest) {
                        popUpTo(0)
                    }
                },
                navigateToDetail = { articleId ->
                    navController.navigate(
                        Detail(
                            articleId = articleId,
                            categoryName = selectedCategory
                        )
                    )
                },
                navController = navController,
                navigateToLogin = { navController.navigate(Login) { popUpTo(0) } },
                logoutViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            )
        }

        /**
         * This is the Repair Request screen of the application.
         * It allows users to create and manage repair requests.
         * Users can navigate back to the `Home` screen after managing their repair requests.
         *
         * The `RepairRequest` object is used to define the route for this screen.
         * The `composable` function is used to define the screen and its corresponding UI.
         */
        composable<Screen.RepairRequest> {
            RepairRequestScreen(
                navigateBack = { navController.navigate(Home) }
            )
        }

        /**
         * This is the Detail screen of the application.
         * It displays detailed information about a specific article.
         * Users can navigate back to the `Home` screen after viewing the article details.
         *
         * It takes an `articleId` as an argument to fetch the details of the article.
         *
         * The `Detail` object is used to define the route for this screen, which includes an article ID.
         * The `composable` function is used to define the screen and its corresponding UI.
         */
        composable<Detail> { backStackEntry ->
            val args = backStackEntry.toRoute<Detail>()

            ArticleDetailScreen(
                articleId = args.articleId,
                currentCategory = args.categoryName,
                navController = navController,
                navigateBack = { navController.popBackStack() }
            )
        }

        composable("SupportRequest") {
            SupportRequestScreen(
                onNavigateBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }
    }
}