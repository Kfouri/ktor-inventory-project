package com.kfouri.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class HomeRequest(
    val token: String
)