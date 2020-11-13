package com.focusstart.imgurupload.network.apis

import com.focusstart.imgurupload.dto.ImageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImgurApi {

    // Загрузка картинки,
    // возвращает результат в виде
    // data class ImageResponse
    @Multipart
    @POST("3/image")
    suspend fun postImageAsync(
        @Part image: MultipartBody.Part,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody
    ): Response<ImageResponse>
}