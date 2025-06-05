package com.moviles.servitech.core.providers

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * The `StringProvider` interface defines methods for retrieving strings from resources.
 * It allows for getting a string by its resource ID, with an optional format argument.
 */
interface StringProvider {
    fun getString(resId: Int): String
    fun getString(resId: Int, vararg args: Any): String
}

/**
 * The `AndroidStringProvider` class implements the `StringProvider` interface.
 * It is responsible for providing string resources from the Android context.
 * It is annotated with `@Inject` to enable dependency injection using Dagger Hilt.
 *
 * @param context The Android context used to access string resources.
 * This class is injected into other components that require string resources.
 */
class AndroidStringProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : StringProvider {
    /**
     * Retrieves a string resource by its ID.
     *
     * @param resId The resource ID of the string to retrieve.
     * @return The string associated with the specified resource ID.
     */
    override fun getString(resId: Int): String = context.getString(resId)

    /**
     * Retrieves a formatted string resource by its ID, with optional arguments.
     *
     * @param resId The resource ID of the string to retrieve.
     * @param args Optional arguments to format the string.
     * @return The formatted string associated with the specified resource ID.
     */
    override fun getString(resId: Int, vararg args: Any): String = context.getString(resId, *args)
}