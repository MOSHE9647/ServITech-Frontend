package com.moviles.servitech.core.di

import android.content.Context
import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * The `NetworkModule` provides the necessary dependencies related to network connectivity.
 * It is annotated with `@Module` to indicate that it is a Dagger module,
 * and `@InstallIn(SingletonComponent::class)` to specify that the provided dependencies
 * will be available in the singleton scope of the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides the `ConnectivityManager` instance for the application.
     * This allows access to network connectivity information and management.
     *
     * @param context The application context used to retrieve the system service.
     * @return The `ConnectivityManager` instance.
     */
    @Provides
    @Singleton
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

}