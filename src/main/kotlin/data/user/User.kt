package com.kfouri.data.user

data class User(
    val id: Int,
    val email: String,
    val password: String,
    val salt: String,
    val companyId: Int,
    val name: String,
    val allowed: Boolean,
    val emailVerified: Boolean,
    val verificationToken: String?
)