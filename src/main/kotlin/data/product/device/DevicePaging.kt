package com.kfouri.data.product.device

import kotlinx.serialization.Serializable

@Serializable
data class DevicePaging(
    val info: PagingInfo,
    val results: List<Device>
)

@Serializable
data class PagingInfo(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?
)