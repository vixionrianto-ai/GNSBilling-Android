package com.gns.billing.api

import com.gns.billing.model.DashboardResponse
import com.gns.billing.model.LoginRequest
import com.gns.billing.model.LoginResponse
import com.gns.billing.model.MeResponse
import com.gns.billing.model.MessageResponse
import com.gns.billing.model.Paket
import com.gns.billing.model.PaketDetailResponse
import com.gns.billing.model.PaketRequest
import com.gns.billing.model.PaketResponse
import com.gns.billing.model.PelangganDetailResponse
import com.gns.billing.model.PelangganRequest
import com.gns.billing.model.PelangganResponse
import com.gns.billing.model.PembayaranDetailResponse
import com.gns.billing.model.PembayaranHistoryResponse
import com.gns.billing.model.PembayaranRequest
import com.gns.billing.model.PembayaranResponse
import com.gns.billing.model.PembayaranSummaryResponse
import com.gns.billing.model.ProfileResponse
import com.gns.billing.model.RouterResponse
import com.gns.billing.model.WhatsAppHistoryDetailResponse
import com.gns.billing.model.WhatsAppHistoryResponse
import com.gns.billing.tagihan.DetailTagihanResponse
import com.gns.billing.tagihan.TagihanResponse
import com.gns.billing.tagihan.TagihanWhatsappResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("me")
    suspend fun me(): MeResponse

    @GET("dashboard")
    suspend fun getDashboard(): DashboardResponse

    @GET("pelanggan")
    suspend fun getPelanggan(
        @Query("page") page: Int = 1,
        @Query("search") search: String = "",
        @Query("status") status: String = ""
    ): PelangganResponse

    @GET("pelanggan")
    suspend fun searchPelanggan(@Query("search") query: String): PelangganResponse

    @GET("pelanggan/{id}")
    suspend fun getDetailPelanggan(@Path("id") id: Int): PelangganDetailResponse

    @POST("pelanggan")
    suspend fun tambahPelanggan(@Body request: PelangganRequest): PelangganDetailResponse

    @PUT("pelanggan/{id}")
    suspend fun updatePelanggan(
        @Path("id") id: Int,
        @Body request: PelangganRequest
    ): PelangganDetailResponse

    @DELETE("pelanggan/{id}")
    suspend fun hapusPelanggan(@Path("id") id: Int): MessageResponse

    @GET("pelanggan/{id}/tagihan")
    suspend fun getTagihanPelanggan(@Path("id") id: Int): TagihanResponse

    @GET("tagihan/{id}")
    suspend fun getTagihanDetail(@Path("id") id: Int): DetailTagihanResponse

    @GET("tagihan")
    suspend fun getSemuaTagihan(@Query("status") status: String? = null): TagihanResponse

    @GET("tagihan/jatuh-tempo-all")
    suspend fun getTagihanJatuhTempoList(): TagihanResponse

    @GET("tagihan/{id}/whatsapp")
    suspend fun getTagihanWhatsapp(@Path("id") id: Int): TagihanWhatsappResponse

    @GET("pembayaran/{id}")
    suspend fun getDetailPembayaran(@Path("id") id: Int): PembayaranDetailResponse

    @GET("pembayaran/summary")
    suspend fun getPembayaranSummary(): PembayaranSummaryResponse

    @POST("pembayaran")
    suspend fun simpanPembayaran(@Body request: PembayaranRequest): PembayaranResponse

    @GET("pembayaran/history")
    suspend fun getPembayaranHistory(
        @Query("page") page: Int = 1,
        @Query("search") search: String? = null,
        @Query("status") status: String? = null,
        @Query("metode") metode: String? = null
    ): PembayaranHistoryResponse

    @GET("paket")
    suspend fun getPaket(): PaketResponse

    @GET("paket/{id}")
    suspend fun getDetailPaket(@Path("id") id: Int): PaketDetailResponse

    @POST("paket")
    suspend fun tambahPaket(@Body request: PaketRequest): Paket

    @PUT("paket/{id}")
    suspend fun updatePaket(
        @Path("id") id: Int,
        @Body request: PaketRequest
    ): Paket

    @DELETE("paket/{id}")
    suspend fun hapusPaket(@Path("id") id: Int): Unit

    @GET("router")
    suspend fun getRouter(): RouterResponse

    @GET("router/{id}/profiles")
    suspend fun getProfiles(@Path("id") id: Int): ProfileResponse

    @GET("router/{id}/secrets")
    suspend fun getSecrets(@Path("id") id: Int): MessageResponse

    @GET("router/{id}/active")
    suspend fun getActiveSessions(@Path("id") id: Int): MessageResponse

    @POST("router/{id}/test")
    suspend fun testRouter(@Path("id") id: Int): MessageResponse

    @GET("router/{id}/info")
    suspend fun getRouterInfo(@Path("id") id: Int): MessageResponse

    @FormUrlEncoded
    @POST("router/{id}/secret")
    suspend fun createSecret(
        @Path("id") routerId: Int,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("profile") profile: String,
        @Field("service") service: String = "pppoe"
    ): MessageResponse

    @FormUrlEncoded
    @PUT("router/{id}/secret/{secret}")
    suspend fun updateSecret(
        @Path("id") routerId: Int,
        @Path("secret") secretId: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("profile") profile: String,
        @Field("service") service: String = "pppoe"
    ): MessageResponse

    @DELETE("router/{id}/secret/{secret}")
    suspend fun deleteSecret(
        @Path("id") routerId: Int,
        @Path("secret") secretId: String
    ): MessageResponse

    @PUT("router/{id}/secret/{secret}/enable")
    suspend fun enableSecret(
        @Path("id") routerId: Int,
        @Path("secret") secretId: String
    ): MessageResponse

    @PUT("router/{id}/secret/{secret}/disable")
    suspend fun disableSecret(
        @Path("id") routerId: Int,
        @Path("secret") secretId: String
    ): MessageResponse

    @POST("router/{id}/secret/{secret}/disconnect")
    suspend fun disconnectSecret(
        @Path("id") routerId: Int,
        @Path("secret") secretId: String
    ): MessageResponse

    @POST("pelanggan/{id}/buka-isolir")
    suspend fun bukaIsolir(@Path("id") id: Int): MessageResponse

    @POST("pelanggan/{id}/isolir")
    suspend fun isolirPelanggan(@Path("id") id: Int): MessageResponse

    @POST("pelanggan/{id}/disconnect")
    suspend fun disconnectSession(@Path("id") id: Int): MessageResponse

    @GET("whatsapp")
    suspend fun getWhatsAppHistory(
        @Query("page") page: Int = 1,
        @Query("search") search: String? = null,
        @Query("status") status: String? = null,
        @Query("jenis") jenis: String? = null,
        @Query("provider") provider: String? = null,
        @Query("tanggal") tanggal: String? = null
    ): WhatsAppHistoryResponse

    @GET("whatsapp/{id}")
    suspend fun getWhatsAppHistoryDetail(@Path("id") id: Int): WhatsAppHistoryDetailResponse
}