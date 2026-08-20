package com.gns.billing.model

data class PelangganResponse(
    val success: Boolean = false,
    val message: String = "",
    val data: List<Pelanggan> = emptyList(),
    val pagination: Pagination = Pagination()
)
