package com.gns.billing.repository

import com.gns.billing.api.RetrofitClient
import com.gns.billing.model.PembayaranRequest
import com.gns.billing.model.PembayaranResponse

class PembayaranRepository {
    suspend fun getHistory(page: Int = 1, search: String? = null) =
        RetrofitClient.api.getPembayaranHistory(page, search)

    suspend fun getDetail(id: Int) =
        RetrofitClient.api.getDetailPembayaran(id)

    suspend fun getForm(tagihanId: Int) =
        RetrofitClient.api.getFormPembayaran(tagihanId)

    suspend fun simpanPembayaran(request: PembayaranRequest): PembayaranResponse =
        RetrofitClient.api.simpanPembayaran(request)
}
