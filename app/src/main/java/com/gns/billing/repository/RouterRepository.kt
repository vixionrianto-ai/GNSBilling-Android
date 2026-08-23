package com.gns.billing.repository

import com.gns.billing.api.RetrofitClient
import com.gns.billing.model.PppSecretRequest

class RouterRepository {
    suspend fun getRouter() = RetrofitClient.api.getRouter()
    suspend fun getProfiles(routerId: Int) = RetrofitClient.api.getProfiles(routerId)
    suspend fun getSecrets(routerId: Int) = RetrofitClient.api.getSecrets(routerId)
    suspend fun getActiveSessions(routerId: Int) = RetrofitClient.api.getActiveSessions(routerId)
    suspend fun getRouterInfo(routerId: Int) = RetrofitClient.api.getRouterInfo(routerId)
    suspend fun testRouter(routerId: Int) = RetrofitClient.api.testRouter(routerId)

    suspend fun createSecret(
        routerId: Int,
        username: String,
        password: String,
        profile: String,
        service: String = "pppoe"
    ) = RetrofitClient.api.createSecret(
        routerId,
        PppSecretRequest(username, password, profile, service)
    )

    suspend fun updateSecret(
        routerId: Int,
        secret: String,
        username: String,
        password: String,
        profile: String,
        service: String = "pppoe"
    ) = RetrofitClient.api.updateSecret(
        routerId,
        secret,
        PppSecretRequest(username, password, profile, service)
    )

    suspend fun deleteSecret(routerId: Int, secret: String) =
        RetrofitClient.api.deleteSecret(routerId, secret)

    suspend fun enableSecret(routerId: Int, secret: String) =
        RetrofitClient.api.enableSecret(routerId, secret)

    suspend fun disableSecret(routerId: Int, secret: String) =
        RetrofitClient.api.disableSecret(routerId, secret)

    suspend fun disconnectSecret(routerId: Int, secret: String) =
        RetrofitClient.api.disconnectSecret(routerId, secret)
}
