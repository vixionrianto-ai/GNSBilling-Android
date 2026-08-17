package com.gns.billing.model

data class TagihanDetailResponse(
    val success: Boolean,
    val message: String,
    val data: TagihanDetail
)

data class TagihanDetail(

    val id: Int,

    val pelanggan_id: Int,

    val pelanggan_nama: String,

    val invoice_no: String,

    val periode: String,

    val bulan: Int,

    val tahun: String,

    val tanggal_tagihan: String,

    val tanggal_jatuh_tempo: String,

    val tanggal_bayar: String?,

    val nominal: Double,

    val subtotal: Double,

    val tunggakan: Double,

    val denda: Double,

    val total: Double,

    val dibayar: Double,

    val sisa: Double,

    val status: String

)