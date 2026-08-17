package com.gns.billing.model

data class Paket(

    val id: Int,

    val router_id: Int,

    val router: String?,

    val nama_paket: String,

    val kecepatan: String?,

    val profile_mikrotik: String?,

    val harga: Double,

    val status: String,

    val keterangan: String?

)