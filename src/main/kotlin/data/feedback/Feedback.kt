package com.kfouri.data.feedback

import kotlinx.serialization.Serializable

@Serializable
data class Feedback(
    val hasBackButton: Boolean,
    val image: String,
    val title: String,
    val description: String,
    val button: Button
)

@Serializable
data class Button(
    val title: String,
    val route: String
)