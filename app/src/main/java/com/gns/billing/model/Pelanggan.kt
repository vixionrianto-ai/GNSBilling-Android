package com.gns.billing.model

data class Pelanggan(
    val id: Int,
    val kode_pelanggan: String,
    val nama: String,
    val alamat: String?,
    val no_hp: String?,
    val whatsapp_url: String? = null,
    val router_id: Int?,
    val router: Router?,
    val paket_id: Int?,
    val paket: Paket?,
    val username_pppoe: String?,
    val password_pppoe: String?,
    val ip_address: String?,
    val mac_address: String?,
    val status: String,
    val tanggal_pasang: String? = null,
    val tanggal_aktif: String? = null,
    val keterangan: String? = null,
    val isolation_use_default: Boolean? = true,
    val isolation_period_limit: Int? = null
)
