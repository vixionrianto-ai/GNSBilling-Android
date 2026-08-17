package com.gns.billing.model

data class RouterResponse(
    val success: Boolean,
    val message: String,
    val data: List<Router>
)