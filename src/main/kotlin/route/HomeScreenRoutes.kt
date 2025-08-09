package com.kfouri.route

import com.kfouri.data.home.HomeItem
import com.kfouri.data.home.HomeScreen
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.homeScreen() {

    authenticate("user-allowed") {
        get("home") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.payload?.getClaim("userId")?.asString()
            //call.respondText("Hola tu id es: $userId")

            val list = ArrayList<HomeItem>()

            list.add(
                HomeItem(
                    title = "Inventario",
                    description = "Listado de inventario",
                    icon = "http://localhost:8080/uploads/inventary.png",
                    deeplink = "app://inventario"
                )
            )

            val result = HomeScreen(
                title = "Home",
                list = list
            )

            call.respond(HttpStatusCode.OK, result)
            return@get
        }
    }
}