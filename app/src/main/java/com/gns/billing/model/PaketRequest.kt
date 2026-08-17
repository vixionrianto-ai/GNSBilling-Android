package com.gns.billing.model

data class PaketRequest(
    val router_id: Int,
    val nama_paket: String,
    val profile_mikrotik: String,
    val kecepatan: String? = null,
    val harga: Double,
    val status: String,
    val keterangan: String? = null
)
