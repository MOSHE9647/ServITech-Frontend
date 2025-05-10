package com.moviles.servitech.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.moviles.servitech.database.dao.PendingOperationDao
import com.moviles.servitech.database.dao.UserSessionDao
import com.moviles.servitech.database.entities.PendingOperationEntity
import com.moviles.servitech.database.entities.UserSessionEntity

@Database(
    entities = [
        PendingOperationEntity::class,
        UserSessionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pendingOperationDao(): PendingOperationDao
    abstract fun userSessionDao(): UserSessionDao
}