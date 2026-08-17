package com.gns.billing.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Locale

// 1. Format Rupiah untuk tipe Double
fun formatRupiah(value: Double?): String {
    if (value == null || value == 0.0) return "Rp 0"
    val formatter = DecimalFormat("#,###", DecimalFormatSymbols(Locale("id", "ID")).apply {
        groupingSeparator = '.'
    })
    return "Rp ${formatter.format(value)}"
}

// 2. Format Rupiah untuk tipe String (Otomatis membersihkan desimal .00 dari API)
fun formatRupiah(valueStr: String?): String {
    if (valueStr.isNullOrBlank()) return "Rp 0"
    val cleanNumber = valueStr.toDoubleOrNull() ?: return "Rp 0"
    return formatRupiah(cleanNumber)
}

// 3. Format Rupiah untuk tipe Long / Int
fun formatRupiah(value: Long?): String {
    if (value == null || value == 0L) return "Rp 0"
    return formatRupiah(value.toDouble())
}

// Format Tanggal Indonesia
fun formatTanggalIndonesia(tanggal: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val date = inputFormat.parse(tanggal)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        tanggal
    }
}