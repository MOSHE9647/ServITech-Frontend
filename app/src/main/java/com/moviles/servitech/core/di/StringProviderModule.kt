package com.moviles.servitech.core.di

import android.content.Context
import com.moviles.servitech.core.providers.AndroidStringProvider
import com.moviles.servitech.core.providers.StringProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StringProviderModule {

    @Provides
    @Singleton
    fun provideStringProvider(@ApplicationContext context: Context): StringProvider {
        return AndroidStringProvider(context)
    }
}