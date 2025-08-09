package com.kfouri.data.home

import kotlinx.serialization.Serializable

@Serializable
data class HomeScreen(
    val title: String,
    val list: List<HomeItem>,
)