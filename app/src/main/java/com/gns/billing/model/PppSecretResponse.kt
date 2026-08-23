package com.gns.billing.model

data class PppSecretResponse(
    val success: Boolean = false,
    val message: String? = null,
    val data: List<PppSecret> = emptyList()
)

data class PppSecret(
    val id: String? = null,
    val username: String? = null,
    val password: String? = null,
    val profile: String? = null,
    val service: String? = null,
    val disabled: Boolean? = null
)
