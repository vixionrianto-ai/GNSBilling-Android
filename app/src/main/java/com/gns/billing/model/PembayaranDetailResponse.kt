package com.gns.billing.model

data class PembayaranDetailResponse(
    val status: Boolean = true,
    val message: String = "",
    val data: PembayaranDetailData? = null
)

data class PembayaranDetailData(
    val id: Int = 0,
    val invoice_no: String = "",
    val pelanggan_id: Int = 0,
    val pelanggan_nama: String = "",
    val nominal: Double = 0.0,
    val metode: String = "",
    val tanggal: String = "",
    val status: String = "",
    val keterangan: String = ""
)