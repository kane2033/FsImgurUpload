package com.focusstart.imgurupload.network.interceptors

import com.focusstart.imgurupload.CredentialsConstants
import okhttp3.Interceptor
import okhttp3.Response

// Добавление bearer токена и айди клиента к каждому запросу
class AuthInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request()
            .newBuilder()
            .header("Authorization", "Bearer ${CredentialsConstants.imgurApiToken}")
            .header("Authorization", "Client-ID ${CredentialsConstants.imgurClientId}")
            .build()

        return chain.proceed(newRequest)
    }
}