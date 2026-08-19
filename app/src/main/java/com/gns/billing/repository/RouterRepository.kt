package com.gns.billing.repository

import com.gns.billing.api.RetrofitClient

class RouterRepository {
    suspend fun getRouter() = RetrofitClient.api.getRouter()
    suspend fun getProfiles(routerId: Int) = RetrofitClient.api.getProfiles(routerId)
    suspend fun testRouter(routerId: Int) = RetrofitClient.api.testRouter(routerId)

    suspend fun getPppSecret(routerId: Int) = RetrofitClient.api.getPppSecret(routerId)
    suspend fun createSecret(routerId: Int, username: String, password: String, service: String, profile: String) = RetrofitClient.api.createSecret(routerId, username, password, service, profile)
    suspend fun editSecret(routerId: Int, username: String) = RetrofitClient.api.editSecret(routerId, username)
    suspend fun updateSecret(routerId: Int, secret: String, username: String, password: String, service: String, profile: String, disabled: String) = RetrofitClient.api.updateSecret(routerId, secret, username, password, service, profile, disabled)
    suspend fun deleteSecret(routerId: Int, secret: String) = RetrofitClient.api.deleteSecret(routerId, secret)
    suspend fun enableSecret(routerId: Int, secret: String) = RetrofitClient.api.enableSecret(routerId, secret)
    suspend fun disableSecret(routerId: Int, secret: String) = RetrofitClient.api.disableSecret(routerId, secret)

    suspend fun getPppProfile(routerId: Int) = RetrofitClient.api.getPppProfile(routerId)
    suspend fun createProfile(routerId: Int, name: String, localAddress: String, remoteAddress: String, rateLimit: String, onlyOne: String) = RetrofitClient.api.createProfile(routerId, name, localAddress, remoteAddress, rateLimit, onlyOne)
    suspend fun deleteProfile(routerId: Int, profile: String) = RetrofitClient.api.deleteProfile(routerId, profile)
}
