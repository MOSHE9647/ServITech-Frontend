package com.moviles.servitech.common

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.moviles.servitech.R
import com.moviles.servitech.core.di.SessionManagerEntryPoint
import com.moviles.servitech.core.providers.AndroidStringProvider
import com.moviles.servitech.core.session.SessionManager
import dagger.hilt.android.EntryPointAccessors
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/**
 * Utility object for common functions used across the application.
 * This object provides methods to manage user sessions and format
 * dates, among other utilities.
 */
object Utils {
    /**
     * Displays a remote image using Coil's AsyncImage composable.
     * The image is loaded from the provided URL and displayed
     * with a fixed height and width, cropping to fit the box.
     *
     * @param imageUrl The URL of the image to be displayed.
     */
    @Composable
    fun RemoteImage(imageUrl: String) {
//        val imagePath = imageUrl.replace("localhost", "10.0.2.2")
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp), // Set fixed height
            contentScale = ContentScale.Fit // Crop to fit the box
        )
    }

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

    /**
     * Formats a given date string in ISO 8601 format (yyyy-MM-dd) to a human-readable string.
     * The output format depends on the specified language.
     *
     * @param dateString The date string in ISO 8601 format.
     * @param language The language code for formatting (default is "es" for Spanish).
     * @return A formatted date string in the specified language.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatLocalDateForDisplay(dateString: String, language: String = "es"): String {
        if (dateString.isEmpty()) return dateString

        try {
            val locale = when (language.lowercase()) {
                "en" -> Locale.ENGLISH
                "es" -> Locale("es", "ES")
                else -> Locale.getDefault()
            }

            val date = LocalDate.parse(dateString) // ISO 8601 format: yyyy-MM-dd

            val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.FULL, locale)
            val dayOfMonth = date.dayOfMonth
            val month = date.month.getDisplayName(TextStyle.FULL, locale)
            val year = date.year

            return when (locale.language) {
                "en" -> "$dayOfWeek $dayOfMonth${getDaySuffix(dayOfMonth)}, $year"
                "es" -> "$dayOfWeek, $dayOfMonth de $month del $year"
                else -> "$dayOfWeek, $dayOfMonth $month $year"
            }
        } catch (e: Exception) {
            Log.e("Utils", "Error parsing date: $dateString", e)
            return dateString // Return the original string if parsing fails
        }
    }

    /**
     * Returns the appropriate suffix for a given day of the month.
     * This is used to format dates correctly in English.
     *
     * @param day The day of the month (1-31).
     * @return A string representing the suffix for the day (e.g., "st", "nd", "rd", "th").
     */
    private fun getDaySuffix(day: Int): String {
        return if (day in 11..13) {
            "th"
        } else {
            when (day % 10) {
                1 -> "st"
                2 -> "nd"
                3 -> "rd"
                else -> "th"
            }
        }
    }

    /**
     * Gets the display language of the device.
     * This function retrieves the primary language
     * set on the device's configuration.
     *
     * @param context The context used to access resources.
     * @return A string representing the display language (e.g., "en", "es").
     */
    fun getDisplayLanguage(context: Context): String {
        return context.resources.configuration.locales[0].language
    }

    /**
     * Converts a text string to a RequestBody with "text/plain" media type.
     *
     * @return A RequestBody containing the text.
     */
    val textToRequestBody =
        { text: String -> text.toRequestBody("text/plain".toMediaTypeOrNull()) }

    /**
     * Converts a Double value to a RequestBody with "text/plain" media type.
     * This is useful for sending numeric values in API requests.
     *
     * @return A RequestBody containing the string representation of the Double.
     */
    val doubleToRequestBody =
        { number: Double -> number.toString().toRequestBody("text/plain".toMediaTypeOrNull()) }

    /**
     * Retrieves the file path from a URI.
     * This function checks if the URI is a content URI or a file URI,
     * and retrieves the file path accordingly.
     *
     * @param uri The URI of the file.
     * @param context The context used to access the content resolver.
     */
    fun getFilePathFromUri(uri: Uri, context: Context): String? {
        var filePath: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)

        cursor?.use {
            val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst()) {
                val fileName = cursor.getString(columnIndex)
            }
        }

        if (uri.scheme == "content") {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(uri, projection, null, null, null)
            cursor?.use {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex)
                }
            }
        } else if (uri.scheme == "file") {
            filePath = uri.path
        }

        return filePath
    }

    /**
     * Converts a URI to a MultipartBody.Part for file uploads.
     * This function retrieves the MIME type of the file from the URI,
     * reads the file content, and creates a MultipartBody.Part
     * for use in API requests.
     *
     * @param context The context used to access the content resolver.
     * @param uri The URI of the file to be uploaded.
     * @param fieldName The name of the form field for the file upload.
     * @return A MultipartBody.Part representing the file, or null if an error occurs.
     */
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

    fun getFileNameAndExtension(uri: Uri, contentResolver: ContentResolver): String {
        contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                    return cursor.getString(nameIndex)
                }
            }
        return ""
    }

    /**
     * Logs an informational message with the class name as the tag.
     * This method uses the Android Log class to log debug messages.
     *
     * @param className The name of the class where the log is being made.
     * @param stringProvider The AndroidStringProvider used to retrieve the message string.
     * @param messageResId The resource ID of the message to log.
     * @param args Optional arguments to format the message.
     */
    fun logInfo(
        stringProvider: AndroidStringProvider,
        className: String,
        messageResId: Int,
        vararg args: Any?,
    ) {
        Log.d(className, stringProvider.getString(messageResId, args))
    }

    /**
     * Logs an error message with the class name as the tag.
     * This method uses the Android Log class to log error messages.
     *
     * @param throwable An optional Throwable to log with the error message.
     * @param messageResId The resource ID of the error message to log.
     * @param args Optional arguments to format the error message.
     */
    fun logError(
        stringProvider: AndroidStringProvider,
        className: String,
        throwable: Throwable? = null,
        messageResId: Int,
        vararg args: Any?
    ) {
        Log.e(className, stringProvider.getString(messageResId, args), throwable)
    }
}