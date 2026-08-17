package com.gns.billing.model

data class PelangganRequest(
    val nama: String,
    val no_hp: String,
    val alamat: String,
    val paket_id: Int,
    val router_id: Int,
    val status: String,
    val username_pppoe: String,
    val password_pppoe: String,
    val kode_pelanggan: String? = null,
    val ip_address: String? = null,
    val mac_address: String? = null,
    val tanggal_pasang: String? = null,
    val tanggal_aktif: String? = null,
    val keterangan: String? = null,
    val isolation_use_default: Boolean = true,
    val isolation_period_limit: Int? = null
)
