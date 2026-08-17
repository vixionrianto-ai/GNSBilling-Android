package com.gns.billing.model

data class ProfileResponse(
    val success: Boolean,
    val message: String,
    val data: List<String>
)