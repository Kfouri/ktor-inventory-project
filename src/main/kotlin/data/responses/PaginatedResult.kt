package com.kfouri.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResult<T>(
    val info: PaginatedInfo,
    val results: List<T>
)

@Serializable
data class PaginatedInfo(
    val currentPage: Int,
    val pageSize: Int,
    val totalItems: Long,
    val totalPages: Long,
    val nextPage: Int?
)
