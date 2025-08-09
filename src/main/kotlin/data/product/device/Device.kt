package com.kfouri.data.product.device

import kotlinx.serialization.Serializable

@Serializable
data class Device(
    val id: Int,
    val code: String,
    val sets: String?,
    val location: String?,
    val implements: String?,
    val imageUrl: String?,
    val companyId: Int
)