package com.gns.billing.model

sealed class LoginState {

    object Idle : LoginState()

    object Loading : LoginState()

    data class Success(
        val response: LoginResponse
    ) : LoginState()

    data class Error(
        val message: String
    ) : LoginState()

}