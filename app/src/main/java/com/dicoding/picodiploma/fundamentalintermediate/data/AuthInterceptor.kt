package com.dicoding.picodiploma.fundamentalintermediate.data

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor(private var token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        if (originalRequest.header("Authorization") != null) {
            return chain.proceed(originalRequest)
        }

        val finalToken = "Bearer $token"
        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", finalToken)
            .build()

        return chain.proceed(newRequest)
    }
}
