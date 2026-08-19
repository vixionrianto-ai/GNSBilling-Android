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

    // PPP Secret — field names match RouterController.php exactly.
    @GET("router/{id}/ppp-secret") suspend fun getPppSecret(@Path("id") id: Int): PppSecretResponse
    @FormUrlEncoded
    @POST("router/{id}/ppp-secret")
    suspend fun createSecret(@Path("id") id: Int, @Field("username") username: String, @Field("password") password: String, @Field("service") service: String, @Field("profile") profile: String): MessageResponse
    @GET("router/{id}/ppp-secret/{username}/edit") suspend fun editSecret(@Path("id") id: Int, @Path("username") username: String): PppSecretEditResponse
    @FormUrlEncoded
    @PUT("router/{id}/ppp-secret/{secret}")
    suspend fun updateSecret(@Path("id") id: Int, @Path("secret") secret: String, @Field("username") username: String, @Field("password") password: String, @Field("service") service: String, @Field("profile") profile: String, @Field("disabled") disabled: String): MessageResponse
    @DELETE("router/{id}/ppp-secret/{secret}") suspend fun deleteSecret(@Path("id") id: Int, @Path("secret") secret: String): MessageResponse
    @PUT("router/{id}/ppp-secret/{secret}/enable") suspend fun enableSecret(@Path("id") id: Int, @Path("secret") secret: String): MessageResponse
    @PUT("router/{id}/ppp-secret/{secret}/disable") suspend fun disableSecret(@Path("id") id: Int, @Path("secret") secret: String): MessageResponse

    // PPP Profile — request fields will follow the actual controller contract.
    @GET("router/{id}/ppp-profile") suspend fun getPppProfile(@Path("id") id: Int): PppProfileResponse
    @FormUrlEncoded
    @POST("router/{id}/ppp-profile") suspend fun createProfile(@Path("id") id: Int, @Field("name") name: String, @Field("local_address") localAddress: String, @Field("remote_address") remoteAddress: String, @Field("rate_limit") rateLimit: String, @Field("only_one") onlyOne: String): MessageResponse
    @DELETE("router/{id}/ppp-profile/{profile}") suspend fun deleteProfile(@Path("id") id: Int, @Path("profile") profile: String): MessageResponse
}
