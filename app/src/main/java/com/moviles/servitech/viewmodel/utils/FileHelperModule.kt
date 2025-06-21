package com.moviles.servitech.viewmodel.utils

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FileHelperModule {

    @Provides
    @Singleton
    fun provideFileHelper(@ApplicationContext context: Context): FileHelper {
        return FileHelper(context)
    }
}