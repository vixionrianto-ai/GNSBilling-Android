package com.gns.billing.repository

import com.gns.billing.api.RetrofitClient

class RouterRepository {
    suspend fun getRouter() = RetrofitClient.api.getRouter()
    suspend fun getProfiles(routerId: Int) = RetrofitClient.api.getProfiles(routerId)
    suspend fun testRouter(routerId: Int) = RetrofitClient.api.testRouter(routerId)
    suspend fun createSecret(routerId: Int, username: String, password: String, profile: String) =
        RetrofitClient.api.createSecret(routerId, username, password, profile)
}
