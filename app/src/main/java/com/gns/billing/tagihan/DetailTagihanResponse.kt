package com.gns.billing.tagihan

data class DetailTagihanResponse(
    val success: Boolean,
    val message: String,
    val data: DetailTagihan
)

data class DetailTagihan(
    val id: Int,
    val pelanggan_id: Int,
    val pelanggan_nama: String,
    val invoice_no: String,
    val periode: String,
    val bulan: Int,
    val tahun: String,
    val tanggal_tagihan: String?,
    val tanggal_jatuh_tempo: String?,
    val tanggal_bayar: String?,
    val total: Double,
    val dibayar: Double,
    val sisa: Double,
    val status: String,
    val keterangan: String?
)