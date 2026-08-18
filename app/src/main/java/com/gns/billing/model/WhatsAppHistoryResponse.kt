package com.gns.billing.model

import com.google.gson.annotations.SerializedName

data class WhatsAppHistoryResponse(
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: List<WhatsAppLogItem> = emptyList(),
    @SerializedName("pagination") val pagination: WhatsAppPagination = WhatsAppPagination(),
    @SerializedName("statistics") val statistics: WhatsAppStatistics = WhatsAppStatistics()
)

data class WhatsAppHistoryDetailResponse(
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: WhatsAppLogItem? = null
)

data class WhatsAppLogItem(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("jenis") val jenis: String? = null,
    @SerializedName("provider") val provider: String? = null,
    @SerializedName("nomor") val nomor: String? = null,
    @SerializedName("pesan") val pesan: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("response") val response: String? = null,
    @SerializedName("sent_at") val sentAt: String? = null,
    @SerializedName("pelanggan") val pelanggan: WhatsAppCustomerSummary? = null,
    @SerializedName("tagihan") val tagihan: WhatsAppInvoiceSummary? = null
)

data class WhatsAppCustomerSummary(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("nama") val nama: String? = null,
    @SerializedName("kode_pelanggan") val kodePelanggan: String? = null
)

data class WhatsAppInvoiceSummary(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("invoice_no") val invoiceNo: String? = null,
    @SerializedName("periode") val periode: String? = null
)

data class WhatsAppPagination(
    @SerializedName("current_page") val currentPage: Int = 1,
    @SerializedName("last_page") val lastPage: Int = 1,
    @SerializedName("per_page") val perPage: Int = 20,
    @SerializedName("total") val total: Int = 0,
    @SerializedName("from") val from: Int? = null,
    @SerializedName("to") val to: Int? = null
)

data class WhatsAppStatistics(
    @SerializedName("total") val total: Int = 0,
    @SerializedName("success") val success: Int = 0,
    @SerializedName("failed") val failed: Int = 0,
    @SerializedName("pending") val pending: Int = 0,
    @SerializedName("today") val today: Int = 0
)