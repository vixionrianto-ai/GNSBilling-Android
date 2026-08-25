package com.gns.billing.model

data class PppSecretEditResponse(
    val success: Boolean = false,
    val data: PppSecret? = null,
    val message: String? = null
)

data class PppProfileResponse(
    val success: Boolean = false,
    val data: List<PppProfile> = emptyList(),
    val message: String? = null
)

data class PppProfile(
    val id: String? = null,
    val name: String? = null,
    val local_address: String? = null,
    val remote_address: String? = null,
    val rate_limit: String? = null,
    val only_one: String? = null
)
