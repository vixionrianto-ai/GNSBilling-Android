package com.gns.billing.model

data class OperatorProfileRequest(
    val name: String,
    val email: String
)

data class OperatorPasswordRequest(
    val current_password: String,
    val password: String,
    val password_confirmation: String
)
