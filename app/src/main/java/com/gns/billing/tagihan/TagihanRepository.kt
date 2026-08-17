package com.gns.billing.tagihan

import com.gns.billing.api.RetrofitClient

class TagihanRepository {
    suspend fun getTagihan(pelangganId: Int): TagihanResponse {
        return RetrofitClient.api.getTagihanPelanggan(pelangganId)
    }

    suspend fun getTagihanDetail(id: Int): DetailTagihanResponse {
        return RetrofitClient.api.getTagihanDetail(id)
    }

    suspend fun getTagihanJatuhTempoList(): TagihanResponse {
        return RetrofitClient.api.getTagihanJatuhTempoList()
    }

    suspend fun getSemuaTagihan(): TagihanResponse {
        return RetrofitClient.api.getSemuaTagihan()
    }
}