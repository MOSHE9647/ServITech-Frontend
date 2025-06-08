package com.moviles.servitech.common

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.moviles.servitech.R
import com.moviles.servitech.core.di.SessionManagerEntryPoint
import com.moviles.servitech.core.session.SessionManager
import dagger.hilt.android.EntryPointAccessors
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

/**
 * Utility object for common functions used across the application.
 * This object provides methods to manage user sessions and format
 * dates, among other utilities.
 */
object Utils {
    /**
     * Creates a [SessionManager] instance using Hilt's EntryPointAccessors.
     * This function is used to access the SessionManager
     * from a Composable function
     *
     * @param context The application context used to access the SessionManager.
     * @return A [SessionManager] instance that can be used to manage user sessions.
     */
    @Composable
    fun rememberSessionManager(context: Context): SessionManager {
        return remember {
            EntryPointAccessors.fromApplication(
                context,
                SessionManagerEntryPoint::class.java
            ).sessionManager()
        }
    }

    /**
     * Formats a given epoch time in milliseconds to a human-readable date string.
     * If the epoch time is 0, it returns "No date".
     *
     * @param epochMillis The epoch time in milliseconds to format.
     * @param context The context used to access string resources.
     * @return A formatted date string or "No date" if the epoch time is 0.
     */
    @SuppressLint("SimpleDateFormat")
    fun convertMillisInDate(epochMillis: Long, context: Context): String {
        return if (epochMillis != 0L) {
            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            sdf.format(java.util.Date(epochMillis))
        } else {
            context.getString(R.string.no_date)
        }
    }

    val textToRequestBody =
        { text: String -> text.toRequestBody("text/plain".toMediaTypeOrNull()) }

    val doubleToRequestBody =
        { number: Double -> number.toString().toRequestBody("text/plain".toMediaTypeOrNull()) }

    fun uriToMultipart(
        context: Context,
        uri: Uri,
        fieldName: String = "images[]"
    ): MultipartBody.Part? {
        return try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri)
                ?: MimeTypeMap.getFileExtensionFromUrl(uri.toString())?.let {
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension(it)
                } ?: "application/octet-stream"

            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val file = File.createTempFile("upload_", null, context.cacheDir)
            file.outputStream().use { inputStream.copyTo(it) }

            val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
            MultipartBody.Part.createFormData(fieldName, file.name, requestBody)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}