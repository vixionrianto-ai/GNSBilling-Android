package com.gns.billing.model

data class Pagination(

    val current_page: Int,

    val last_page: Int,

    val per_page: Int,

    val total: Int,

    val from: Int?,

    val to: Int?

)