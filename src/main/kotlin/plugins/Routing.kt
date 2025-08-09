package com.kfouri.plugins

import com.kfouri.data.company.CompanyDataSource
import com.kfouri.data.product.device.DeviceDataSource
import com.kfouri.route.authenticate
import com.kfouri.data.user.UserDataSource
import com.kfouri.route.deviceScreen
import com.kfouri.route.getSecretInfo
import com.kfouri.route.homeScreen
import com.kfouri.security.hashing.HashingService
import com.kfouri.security.token.TokenConfig
import com.kfouri.security.token.TokenService
import com.kfouri.route.singIn
import com.kfouri.route.singUp
import com.kfouri.route.verifyEmail
import io.ktor.server.application.Application
import io.ktor.server.http.content.staticFiles
import io.ktor.server.routing.routing
import java.io.File

fun Application.configureRouting(
    userDataSource: UserDataSource,
    companyDataSource: CompanyDataSource,
    deviceDataSource: DeviceDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {
        singIn(hashingService, companyDataSource, userDataSource, tokenService, tokenConfig)
        singUp(hashingService, userDataSource)
        verifyEmail(userDataSource)
        authenticate()
        getSecretInfo()

        //Screens
        homeScreen()
        deviceScreen(companyDataSource, userDataSource, deviceDataSource)

        staticFiles("/uploads", File("uploads"))
    }
}