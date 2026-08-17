package com.gns.billing.model

import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    val success: Boolean,
    val message: String,
    val data: DashboardData?
)

data class DashboardData(
    val totalPelanggan: Int = 0,
    val pelangganAktif: Int = 0,
    val pelangganNonaktif: Int = 0,
    val totalRouter: Int = 0,
    val routerAktif: Int = 0,
    val routerOffline: Int = 0,
    val tagihanBelumLunas: Int = 0,
    val tagihanBelumBayar: Int = 0,
    val tagihanSebagian: Int = 0,
    val tagihanJatuhTempoCount: Int = 0,
    val tagihanLunas: Int = 0,
    val tagihanHariIni: Int = 0,
    val totalPiutang: String? = "0",
    val totalPembayaran: Int = 0,
    val pembayaranHariIni: Int = 0,
    val pendapatanHariIni: Int = 0,
    val pendapatanBulanIni: String? = "0",
    val totalPendapatan: String? = "0",
    val totalSaldoPelanggan: String? = "0"
)
