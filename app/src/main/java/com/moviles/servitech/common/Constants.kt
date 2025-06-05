package com.moviles.servitech.common

object Constants {
    // Base URLs for the API and image storage
    const val API_BASE_URL = "http://10.0.2.2:8000/"
    const val IMAGES_BASE_URL = API_BASE_URL + "storage/"

    // API route for authentication
    const val API_AUTH_ROUTE = "api/v1/auth"

    // API routes for
    const val API_ARTICLES_ROUTE = "api/v1/articles"

    // Endpoints for articles and repair requests images
    const val ARTICLE_IMAGE_PATH = "${IMAGES_BASE_URL}/articles/"
    const val REP_REQUEST_IMAGE_PATH = "${IMAGES_BASE_URL}/requests/"

    // Home constants
    const val CAT_TECHNOLOGY = "tecnologia"
    const val CAT_SUPPORT = "soporte"
    const val CAT_ANIME = "anime"

    // Database constants
    const val DATABASE_NAME = "servitech_database.db"
    const val PEND_OP_TABLE = "pending_operations"
    const val SESSION_TABLE = "user_session"
    const val USER_TABLE = "users"

    // User roles
    const val GUEST_ROLE = "guest"
}