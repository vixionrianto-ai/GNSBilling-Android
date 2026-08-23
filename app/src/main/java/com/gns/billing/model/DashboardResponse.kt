package com.gns.billing.model

import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    val success: Boolean,
    val message: String,
    val data: DashboardData?
)

data class DashboardData(
    @SerializedName("totalPelanggan") val totalPelanggan: Int = 0,
    @SerializedName("pelangganAktif") val pelangganAktif: Int = 0,
    @SerializedName("pelangganNonaktif") val pelangganNonaktif: Int = 0,
    @SerializedName("totalRouter") val totalRouter: Int = 0,
    @SerializedName("routerAktif") val routerAktif: Int = 0,
    @SerializedName("routerOffline") val routerOffline: Int = 0,
    @SerializedName("tagihanBelumLunas") val tagihanBelumLunas: Int = 0,
    @SerializedName("tagihanBelumBayar") val tagihanBelumBayar: Int = 0,
    @SerializedName("tagihanSebagian") val tagihanSebagian: Int = 0,
    @SerializedName("tagihanJatuhTempoCount") val tagihanJatuhTempoCount: Int = 0,
    @SerializedName("tagihanLunas") val tagihanLunas: Int = 0,
    @SerializedName("tagihanHariIni") val tagihanHariIni: Int = 0,
    @SerializedName("totalPiutang") val totalPiutang: String? = "0",
    @SerializedName("totalPembayaran") val totalPembayaran: Int = 0,
    @SerializedName("pembayaranHariIni") val pembayaranHariIni: Int = 0,
    @SerializedName("pendapatanHariIni") val pendapatanHariIni: Int = 0,
    @SerializedName("pendapatanBulanIni") val pendapatanBulanIni: String? = "0",
    @SerializedName("totalPendapatan") val totalPendapatan: String? = "0",
    @SerializedName("totalSaldoPelanggan") val totalSaldoPelanggan: String? = "0"
)
