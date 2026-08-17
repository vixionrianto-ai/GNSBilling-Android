package com.gns.billing.tagihan

import com.google.gson.annotations.SerializedName

data class TagihanResponse(
    @SerializedName(value = "success", alternate = ["status"])
    val success: Boolean = false,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("data")
    val data: List<Tagihan>? = emptyList()
)

data class Tagihan(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("invoice_no")
    val invoice_no: String = "",
    @SerializedName("periode")
    val periode: String = "",
    @SerializedName("bulan")
    val bulan: Int = 0,
    @SerializedName("tahun")
    val tahun: String = "",
    @SerializedName("tanggal_tagihan")
    val tanggal_tagihan: String? = null,
    @SerializedName("tanggal_jatuh_tempo")
    val tanggal_jatuh_tempo: String? = null,
    @SerializedName("tanggal_bayar")
    val tanggal_bayar: String? = null,
    @SerializedName("total")
    val total: Double = 0.0,
    @SerializedName("dibayar")
    val dibayar: Double = 0.0,
    @SerializedName("sisa")
    val sisa: Double = 0.0,
    @SerializedName("status")
    val status: String = "",
    @SerializedName("keterangan")
    val keterangan: String? = null,
    @SerializedName(value = "pelanggan_nama", alternate = ["nama_pelanggan", "nama"])
    val pelanggan_nama: String? = null,
    @SerializedName(value = "pelanggan_no_hp", alternate = ["no_hp", "telepon"])
    val pelanggan_no_hp: String? = null,
    @SerializedName("pelanggan_id")
    val pelanggan_id: Int? = null
)