package com.gns.billing.model

import com.google.gson.annotations.SerializedName

data class PembayaranSummaryResponse(
    val success: Boolean? = null,
    val message: String? = null,
    val data: SummaryData? = null
) {
    // Formatting helpers only; all business totals remain supplied by Laravel.
    private fun cleanToDouble(value: Any?): Double {
        if (value == null) return 0.0
        if (value is Number) return value.toDouble()
        val text = value.toString()
        if (text.isBlank()) return 0.0
        val cleaned = text.replace("Rp", "", ignoreCase = true)
            .replace(".", "")
            .replace(" ", "")
            .replace(",", ".")
        return cleaned.toDoubleOrNull() ?: 0.0
    }

    fun getPendapatanHariIniDouble(): Double = cleanToDouble(data?.hari_ini)
    fun getPendapatanBulanDouble(): Double = cleanToDouble(data?.bulan_ini)
    fun getTotalTagihanDouble(): Double = cleanToDouble(data?.total)
    fun getPiutangDouble(): Double = cleanToDouble(data?.piutang)

    fun getPelangganAktifCount(): Int = data?.pelanggan_aktif ?: 0
}

data class SummaryData(
    @SerializedName(value = "bulan_ini", alternate = ["pendapatan_bulan", "pendapatanBulan", "bulan"])
    val bulan_ini: Any? = null,
    @SerializedName(value = "hari_ini", alternate = ["pendapatan_hari_ini", "pendapatanHariIni", "today"])
    val hari_ini: Any? = null,
    @SerializedName(value = "total", alternate = ["total_tagihan", "totalTagihan"])
    val total: Any? = null,
    @SerializedName(value = "piutang", alternate = ["sisa", "total_piutang"])
    val piutang: Any? = null,
    @SerializedName(value = "pelanggan_aktif", alternate = ["pelangganAktif", "aktif"])
    val pelanggan_aktif: Int? = null,
    @SerializedName(value = "lunas", alternate = ["totalLunas", "tagihan_lunas"])
    val lunas: Int? = null,
    @SerializedName(value = "jatuh_tempo", alternate = ["totalJatuhTempo", "jatuhTempo"])
    val jatuh_tempo: Int? = null
)
