package com.focusstart.imgurupload

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/*
* Класс для осуществления операций
* над Uri и File
* */
object UriFileResolver {

    // Получение uri для нового фото
    fun generatePhotoUri(context: Context): Uri? {
        val fileName = "img_" + System.currentTimeMillis() + ".jpg"
        val file = File(context.getExternalFilesDir(null), fileName)
        return FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            file
        )
    }

    // Получение файла через его uri с помощью contentResolver
    fun toFile(imageUri: Uri, context: Context, contentResolver: ContentResolver): File? {
        val parcelFileDescriptor =
            contentResolver.openFileDescriptor(imageUri, "r", null)
                ?: return null

        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(context.cacheDir, contentResolver.getFileName(imageUri))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        return file
    }

    // Получение имени файла
    private fun ContentResolver.getFileName(fileUri: Uri): String {
        var name = ""
        val returnCursor = this.query(
            fileUri,
            null,
            null,
            null,
            null)
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            name = returnCursor.getString(nameIndex)
            returnCursor.close()
        }
        return name
    }
}