package com.moviles.servitech.database.entities.repairRequest

import androidx.room.Embedded
import androidx.room.Relation
import com.moviles.servitech.database.entities.ImageEntity

data class RepairRequestWithImagesEntity(
    @Embedded val repairRequest: RepairRequestEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "imageableId",
        entity = ImageEntity::class,
    )
    val images: List<ImageEntity>? = emptyList()
)
