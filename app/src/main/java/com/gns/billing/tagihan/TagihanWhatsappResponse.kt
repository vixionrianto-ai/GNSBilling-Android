package com.gns.billing.tagihan

import com.google.gson.annotations.SerializedName

data class TagihanWhatsappResponse(
    @SerializedName("success")
    val success: Boolean = false,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("data")
    val data: TagihanWhatsappData? = null
)

data class TagihanWhatsappData(
    @SerializedName("url")
    val url: String? = null,
    @SerializedName("message")
    val message: String? = null
)