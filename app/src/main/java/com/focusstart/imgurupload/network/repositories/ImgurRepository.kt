package com.focusstart.imgurupload.network.repositories

import com.focusstart.imgurupload.dto.ImageResponse
import com.focusstart.imgurupload.network.apis.ImgurApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

// Обработка запроса на загрузку картинки в imgur
class ImgurRepository(private val api : ImgurApi) : BaseRepository() {

    suspend fun postImage(imageFile: File, title: String, description: String) : ImageResponse?{
        // Изображение в формате MultipartBody
        val imagePart = MultipartBody.Part.createFormData(
            "image",
            imageFile.name,
            imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        )

        // Остальные параметры
        val mediaType = "multipart/form-data".toMediaTypeOrNull()
        return safeApiCall(
        call = {
            api.postImageAsync(
            imagePart,
            title.toRequestBody(mediaType),
            description.toRequestBody(mediaType)
            )
        },
        errorMessage = "Error while trying to post an image"
        )
    }
}