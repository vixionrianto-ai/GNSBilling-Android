package com.gns.billing.repository

import com.gns.billing.api.RetrofitClient
import com.gns.billing.model.Paket
import com.gns.billing.model.PaketRequest
import com.gns.billing.model.PaketResponse

class PaketRepository {

    suspend fun getPaket(): PaketResponse {
        return RetrofitClient
            .api
            .getPaket()
    }

    suspend fun getDetailPaket(
        id: Int
    ): Paket {

        return RetrofitClient
            .api
            .getDetailPaket(id)
            .data

    }

    suspend fun tambahPaket(
        request: PaketRequest
    ): Paket {

        return RetrofitClient
            .api
            .tambahPaket(request)

    }

    suspend fun updatePaket(
        id: Int,
        request: PaketRequest
    ): Paket {

        return RetrofitClient
            .api
            .updatePaket(id, request)

    }

    suspend fun hapusPaket(
        id: Int
    ) {

        RetrofitClient
            .api
            .hapusPaket(id)

    }
}