package com.moviles.servitech.core.di

import android.content.Context
import androidx.room.Room
import com.moviles.servitech.common.Constants.DATABASE_NAME
import com.moviles.servitech.database.AppDatabase
import com.moviles.servitech.database.dao.PendingOperationDao
import com.moviles.servitech.database.dao.UserSessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()

    @Provides
    fun providePendingOperationDao(database: AppDatabase): PendingOperationDao = database.pendingOperationDao()

    @Provides
    fun provideUserSessionDao(database: AppDatabase): UserSessionDao = database.userSessionDao()

}