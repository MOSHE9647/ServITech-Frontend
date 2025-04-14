package com.moviles.servitech.network

import android.content.Context
import com.moviles.servitech.network.services.providers.AndroidStringProvider
import com.moviles.servitech.network.services.providers.StringProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideStringProvider(@ApplicationContext context: Context): StringProvider {
        return AndroidStringProvider(context)
    }

}