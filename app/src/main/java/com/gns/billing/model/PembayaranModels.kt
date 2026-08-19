package com.gns.billing.model

import com.google.gson.annotations.SerializedName

data class PembayaranHistoryResponse(
    val success: Boolean = false,
    val data: PembayaranPage? = null,
    val message: String? = null
)

data class PembayaranPage(
    val current_page: Int = 1,
    val data: List<PembayaranItem> = emptyList(),
    val last_page: Int = 1,
    val total: Int = 0
)

data class PembayaranItem(
    val id: Int,
    val invoice_no: String? = null,
    val metode: String? = null,
    val dibayar: Double = 0.0,
    val total_bayar: Double = 0.0,
    val status: String? = null,
    val tanggal_bayar: String? = null,
    val keterangan: String? = null,
    val tagihan: TagihanRingkas? = null,
    val user: UserRingkas? = null
)

data class PembayaranDetailResponse(
    val success: Boolean = false,
    val data: PembayaranItem? = null,
    val message: String? = null
)

data class TagihanRingkas(
    val id: Int? = null,
    val invoice: String? = null,
    val total: Double = 0.0,
    val dibayar: Double = 0.0,
    val sisa: Double = 0.0,
    val status: String? = null,
    val pelanggan: PelangganRingkas? = null
)

data class PelangganRingkas(
    val id: Int? = null,
    val nama: String? = null
)

data class UserRingkas(
    val id: Int? = null,
    val name: String? = null
)
