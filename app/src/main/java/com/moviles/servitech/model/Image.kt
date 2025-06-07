package com.moviles.servitech.model

import okhttp3.MultipartBody

data class Image(
    val id: Int? = null,
    val file: MultipartBody.Part? = null,
    val title: String? = "",
    val alt: String? = "",
    val path: String,
)
