package com.kfouri.plugins

import com.kfouri.Constants.UPLOAD_FOLDER
import com.kfouri.data.company.CompanyDataSource
import com.kfouri.data.home.HomeDataSource
import com.kfouri.data.product.device.DeviceDataSource
import com.kfouri.route.authenticate
import com.kfouri.data.user.UserDataSource
import com.kfouri.route.deviceScreen
import com.kfouri.route.getSecretInfo
import com.kfouri.route.homeScreen
import com.kfouri.route.signIn
import com.kfouri.route.signUp
import com.kfouri.security.hashing.HashingService
import com.kfouri.security.token.TokenConfig
import com.kfouri.security.token.TokenService
import com.kfouri.route.verifyEmail
import io.ktor.server.application.Application
import io.ktor.server.http.content.staticFiles
import io.ktor.server.routing.routing
import java.io.File

fun Application.configureRouting(
    userDataSource: UserDataSource,
    companyDataSource: CompanyDataSource,
    homeDataSource: HomeDataSource,
    deviceDataSource: DeviceDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {
        signIn(hashingService, companyDataSource, userDataSource, tokenService, tokenConfig)
        signUp(hashingService, userDataSource)
        verifyEmail(userDataSource)
        authenticate()
        getSecretInfo()

        //Screens
        homeScreen(homeDataSource, userDataSource)
        deviceScreen(companyDataSource, userDataSource, deviceDataSource)

        staticFiles("/$UPLOAD_FOLDER", File(UPLOAD_FOLDER))
    }
}