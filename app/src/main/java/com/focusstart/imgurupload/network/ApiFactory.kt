package com.focusstart.imgurupload.network

import com.focusstart.imgurupload.network.apis.ImgurApi
import com.focusstart.imgurupload.network.interceptors.AuthInterceptor
import com.focusstart.imgurupload.network.interceptors.RefreshTokenAuthenticator
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/*
* Объект, служащий для создания экземпляра класса ретрофит
* с реализованным интерфейсом запросов
* */
object ApiFactory {
    // Логирование запросов
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Клиент okhttp с добавленными интерсепторами
    // для авторизации
    private val client = OkHttpClient().newBuilder()
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .connectTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(logging)
        .addInterceptor(AuthInterceptor())
        .authenticator(RefreshTokenAuthenticator())
        .build()

    // Создание клиента ретрофита
    private fun retrofit() : Retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl("https://api.imgur.com/")
        .addConverterFactory(MoshiConverterFactory.create()) // Парсинг JSON
        .build()


    // Ретрофит имплементирует интерфейс ImgurApi с запросами
    val imgurApi : ImgurApi = retrofit().create(ImgurApi::class.java)
}