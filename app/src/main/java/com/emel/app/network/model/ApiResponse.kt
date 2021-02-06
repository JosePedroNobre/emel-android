package com.emel.app.network.model

import retrofit2.Response

@Suppress("unused")
open class ApiResponse<T> {

    companion object {
        fun <T> create(url: String, throwable: Throwable): com.emel.app.network.model.ApiErrorResponse<T> {
            return com.emel.app.network.model.ApiErrorResponse(
                url,
                520,
                throwable.message ?: "Something went wrong"
            )
        }

        fun <T> create(url: String, response: Response<T>): com.emel.app.network.model.ApiResponse<T> {
            return if (response.isSuccessful) {
                val body = response.body()
                com.emel.app.network.model.ApiSuccessResponse(url, body)
            } else {
                val code = response.code()
                val message = response.errorBody()?.string()
                val errorMessage = if (message.isNullOrEmpty()) {
                    response.message()
                } else {
                    message
                }
                com.emel.app.network.model.ApiErrorResponse(
                    url,
                    code,
                    errorMessage ?: "Something went wrong"
                )
            }
        }
    }
}

data class ApiSuccessResponse<T>(val url: String, val body: T?) : com.emel.app.network.model.ApiResponse<T>()

data class ApiErrorResponse<T>(val url: String, val errorCode: Int, val errorMessage: String) : com.emel.app.network.model.ApiResponse<T>()
