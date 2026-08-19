package com.gns.billing.tagihan

import com.gns.billing.api.RetrofitClient

class TagihanRepository {
    suspend fun getTagihan(pelangganId: Int): TagihanResponse =
        RetrofitClient.api.getSemuaTagihan(pelangganId = pelangganId)

    suspend fun getTagihanDetail(id: Int): DetailTagihanResponse =
        RetrofitClient.api.getTagihanDetail(id)

    suspend fun getTagihanJatuhTempoList(): TagihanResponse =
        RetrofitClient.api.getSemuaTagihan(status = "Jatuh Tempo")

    suspend fun getSemuaTagihan(
        page: Int = 1,
        status: String? = null,
        search: String? = null
    ): TagihanResponse = RetrofitClient.api.getSemuaTagihan(page, status, search)
}
