package com.gns.billing.api

import android.content.Context
import com.gns.billing.network.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://192.168.1.22/GNS_DEV/public/api/v1/"

    private lateinit var retrofit: Retrofit

    fun init(context: Context) {
        if (::retrofit.isInitialized) return

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context.applicationContext))
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService
        get() {
            check(::retrofit.isInitialized) {
                "RetrofitClient belum diinisialisasi. Panggil RetrofitClient.init(context) dari Application."
            }
            return retrofit.create(ApiService::class.java)
        }
}
