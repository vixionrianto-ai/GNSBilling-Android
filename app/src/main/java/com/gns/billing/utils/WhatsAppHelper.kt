package com.gns.billing.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object WhatsAppHelper {

    /**
     * Kirim WhatsApp otomatis dengan rincian tagihan dinamis (1 bulan, 2 bulan, atau lebih)
     */
    fun sendTagihanWhatsApp(
        context: Context,
        phone: String?,
        namaPelanggan: String,
        listBulan: List<String>, // Contoh: ["Juli 2026", "Agustus 2026"]
        totalTagihan: Double
    ) {
        if (phone.isNullOrBlank()) {
            Toast.makeText(context, "Nomor HP pelanggan tidak tersedia", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // 1. Bersihkan format nomor HP (08... jadi 62...)
            var cleanNumber = phone.replace(Regex("[^0-9]"), "")
            if (cleanNumber.startsWith("0")) {
                cleanNumber = "62" + cleanNumber.substring(1)
            } else if (cleanNumber.startsWith("8")) {
                cleanNumber = "62$cleanNumber"
            }

            // 2. Susun rincian pesan otomatis mirip sistem Fonnte website
            val jumlahBulan = listBulan.size
            val gabunganBulan = listBulan.joinToString(", ")
            val formatRupiah = "%,d".format(totalTagihan.toLong()).replace(",", ".")

            val pesan = "*TAGIHAN INTERNET BULANAN*\n\n" +
                    "Halo Kak *{nama}*,\n" +
                    "Berikut adalah rincian tagihan layanan internet Anda untuk *{jumlah} bulan* ({periode}):\n\n" +
                    "💰 *Total Tagihan:* Rp {total}\n\n" +
                    "Silakan melakukan pembayaran melalui transfer bank/metode yang tersedia.\n" +
                    "Abaikan pesan ini jika sudah melakukan pembayaran. Terima kasih! 🙏"
                        .replace("{nama}", namaPelanggan)
                        .replace("{jumlah}", jumlahBulan.toString())
                        .replace("{periode}", gabunganBulan)
                        .replace("{total}", formatRupiah)

            // 3. Encode pesan dan buka WhatsApp
            val url = "https://api.whatsapp.com/send?phone=$cleanNumber&text=${Uri.encode(pesan)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)

        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Gagal membuka WhatsApp. Pastikan aplikasi terinstal.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}