package com.moviles.servitech.common

object Constants {
    // Base URLs for the API
    const val API_BASE_URL = "http://10.0.2.2:8000/"
    const val API_AUTH_ROUTE = "api/v1/auth"
    const val API_BASE_ROUTE = "api/v1"

    // API endpoints routes
    const val API_ARTICLES_ROUTE = "${API_BASE_ROUTE}/articles"
    const val API_REPAIR_REQUESTS_ROUTE = "${API_BASE_ROUTE}/repair-request"

    // Home constants
    const val CAT_TECHNOLOGY = "tecnologia"
    const val CAT_SUPPORT = "soporte"
    const val CAT_ANIME = "anime"

    // Database constants
    const val DATABASE_NAME = "servitech_database.db"
    const val IMAGES_TABLE = "images"
    const val PEND_OP_TABLE = "pending_operations"
    const val REPAIR_REQ_TABLE = "repair_requests"
    const val SESSION_TABLE = "user_session"
    const val USER_TABLE = "users"

    // User roles
    const val GUEST_ROLE = "guest"
}