package com.gns.billing.model

import com.google.gson.annotations.SerializedName

data class PembayaranSummaryResponse(
    val success: Boolean? = null,
    val message: String? = null,
    val data: SummaryData? = null
) {
    // Fungsi untuk membersihkan dan mengonversi format mata uang / angka dari server
    private fun cleanToDouble(valObj: Any?): Double {
        if (valObj == null) return 0.0
        if (valObj is Number) return valObj.toDouble()
        val valStr = valObj.toString()
        if (valStr.isBlank()) return 0.0
        val cleaned = valStr.replace("Rp", "", ignoreCase = true)
            .replace(".", "")
            .replace(" ", "")
            .replace(",", ".")
        return cleaned.toDoubleOrNull() ?: 0.0
    }

    fun getPendapatanHariIniDouble(): Double = cleanToDouble(data?.hari_ini)
    fun getPendapatanBulanDouble(): Double = cleanToDouble(data?.bulan_ini)
    fun getTotalTagihanDouble(): Double = cleanToDouble(data?.total)
    fun getPiutangDouble(): Double = cleanToDouble(data?.piutang)

    // Mengambil langsung jumlah pelanggan aktif murni dari server (75)
    fun getPelangganAktifCount(): Int {
        return data?.pelanggan_aktif ?: 0
    }
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

data class PembayaranHistoryResponse(
    val success: Boolean? = null,
    val data: List<PembayaranHistoryItem>? = emptyList(),
    val message: String? = null
)

data class PembayaranHistoryItem(
    val id: Int? = null,
    val invoice_no: String? = null,
    val nama_pelanggan: String? = null,
    val pelanggan_nama: String? = null,
    val tanggal: String? = null,
    val jumlah: Any? = null,
    val total: Any? = null,
    val nominal: Any? = null,
    val amount: Any? = null,
    val metode: String? = null,
    val status: String? = null
) {
    fun getNominalDouble(): Double {
        val valObj = total ?: jumlah ?: nominal ?: amount ?: 0.0
        if (valObj is Number) return valObj.toDouble()
        val valStr = valObj.toString()
        val cleaned = valStr.replace("Rp", "", ignoreCase = true)
            .replace(".", "")
            .replace(" ", "")
            .replace(",", ".")
        return cleaned.toDoubleOrNull() ?: 0.0
    }

    fun getNamaPelanggan(): String {
        return nama_pelanggan ?: pelanggan_nama ?: "Pelanggan Umum"
    }
}