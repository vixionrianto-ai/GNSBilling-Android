package com.gns.billing.model

data class PembayaranResponse(
    val success: Boolean = false,
    val message: String = "",
    val data: PembayaranResult? = null
)

data class PembayaranResult(
    val id: Int = 0,
    val invoice_no: String? = null,
    val nominal: Double = 0.0,
    val biaya_admin: Double = 0.0,
    val total_bayar: Double = 0.0,
    val dibayar: Double = 0.0,
    val kembalian: Double = 0.0,
    val status: String = "",
    val is_lunas: Boolean = false,
    val pelanggan_nama: String? = null
)
