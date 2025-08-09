package com.kfouri

import com.kfouri.data.company.MySqlCompanyDataSource
import com.kfouri.data.home.MySqlHomeDataSource
import com.kfouri.data.product.device.MySqlDeviceDataSource
import com.kfouri.data.user.MySqlUserDataSource
import com.kfouri.database.MySqlDatabaseFactory
import com.kfouri.plugins.configureRouting
import com.kfouri.plugins.configureSecurity
import com.kfouri.plugins.configureSerialization
import com.kfouri.security.hashing.SHA256HashingService
import com.kfouri.security.token.JWTTokenService
import com.kfouri.security.token.TokenConfig
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    MySqlDatabaseFactory.init()

    val userDataSource = MySqlUserDataSource()
    val companyDataSource = MySqlCompanyDataSource()
    val deviceDataSource = MySqlDeviceDataSource()
    val homeDataSource = MySqlHomeDataSource()

    val tokenService = JWTTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 1000L * 60L * 60L * 24L,
        secret = System.getenv("SECRET")
    )
    val hashingService = SHA256HashingService()

    configureSecurity(userDataSource, companyDataSource, tokenConfig)
    configureRouting(
        userDataSource,
        companyDataSource,
        homeDataSource,
        deviceDataSource,
        hashingService,
        tokenService,
        tokenConfig
    )
    configureSerialization()
}
