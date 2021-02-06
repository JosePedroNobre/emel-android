package com.emel.app.network.api

import androidx.annotation.NonNull
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Default constructor.
 */
class AuthInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(@NonNull chain: Interceptor.Chain): Response {
        var request = chain.request()
        val url = request.url().newBuilder()
            .build()
        request = request.newBuilder().url(url).build()
        return chain.proceed(request)
    }
}
