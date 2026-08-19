package com.gns.billing.repository

import com.gns.billing.api.RetrofitClient
import com.gns.billing.model.Pelanggan
import com.gns.billing.model.PelangganRequest
import com.gns.billing.model.PelangganResponse
import com.gns.billing.model.MessageResponse

class PelangganRepository {
    suspend fun getPelanggan(page: Int, search: String, status: String): PelangganResponse =
        RetrofitClient.api.getPelanggan(page = page, search = search, status = status)

    suspend fun getDetailPelanggan(id: Int): Pelanggan =
        RetrofitClient.api.getDetailPelanggan(id).data

    suspend fun tambahPelanggan(request: PelangganRequest): MessageResponse =
        RetrofitClient.api.tambahPelanggan(request)

    suspend fun updatePelanggan(id: Int, request: PelangganRequest): MessageResponse =
        RetrofitClient.api.updatePelanggan(id, request)

    suspend fun hapusPelanggan(id: Int): MessageResponse =
        RetrofitClient.api.hapusPelanggan(id)

    suspend fun syncPelanggan(): MessageResponse =
        RetrofitClient.api.syncPelanggan()
}
