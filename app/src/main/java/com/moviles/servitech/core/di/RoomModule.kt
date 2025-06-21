package com.moviles.servitech.core.di

import android.content.Context
import androidx.room.Room
import com.moviles.servitech.common.Constants.DATABASE_NAME
import com.moviles.servitech.database.AppDatabase
import com.moviles.servitech.database.dao.ImageDao
import com.moviles.servitech.database.dao.PendingOperationDao
import com.moviles.servitech.database.dao.RepairRequestDao
import com.moviles.servitech.database.dao.UserSessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * This module provides the Room database and its DAOs.
 * It is annotated with `@Module` to indicate that it provides dependencies,
 * and `@InstallIn(SingletonComponent::class)` to specify that these dependencies
 * should be available in the singleton scope of the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    /**
     * Provides the Room database instance.
     * The `@Singleton` annotation ensures that only one instance of the database
     * is created and shared throughout the application.
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()

    /**
     * Provides the PendingOperationDao instance.
     * This DAO is used to interact with the pending operations table in the database.
     */
    @Provides
    fun providePendingOperationDao(database: AppDatabase): PendingOperationDao = database.pendingOperationDao()

    /**
     * Provides the UserSessionDao instance.
     * This DAO is used to interact with the user session table in the database.
     */
    @Provides
    fun provideUserSessionDao(database: AppDatabase): UserSessionDao = database.userSessionDao()

    /**
     * Provides the RepairRequestDao instance.
     * This DAO is used to interact with the repair requests table in the database.
     */
    @Provides
    fun provideRepairRequestDao(database: AppDatabase): RepairRequestDao =
        database.repairRequestDao()

    /**
     * Provides the ImageDao instance.
     * This DAO is used to interact with the images table in the database.
     */
    @Provides
    fun provideImageDao(database: AppDatabase): ImageDao = database.imageDao()

}