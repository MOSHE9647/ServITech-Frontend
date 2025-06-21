package com.moviles.servitech.model.mappers

import android.content.Context
import android.net.Uri
import com.moviles.servitech.common.Utils.uriToMultipart
import com.moviles.servitech.database.entities.ImageEntity
import com.moviles.servitech.model.Image
import com.moviles.servitech.network.responses.ImageResponse
import java.io.File

fun Image.toEntity(imageableType: String, imageableId: Long): ImageEntity {
    return ImageEntity(
        id = this.id,
        imageableType = imageableType,
        imageableId = imageableId,
        filePath = this.path,
        path = this.path,
        title = this.title,
        alt = this.alt
    )
}

fun ImageEntity.toModel(context: Context? = null): Image {
    return Image(
        id = this.id,
        file = this.filePath?.let { path ->
            when {
                path.isEmpty() -> null // Return null if the path is empty
                context == null -> null // Return null if context is not provided
                else -> {
                    val file = File(context.cacheDir, path)
                    when {
                        file.exists() -> uriToMultipart(context, Uri.fromFile(file))
                        else -> null // Return null if the file doesn't exist
                    }
                }
            }
        },
        title = this.title,
        path = this.path,
        alt = this.alt
    )
}

fun ImageResponse.toModel(): Image {
    return Image(
        id = null,
        file = null,
        title = this.title,
        path = this.path,
        alt = this.alt
    )
}

fun List<Image>.toEntityList(imageableType: String, imageableId: Long): List<ImageEntity> {
    return this.map { it.toEntity(imageableType, imageableId) }
}

fun List<ImageResponse>.toModelList(): List<Image> {
    return this.map { it.toModel() }
}