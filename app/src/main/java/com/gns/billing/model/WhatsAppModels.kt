package com.gns.billing.model

data class WhatsAppHistoryResponse(
    val success: Boolean = false,
    val message: String? = null,
    val data: List<WhatsAppLog> = emptyList(),
    val pagination: Pagination? = null,
    val statistics: WhatsAppStatistics? = null
)

data class WhatsAppLogResponse(
    val success: Boolean = false,
    val message: String? = null,
    val data: WhatsAppLog? = null
)

data class WhatsAppLog(
    val id: Int = 0,
    val nomor: String? = null,
    val jenis: String? = null,
    val provider: String? = null,
    val status: String? = null,
    val message: String? = null,
    val sent_at: String? = null,
    val pelanggan: WhatsAppCustomer? = null,
    val tagihan: WhatsAppTagihan? = null
)

data class WhatsAppCustomer(
    val id: Int? = null,
    val nama: String? = null,
    val kode_pelanggan: String? = null
)

data class WhatsAppTagihan(
    val id: Int? = null,
    val invoice_no: String? = null,
    val periode: String? = null
)

data class WhatsAppStatistics(
    val total: Int = 0,
    val success: Int = 0,
    val failed: Int = 0,
    val pending: Int = 0,
    val today: Int = 0
)
