package com.kfouri.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class SingInRequest(
    val email: String,
    val password: String
)