package com.gns.billing.repository

import com.gns.billing.api.RetrofitClient
import com.gns.billing.model.Pelanggan
import com.gns.billing.model.PelangganRequest
import com.gns.billing.model.PelangganResponse

class PelangganRepository {

    suspend fun getPelanggan(
        page: Int,
        search: String,
        status: String
    ): PelangganResponse {
        return RetrofitClient.api.getPelanggan(
            page = page,
            search = search,
            status = status
        )
    }

    suspend fun getDetailPelanggan(
        id: Int
    ): Pelanggan {

        return RetrofitClient
            .api
            .getDetailPelanggan(id)
            .data

    }
    suspend fun tambahPelanggan(
        request: PelangganRequest
    ): Pelanggan {

        return RetrofitClient
            .api
            .tambahPelanggan(request)
            .data

    }

    suspend fun updatePelanggan(
        id: Int,
        request: PelangganRequest
    ): Pelanggan {

        return RetrofitClient
            .api
            .updatePelanggan(
                id,
                request
            )
            .data

    }

    suspend fun hapusPelanggan(
        id: Int
    ) {

        RetrofitClient
            .api
            .hapusPelanggan(id)

    }
}