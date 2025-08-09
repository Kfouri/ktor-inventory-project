package com.kfouri.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class SingUpRequest(
    val email: String,
    val password: String,
    val name: String
)