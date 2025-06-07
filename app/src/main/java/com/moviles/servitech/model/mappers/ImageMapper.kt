package com.moviles.servitech.model.mappers

import com.moviles.servitech.model.Image
import com.moviles.servitech.network.responses.ImageResponse

fun ImageResponse.toModel(): Image {
    return Image(
        id = null,
        file = null,
        title = this.title,
        path = this.path,
        alt = this.alt
    )
}

fun List<ImageResponse>.toModelList(): List<Image> {
    return this.map { it.toModel() }
}