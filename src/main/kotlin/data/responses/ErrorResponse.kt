package com.kfouri.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val message: String)