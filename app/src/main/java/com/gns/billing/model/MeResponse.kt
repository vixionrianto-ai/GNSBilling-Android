package com.gns.billing.model

data class MeResponse(
    val success: Boolean,
    val message: String,
    val data: MeUser
)

data class MeUser(
    val id: Int,
    val name: String,
    val email: String
)