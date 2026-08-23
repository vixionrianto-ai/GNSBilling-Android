package com.gns.billing.api

import com.gns.billing.model.*
import com.gns.billing.tagihan.DetailTagihanResponse
import com.gns.billing.tagihan.TagihanResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("login") suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    @GET("me") suspend fun me(): MeResponse
    @POST("logout") suspend fun logout(): MessageResponse
    @GET("dashboard") suspend fun getDashboard(): DashboardResponse

    @GET("pelanggan") suspend fun getPelanggan(@Query("page") page: Int = 1, @Query("search") search: String = "", @Query("status") status: String = ""): PelangganResponse
    @GET("pelanggan/{id}") suspend fun getDetailPelanggan(@Path("id") id: Int): PelangganDetailResponse
    @GET("pelanggan/{id}/tagihan") suspend fun getTagihanPelanggan(@Path("id") id: Int): TagihanResponse
    @GET("pelanggan/{id}/pembayaran") suspend fun getPembayaranPelanggan(@Path("id") id: Int): PembayaranHistoryResponse
    @POST("pelanggan") suspend fun tambahPelanggan(@Body request: PelangganRequest): MessageResponse
    @PUT("pelanggan/{id}") suspend fun updatePelanggan(@Path("id") id: Int, @Body request: PelangganRequest): MessageResponse
    @DELETE("pelanggan/{id}") suspend fun hapusPelanggan(@Path("id") id: Int): MessageResponse

    @POST("pelanggan/{id}/buka-isolir") suspend fun bukaIsolir(@Path("id") id: Int): MessageResponse
    @POST("pelanggan/{id}/isolir") suspend fun isolirPelanggan(@Path("id") id: Int): MessageResponse
    @POST("pelanggan/{id}/disconnect") suspend fun disconnectSession(@Path("id") id: Int): MessageResponse

    @GET("tagihan") suspend fun getSemuaTagihan(@Query("page") page: Int = 1, @Query("status") status: String? = null, @Query("search") search: String? = null, @Query("pelanggan_id") pelangganId: Int? = null): TagihanResponse
    @GET("tagihan/{id}") suspend fun getTagihanDetail(@Path("id") id: Int): DetailTagihanResponse
    @GET("tagihan/{id}/bayar") suspend fun getFormPembayaran(@Path("id") id: Int): DetailTagihanResponse
    @POST("tagihan/generate-semua") suspend fun generateSemuaTagihan(): MessageResponse
    @POST("tagihan/generate-periode") suspend fun generatePeriodeTagihan(): MessageResponse
    @POST("tagihan/{tagihan}/regenerate") suspend fun regenerateTagihan(@Path("tagihan") id: Int): MessageResponse
    @POST("tagihan/maintenance") suspend fun maintenanceTagihan(): MessageResponse
    @GET("tagihan/{tagihan}/whatsapp") suspend fun getTagihanWhatsApp(@Path("tagihan") id: Int): MessageResponse

    @GET("pembayaran") suspend fun getPembayaranHistory(@Query("page") page: Int = 1, @Query("search") search: String? = null): PembayaranHistoryResponse
    @GET("pembayaran/{id}") suspend fun getDetailPembayaran(@Path("id") id: Int): PembayaranDetailResponse
    @POST("pembayaran") suspend fun simpanPembayaran(@Body request: PembayaranRequest): PembayaranResponse

    @GET("paket") suspend fun getPaket(): PaketResponse
    @GET("paket/{id}") suspend fun getDetailPaket(@Path("id") id: Int): PaketDetailResponse
    @POST("paket") suspend fun tambahPaket(@Body request: PaketRequest): Paket
    @PUT("paket/{id}") suspend fun updatePaket(@Path("id") id: Int, @Body request: PaketRequest): Paket
    @DELETE("paket/{id}") suspend fun hapusPaket(@Path("id") id: Int): MessageResponse

    @GET("router") suspend fun getRouter(): RouterResponse
    @GET("router/{id}/profiles") suspend fun getProfiles(@Path("id") id: Int): ProfileResponse
    @GET("router/{id}/secrets") suspend fun getSecrets(@Path("id") id: Int): PppSecretResponse
    @GET("router/{id}/active") suspend fun getActiveSessions(@Path("id") id: Int): MessageResponse
    @GET("router/{id}/info") suspend fun getRouterInfo(@Path("id") id: Int): MessageResponse
    @POST("router/{id}/test") suspend fun testRouter(@Path("id") id: Int): MessageResponse
    @POST("router/{id}/secret") suspend fun createSecret(@Path("id") id: Int, @Body request: PppSecretRequest): MessageResponse
    @PUT("router/{id}/secret/{secret}") suspend fun updateSecret(@Path("id") id: Int, @Path("secret") secret: String, @Body request: PppSecretRequest): MessageResponse
    @DELETE("router/{id}/secret/{secret}") suspend fun deleteSecret(@Path("id") id: Int, @Path("secret") secret: String): MessageResponse
    @PUT("router/{id}/secret/{secret}/enable") suspend fun enableSecret(@Path("id") id: Int, @Path("secret") secret: String): MessageResponse
    @PUT("router/{id}/secret/{secret}/disable") suspend fun disableSecret(@Path("id") id: Int, @Path("secret") secret: String): MessageResponse
    @POST("router/{id}/secret/{secret}/disconnect") suspend fun disconnectSecret(@Path("id") id: Int, @Path("secret") secret: String): MessageResponse

    @GET("whatsapp") suspend fun getWhatsAppHistory(@Query("page") page: Int = 1): WhatsAppHistoryResponse
    @GET("whatsapp/{id}") suspend fun getWhatsAppDetail(@Path("id") id: Int): WhatsAppLogResponse

    @GET("operator/home") suspend fun getOperatorHome(): MessageResponse
    @GET("operator/profile") suspend fun getOperatorProfile(): MessageResponse
    @PUT("operator/profile") suspend fun updateOperatorProfile(@Body request: OperatorProfileRequest): MessageResponse
    @PUT("operator/password") suspend fun changeOperatorPassword(@Body request: OperatorPasswordRequest): MessageResponse
    @GET("search") suspend fun search(@Query("q") query: String): MessageResponse
}
