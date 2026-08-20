package com.gns.billing.model

data class Pagination(
    val current_page: Int = 1,
    val last_page: Int = 1,
    val per_page: Int = 20,
    val total: Int = 0,
    val from: Int? = null,
    val to: Int? = null
)
