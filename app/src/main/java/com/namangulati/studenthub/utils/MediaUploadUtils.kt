package com.namangulati.studenthub.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.namangulati.studenthub.API.ApiUploadUtilities
import com.namangulati.studenthub.models.MediaModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

object MediaUploadUtils {

    fun uploadImageToServer(
        context: Context,
        uri: Uri,
        onResult: (MediaModel?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val file = uriToFile(uri, context)
            Log.d("Hello4", "Uploading file: $file")

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("media", file.name, requestFile)

            val response = ApiUploadUtilities.getApiInterface().uploadMedia(body)
            Log.d("Hello5", "Response: ${response.body()}")

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    Log.e("MediaUploadUtils", "Error uploading image: ${response.errorBody()?.string()}")
                    onResult(null)
                }
            }
        }
    }

    private fun uriToFile(uri: Uri, context: Context): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = getFileName(uri, context)
        val tempFile = File(context.cacheDir, fileName)
        tempFile.outputStream().use { output ->
            inputStream?.copyTo(output)
        }
        return tempFile
    }

    private fun getFileName(uri: Uri, context: Context): String {
        var name = "temp_file"
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst()) name = it.getString(nameIndex)
        }
        return name
    }
}