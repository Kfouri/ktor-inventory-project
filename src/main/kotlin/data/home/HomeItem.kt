package com.kfouri.data.home

import kotlinx.serialization.Serializable

@Serializable
data class HomeItem(
    val title: String,
    val description: String,
    val icon: String,
    val route: String
)