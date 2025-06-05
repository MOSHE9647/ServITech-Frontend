package com.moviles.servitech.core.di

import com.moviles.servitech.core.session.SessionManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Entry point for accessing the SessionManager in Hilt.
 * This allows other parts of the application to retrieve the SessionManager instance
 * without directly depending on Hilt.
 * This is useful for classes that cannot be annotated with @AndroidEntryPoint,
 * such as non-Android classes or classes that are not part of the Hilt dependency graph.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface SessionManagerEntryPoint {
    /**
     * Provides the SessionManager instance.
     * This function can be called to retrieve the SessionManager from the Hilt dependency graph.
     *
     * @return The SessionManager instance.
     */
    fun sessionManager(): SessionManager
}