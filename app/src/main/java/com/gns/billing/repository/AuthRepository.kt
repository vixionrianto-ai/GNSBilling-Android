package com.gns.billing.repository

import com.gns.billing.api.RetrofitClient
import com.gns.billing.model.LoginRequest
import com.gns.billing.model.MeResponse

class AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ) = RetrofitClient.api.login(
        LoginRequest(
            email = email,
            password = password
        )
    )
    suspend fun getProfile(): MeResponse {
        return RetrofitClient.api.me()
    }
}