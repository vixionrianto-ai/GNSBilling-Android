package com.gns.billing.repository

import com.gns.billing.api.RetrofitClient
import com.gns.billing.model.WhatsAppHistoryDetailResponse
import com.gns.billing.model.WhatsAppHistoryResponse

class WhatsAppRepository {
    suspend fun getHistory(
        page: Int = 1,
        search: String? = null,
        status: String? = null,
        jenis: String? = null,
        provider: String? = null,
        tanggal: String? = null
    ): WhatsAppHistoryResponse = RetrofitClient.api.getWhatsAppHistory(
        page = page,
        search = search,
        status = status,
        jenis = jenis,
        provider = provider,
        tanggal = tanggal
    )

    suspend fun getDetail(id: Int): WhatsAppHistoryDetailResponse =
        RetrofitClient.api.getWhatsAppHistoryDetail(id)
}