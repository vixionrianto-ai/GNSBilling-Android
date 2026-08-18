package com.gns.billing.core

import android.content.Context
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

private const val BASE_URL = "http://192.168.1.22/GNS_DEV/public/api/"

private val retrofit: Retrofit by lazy {
    val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
    val client = OkHttpClient.Builder().addInterceptor(logging).build()
    Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()
}

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(
    val success: Boolean = false,
    val message: String? = null,
    val data: LoginData? = null,
    val token: String? = null
)
data class LoginData(val token: String? = null, val user: UserData? = null)
data class UserData(val id: Int? = null, val name: String? = null, val email: String? = null)
data class DashboardResponse(val success: Boolean = false, val message: String? = null, val data: Map<String, Any>? = null)

interface GnsApi {
    @POST("login") suspend fun login(@Body request: LoginRequest): LoginResponse
    @GET("dashboard") suspend fun dashboard(): DashboardResponse
}

object Api {
    val service: GnsApi = retrofit.create(GnsApi::class.java)
}

class SessionStore(context: Context) {
    private val prefs = context.getSharedPreferences("gns_native_session", Context.MODE_PRIVATE)
    fun token(): String? = prefs.getString("token", null)
    fun name(): String = prefs.getString("name", "Operator") ?: "Operator"
    fun save(response: LoginResponse) {
        val token = response.data?.token ?: response.token ?: return
        prefs.edit()
            .putString("token", token)
            .putString("name", response.data?.user?.name ?: "Operator")
            .apply()
    }
    fun clear() = prefs.edit().clear().apply()
    fun loggedIn(): Boolean = !token().isNullOrBlank()
}
