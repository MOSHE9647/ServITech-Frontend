package com.moviles.servitech.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.moviles.servitech.common.Constants.IMAGES_TABLE

@Entity(tableName = IMAGES_TABLE)
data class ImageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val imageableType: String,
    val imageableId: Long,
    val filePath: String? = null,
    val path: String,
    val title: String? = null,
    val alt: String? = null,
)
