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
import com.gns.billing.model.ProfileResponse
import com.gns.billing.model.RouterResponse
import com.gns.billing.tagihan.DetailTagihanResponse
import com.gns.billing.tagihan.TagihanResponse
import okhttp3.ResponseBody
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
    @POST("login") suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    @GET("me") suspend fun me(): MeResponse
    @POST("logout") suspend fun logout(): MessageResponse
    @GET("dashboard") suspend fun getDashboard(): DashboardResponse

    @GET("pelanggan") suspend fun getPelanggan(@Query("page") page: Int = 1, @Query("search") search: String = "", @Query("status") status: String = ""): PelangganResponse
    @GET("pelanggan/{id}") suspend fun getDetailPelanggan(@Path("id") id: Int): PelangganDetailResponse
    @POST("pelanggan") suspend fun tambahPelanggan(@Body request: PelangganRequest): PelangganDetailResponse
    @PUT("pelanggan/{id}") suspend fun updatePelanggan(@Path("id") id: Int, @Body request: PelangganRequest): PelangganDetailResponse
    @DELETE("pelanggan/{id}") suspend fun hapusPelanggan(@Path("id") id: Int): MessageResponse

    @GET("tagihan") suspend fun getSemuaTagihan(@Query("page") page: Int = 1, @Query("status") status: String? = null, @Query("search") search: String? = null): TagihanResponse
    @GET("tagihan/{id}") suspend fun getTagihanDetail(@Path("id") id: Int): DetailTagihanResponse
    @GET("tagihan/{id}/bayar") suspend fun getFormPembayaran(@Path("id") id: Int): DetailTagihanResponse

    @GET("pembayaran") suspend fun getPembayaranHistory(@Query("page") page: Int = 1, @Query("search") search: String? = null): PembayaranHistoryResponse
    @GET("pembayaran/{id}") suspend fun getDetailPembayaran(@Path("id") id: Int): PembayaranDetailResponse
    @POST("pembayaran") suspend fun simpanPembayaran(@Body request: PembayaranRequest): PembayaranResponse
    @GET("pembayaran/{id}/invoice") suspend fun getInvoice(@Path("id") id: Int): PembayaranDetailResponse
    @GET("pembayaran/{id}/pdf") suspend fun getInvoicePdf(@Path("id") id: Int): Response<ResponseBody>

    @GET("paket") suspend fun getPaket(): PaketResponse
    @GET("paket/{id}") suspend fun getDetailPaket(@Path("id") id: Int): PaketDetailResponse
    @POST("paket") suspend fun tambahPaket(@Body request: PaketRequest): Paket
    @PUT("paket/{id}") suspend fun updatePaket(@Path("id") id: Int, @Body request: PaketRequest): Paket
    @DELETE("paket/{id}") suspend fun hapusPaket(@Path("id") id: Int): MessageResponse

    @GET("router") suspend fun getRouter(): RouterResponse
    @GET("router/{id}/profiles") suspend fun getProfiles(@Path("id") id: Int): ProfileResponse
    @GET("router/{id}/test") suspend fun testRouter(@Path("id") id: Int): MessageResponse
    @GET("router/{id}/ppp-secret") suspend fun getPppSecret(@Path("id") id: Int): MessageResponse
    @FormUrlEncoded
    @POST("router/{id}/ppp-secret")
    suspend fun createSecret(
        @Path("id") id: Int,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("profile") profile: String
    ): MessageResponse
    @GET("router/{id}/ppp-profile") suspend fun getPppProfile(@Path("id") id: Int): MessageResponse
}
