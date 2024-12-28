package com.dicoding.picodiploma.fundamentalintermediate.data.retrofit

import android.content.Context
import android.content.SharedPreferences
import com.dicoding.picodiploma.fundamentalintermediate.data.AuthInterceptor
import com.fundamentalintermediate.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    private fun getInterceptor(token: String?): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        return if (token.isNullOrEmpty()) {
            OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
        } else {
            OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(token))
                .addInterceptor(loggingInterceptor)
                .build()
        }
    }

    fun getApiService(context: Context): ApiService {
        val sharedPref: SharedPreferences = context.getSharedPreferences("onSignIn", Context.MODE_PRIVATE)
        val token = sharedPref.getString("token", null).toString()
        val client = getInterceptor(token)

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ApiService::class.java)
    }
}
