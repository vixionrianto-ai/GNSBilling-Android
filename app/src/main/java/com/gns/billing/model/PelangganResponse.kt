package com.gns.billing.model

data class PelangganResponse(

    val success: Boolean,

    val message: String,

    val data: List<Pelanggan>,

    val pagination: Pagination

)