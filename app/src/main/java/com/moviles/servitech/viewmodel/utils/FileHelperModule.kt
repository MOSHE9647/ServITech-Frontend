package com.moviles.servitech.viewmodel.utils

import android.content.Context
import com.moviles.servitech.viewmodel.utils.FileHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
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