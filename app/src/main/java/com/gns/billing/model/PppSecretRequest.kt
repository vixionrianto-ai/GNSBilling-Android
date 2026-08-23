package com.gns.billing.model

data class PppSecretRequest(
    val username: String,
    val password: String,
    val profile: String,
    val service: String = "pppoe"
)
