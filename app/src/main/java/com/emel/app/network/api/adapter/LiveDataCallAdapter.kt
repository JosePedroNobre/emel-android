package com.emel.app.network.api.adapter

import androidx.lifecycle.LiveData
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A Retrofit adapterthat converts the Call into a LiveData of ApiResponse.
 * @param <R>
</R> */
class LiveDataCallAdapter<R>(private val responseType: Type) : CallAdapter<R, LiveData<com.emel.app.network.model.ApiResponse<R>>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<R>): LiveData<com.emel.app.network.model.ApiResponse<R>> {
        return object : LiveData<com.emel.app.network.model.ApiResponse<R>>() {
            var started = AtomicBoolean(false)
            override fun onActive() {
                super.onActive()
                if (started.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<R> {
                        override fun onResponse(call: Call<R>, response: Response<R>) {
                            postValue(com.emel.app.network.model.ApiResponse.create(call.request()?.url().toString(), response))
                        }

                        override fun onFailure(call: Call<R>, throwable: Throwable) {
                            postValue(com.emel.app.network.model.ApiResponse.create(call.request()?.url().toString(), throwable))
                        }
                    })
                }
            }
        }
    }
}