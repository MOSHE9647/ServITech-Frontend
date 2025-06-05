package com.moviles.servitech.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.moviles.servitech.database.dao.PendingOperationDao
import com.moviles.servitech.database.dao.UserSessionDao
import com.moviles.servitech.database.entities.PendingOperationEntity
import com.moviles.servitech.database.entities.UserSessionEntity

/**
 * Main database class for the ServITech application.
 * It defines the database version, entities, and DAOs.
 * This class is annotated with @Database to indicate that it is a Room database.
 *
 * @Database annotation specifies the entities that are part of the database,
 * the version of the database, and whether to export the schema.
 *
 * The DAOs (Data Access Objects) are defined as abstract methods,
 * allowing Room to generate the necessary code for database operations.
 *
 * @Database version is set to 1, indicating the initial version of the database.
 * The exportSchema is set to false, meaning the schema will not be exported
 * to a file, which is useful for development purposes.
 */
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