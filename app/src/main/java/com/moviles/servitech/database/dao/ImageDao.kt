package com.moviles.servitech.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.moviles.servitech.common.Constants.IMAGES_TABLE
import com.moviles.servitech.database.entities.ImageEntity

@Dao
interface ImageDao {

    @Query("SELECT * FROM $IMAGES_TABLE")
    suspend fun getAllRepairRequests(): List<ImageEntity>

    @Query("SELECT * FROM $IMAGES_TABLE WHERE imageableType = :type AND imageableId = :id")
    suspend fun getImagesByTypeAndId(type: String, id: Int): List<ImageEntity>

    @Query("SELECT * FROM $IMAGES_TABLE WHERE id = :id")
    suspend fun getImageById(id: Int): ImageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<ImageEntity>): List<Long>

    @Update
    suspend fun updateImage(image: ImageEntity): Int

    @Delete
    suspend fun deleteImage(image: ImageEntity)

    @Query("DELETE FROM $IMAGES_TABLE WHERE id = :id")
    suspend fun deleteImageById(id: Int)

    @Query("DELETE FROM $IMAGES_TABLE")
    suspend fun deleteAllImages()

}