package com.focusstart.imgurupload.network.interceptors

import com.focusstart.imgurupload.CredentialsConstants
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/*
* Класс отвечает за обновление токена при получении ошибки 401
* (не реализован должным образом, оставлен на будущее как пример)
* */
class RefreshTokenAuthenticator: Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val updatedToken = getNewToken()
        return response.request.newBuilder() //return response.request().newBuilder()
            .header("Authorization", "Bearer $updatedToken")
            .build()
    }

    // Тут должна быть реализация обновления токена:
    // отправка рефреш токена для получения нового аксесс токена
    private fun getNewToken(): String {
        return CredentialsConstants.imgurRefreshToken
    }
}