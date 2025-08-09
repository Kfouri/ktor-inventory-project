package com.kfouri.route

import com.kfouri.data.home.HomeDataSource
import com.kfouri.data.home.HomeScreen
import com.kfouri.data.responses.ErrorResponse
import com.kfouri.data.user.UserDataSource
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.homeScreen(
    homeDataSource: HomeDataSource,
    userDataSource: UserDataSource
) {
    authenticate("user-allowed") {
        get("home") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.payload?.getClaim("userId")?.asString()
            userId?.let { id ->
                val user = userDataSource.getUserById(id.toInt())
                user?.let {
                    val homeItems = homeDataSource.getHome(user.companyId)

                    val result = HomeScreen(
                        title = "Home",
                        list = homeItems
                    )

                    call.respond(HttpStatusCode.OK, result)

                } ?: run {
                    call.respond(HttpStatusCode.Conflict, ErrorResponse("Usuario Inexistente"))
                }
            } ?: run {
                call.respond(HttpStatusCode.Conflict, ErrorResponse("Usuario Inexistente"))
            }
            return@get
        }
    }
}