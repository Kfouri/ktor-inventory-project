package com.kfouri.data.screen.model

import kotlinx.serialization.Serializable

@Serializable
data class Screen(
    val title: String,
    val hasBack: Boolean,
    val access: String
)