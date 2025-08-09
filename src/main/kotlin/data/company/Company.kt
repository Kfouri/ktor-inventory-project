package com.kfouri.data.company

import java.time.LocalDateTime

data class Company(
    val id: Int,
    val name: String,
    val phone: String,
    val address: String,
    val email: String,
    val city: String,
    val state: String,
    val cp: String,
    val logoUrl: String,
    val status: String,
    val allowed: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)