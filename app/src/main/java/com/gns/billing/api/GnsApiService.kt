package com.gns.billing.api

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Native Android API contract for GNS Billing.
 *
 * This interface intentionally contains no billing/business rules. Laravel is
 * the source of truth for validation, permissions, transactions, MikroTik,
 * tagihan and payment allocation.
 */
interface GnsApiService {
    @POST("login")
    suspend fun login(@Body body: JsonObject): Response<JsonObject>

    @GET("me")
    suspend fun me(): Response<JsonObject>

    @POST("logout")
    suspend fun logout(): Response<JsonObject>

    @GET("dashboard")
    suspend fun dashboard(): Response<JsonObject>

    @GET("pelanggan")
    suspend fun pelanggan(@Query("page") page: Int = 1, @Query("search") search: String? = null, @Query("status") status: String? = null): Response<JsonObject>

    @GET("pelanggan/{id}")
    suspend fun pelangganDetail(@Path("id") id: Int): Response<JsonObject>

    @POST("pelanggan")
    suspend fun pelangganCreate(@Body body: JsonObject): Response<JsonObject>

    @PUT("pelanggan/{id}")
    suspend fun pelangganUpdate(@Path("id") id: Int, @Body body: JsonObject): Response<JsonObject>

    @DELETE("pelanggan/{id}")
    suspend fun pelangganDelete(@Path("id") id: Int): Response<JsonObject>

    @POST("pelanggan/sync")
    suspend fun pelangganSync(): Response<JsonObject>

    @GET("router")
    suspend fun routers(): Response<JsonObject>

    @GET("router/{id}")
    suspend fun routerDetail(@Path("id") id: Int): Response<JsonObject>

    @POST("router")
    suspend fun routerCreate(@Body body: JsonObject): Response<JsonObject>

    @PUT("router/{id}")
    suspend fun routerUpdate(@Path("id") id: Int, @Body body: JsonObject): Response<JsonObject>

    @DELETE("router/{id}")
    suspend fun routerDelete(@Path("id") id: Int): Response<JsonObject>

    @GET("router/{id}/test")
    suspend fun routerTest(@Path("id") id: Int): Response<JsonObject>

    @GET("router/{id}/ppp-secret")
    suspend fun pppSecrets(@Path("id") id: Int): Response<JsonObject>

    @POST("router/{id}/ppp-secret")
    suspend fun pppSecretCreate(@Path("id") id: Int, @Body body: JsonObject): Response<JsonObject>

    @PUT("router/{id}/ppp-secret/{secret}")
    suspend fun pppSecretUpdate(@Path("id") id: Int, @Path("secret") secret: String, @Body body: JsonObject): Response<JsonObject>

    @DELETE("router/{id}/ppp-secret/{secret}")
    suspend fun pppSecretDelete(@Path("id") id: Int, @Path("secret") secret: String): Response<JsonObject>

    @PUT("router/{id}/ppp-secret/{secret}/enable")
    suspend fun pppSecretEnable(@Path("id") id: Int, @Path("secret") secret: String): Response<JsonObject>

    @PUT("router/{id}/ppp-secret/{secret}/disable")
    suspend fun pppSecretDisable(@Path("id") id: Int, @Path("secret") secret: String): Response<JsonObject>

    @GET("router/{id}/ppp-profile")
    suspend fun pppProfiles(@Path("id") id: Int): Response<JsonObject>

    @POST("router/{id}/ppp-profile")
    suspend fun pppProfileCreate(@Path("id") id: Int, @Body body: JsonObject): Response<JsonObject>

    @PUT("router/{id}/ppp-profile/{profile}")
    suspend fun pppProfileUpdate(@Path("id") id: Int, @Path("profile") profile: String, @Body body: JsonObject): Response<JsonObject>

    @DELETE("router/{id}/ppp-profile/{profile}")
    suspend fun pppProfileDelete(@Path("id") id: Int, @Path("profile") profile: String): Response<JsonObject>

    @GET("paket")
    suspend fun paket(): Response<JsonObject>

    @GET("paket/{id}")
    suspend fun paketDetail(@Path("id") id: Int): Response<JsonObject>

    @POST("paket")
    suspend fun paketCreate(@Body body: JsonObject): Response<JsonObject>

    @PUT("paket/{id}")
    suspend fun paketUpdate(@Path("id") id: Int, @Body body: JsonObject): Response<JsonObject>

    @DELETE("paket/{id}")
    suspend fun paketDelete(@Path("id") id: Int): Response<JsonObject>

    @GET("tagihan")
    suspend fun tagihan(@Query("status") status: String? = null, @Query("search") search: String? = null, @Query("page") page: Int = 1): Response<JsonObject>

    @GET("tagihan/{id}")
    suspend fun tagihanDetail(@Path("id") id: Int): Response<JsonObject>

    @DELETE("tagihan/{id}")
    suspend fun tagihanDelete(@Path("id") id: Int): Response<JsonObject>

    @POST("tagihan/generate-harian")
    suspend fun tagihanGenerate(): Response<JsonObject>

    @GET("tagihan/{id}/bayar")
    suspend fun pembayaranForm(@Path("id") id: Int): Response<JsonObject>

    @GET("pembayaran")
    suspend fun pembayaran(@Query("search") search: String? = null, @Query("page") page: Int = 1): Response<JsonObject>

    @GET("pembayaran/{id}")
    suspend fun pembayaranDetail(@Path("id") id: Int): Response<JsonObject>

    @POST("pembayaran")
    suspend fun pembayaranCreate(@Body body: JsonObject): Response<JsonObject>

    @GET("pembayaran/{id}/invoice")
    suspend fun invoice(@Path("id") id: Int): Response<JsonObject>
}
