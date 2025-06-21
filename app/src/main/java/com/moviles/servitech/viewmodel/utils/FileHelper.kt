package com.moviles.servitech.viewmodel.utils


import android.content.Context
import android.net.Uri
import jakarta.inject.Inject
import java.io.File
import java.io.FileOutputStream

class FileHelper @Inject constructor(private val context: Context) {
    fun getFileFromUri(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
        inputStream?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }
}
