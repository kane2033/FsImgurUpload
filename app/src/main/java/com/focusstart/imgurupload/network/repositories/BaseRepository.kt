package com.focusstart.imgurupload.network.repositories

import android.util.Log
import com.focusstart.imgurupload.network.Result
import retrofit2.Response
import java.io.IOException

/*
* Базовый репозиторий служит для обработки
* запросов
* */
open class BaseRepository{

    // Выполнение запроса (парам. call) и возврат тела,
    // иначе отображение ошибки (парам. errorMessage)
    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>, errorMessage: String): T? {

        val result : Result<T> = safeApiResult(call, errorMessage)
        var data : T? = null

        when(result) {
            is Result.Success ->
                data = result.data
            is Result.Error -> {
                Log.d("ApiRepository", "$errorMessage " +
                        "& Exception - ${result.exception}")
            }
        }
        return data
    }

    // Получение результата запроса (парам. call)
    private suspend fun <T: Any> safeApiResult(call: suspend ()-> Response<T>,
                                               errorMessage: String) : Result<T> {
        val response = call.invoke()
        if(response.isSuccessful) return Result.Success(response.body()!!)

        return Result.Error(
            IOException(
                "Error while trying " +
                        "to get a safe Api result: $errorMessage"
            )
        )
    }
}