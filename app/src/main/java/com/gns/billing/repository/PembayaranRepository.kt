package com.gns.billing.repository

import com.gns.billing.api.RetrofitClient
import com.gns.billing.model.PembayaranRequest
import com.gns.billing.model.PembayaranResponse

class PembayaranRepository {

    suspend fun simpanPembayaran(
        request: PembayaranRequest
    ): PembayaranResponse {

        return RetrofitClient.api.simpanPembayaran(request)

    }

}