package com.gns.billing.model

data class PaketResponse(

    val success: Boolean,

    val message: String,

    val data: List<Paket>

)