package com.moviles.servitech.core.navigation

sealed class Screen(val route: String) {
    object Splash   : Screen("splash")
    object Login    : Screen("login")
    object Register : Screen("register")
    // get("/category/{category}") generyc route
    object Category : Screen("category/{category}") {
        fun createRoute(cat: String) = "category/$cat"
    }
    object Detail   : Screen("article/{articleId}") {
        fun createRoute(id: Int) = "article/$id"
    }
}
