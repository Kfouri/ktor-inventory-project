package com.kfouri.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.kfouri.data.company.CompanyDataSource
import com.kfouri.data.user.UserDataSource
import com.kfouri.security.token.TokenConfig
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

fun Application.configureSecurity(
    userDataSource: UserDataSource,
    companyDataSource: CompanyDataSource,
    config: TokenConfig
) {

    authentication {
        jwt {
            realm = this@configureSecurity.environment.config.property("jwt.realm").getString()
            verifier(
                JWT
                    .require(Algorithm.HMAC256(config.secret))
                    .withAudience(config.audience)
                    .withIssuer(config.issuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(config.audience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }

        jwt("user-allowed") {
            realm = this@configureSecurity.environment.config.property("jwt.realm").getString()
            verifier(
                JWT
                    .require(Algorithm.HMAC256(config.secret))
                    .withAudience(config.audience)
                    .withIssuer(config.issuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(config.audience)) {
                    val userId = credential.payload.getClaim("userId").asString()
                    val user = userDataSource.getUserById( userId?.toInt() ?: 0)
                    user?.let { user ->
                        val isCompanyAllowed = companyDataSource.checkCompanyIsAllowed(user.companyId)

                        if (user.allowed && isCompanyAllowed) {
                            JWTPrincipal(credential.payload)
                        } else {
                            null
                        }
                    } ?: run {
                        null
                    }

                } else {
                    null
                }
            }
        }
    }

}