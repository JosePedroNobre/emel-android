package com.emel.app.network.api.adapter

import android.util.Log
import com.emel.app.App
import com.emel.app.network.api.ApiService
import com.emel.app.network.api.requests.TokenRequest
import com.emel.app.utils.getRefreshToken
import com.emel.app.utils.getToken
import com.emel.app.utils.setRefreshToken
import com.emel.app.utils.setToken
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import javax.inject.Singleton

@Singleton
class TokenAutheticator :
    Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code() == 401) {
            val refreshTokenRequest =
                TokenRequest(App.instance.getRefreshToken().toString())

            callAPI(NetworkManager.TOKEN.refreshToken(
                App.instance.getToken().toString(), refreshTokenRequest
            ),
                onSucceed = {
                    App.instance.setToken("Bearer ${it?.token}")
                    App.instance.setRefreshToken("Bearer ${it?.refreshToken}")
                },
                onError = {
                    Log.e("Error", "")
                },
                onFailureOccurred = {
                    Log.e("Failure", "")
                })

            // Add new header to rejected request and retry it
            return response.request().newBuilder()
                .header("Authorization", App.instance.getToken().toString())
                .build()
        } else {
            return null
        }
    }
}

fun <T> callAPI(
    call: Call<T>?,
    onSucceed: (T?) -> Unit,
    onError: (errorCode: String) -> Unit,
    onFailureOccurred: () -> Unit
) {
    NetworkManager.instance.performCall(call, object : NetworkManager.OnRequestCallback<T> {
        override fun onSuccess(body: T?) {
            onSucceed(body)
        }

        override fun onError(errorResponseBody: ResponseBody?) {
            onError
        }

        override fun onFailure() {
            onFailureOccurred()
        }
    })
}

class NetworkManager private constructor() {

    companion object {
        val instance: NetworkManager by lazy { NetworkManager() }
        val TOKEN by lazy { ServiceGenerator.createService(ApiService::class.java) }
    }

    fun <T> performCall(call: Call<T>?, callback: OnRequestCallback<T>) {
        call?.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: retrofit2.Response<T>) {
                when (response.code()) {
                    200, 201, 204 -> {
                        callback.onSuccess(response.body())
                    }
                    else -> {
                        callback.onError(response.errorBody())
                    }
                }
                return
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onFailure()
                Log.d("Network Manager Failure", t.localizedMessage)
            }
        })
    }

    /**********************************************************************************************
     *                                      CUSTOM CALLBACK                                       *
     **********************************************************************************************/
    interface OnRequestCallback<in T> {
        fun onSuccess(body: T?)
        fun onError(errorResponseBody: ResponseBody?)
        fun onFailure()
    }
}
