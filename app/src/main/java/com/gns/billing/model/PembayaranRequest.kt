package com.gns.billing.model

data class PembayaranRequest(
    val tagihan_id: Int,
    val metode: String,
    val dibayar: Double,
    val biaya_admin: Double = 0.0,
    val keterangan: String? = null
)
