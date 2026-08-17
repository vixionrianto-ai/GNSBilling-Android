package com.gns.billing.repository

import com.gns.billing.api.RetrofitClient
import com.gns.billing.model.DashboardResponse

class DashboardRepository {

    suspend fun getDashboard(): DashboardResponse {

        return RetrofitClient.api.getDashboard()

    }

}