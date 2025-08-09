package com.kfouri.route

import com.kfouri.Constants.UPLOAD_FOLDER
import com.kfouri.data.company.CompanyDataSource
import com.kfouri.data.product.device.Device
import com.kfouri.data.product.device.DeviceDataSource
import com.kfouri.data.responses.ErrorResponse
import com.kfouri.data.user.UserDataSource
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.utils.io.jvm.javaio.copyTo
import java.io.File

fun Route.deviceScreen(
    companyDataSource: CompanyDataSource,
    userDataSource: UserDataSource,
    deviceDataSource: DeviceDataSource
) {

    authenticate("user-allowed") {
        route("/device") {

            get("/paginated") {
                val page = call.queryParameters["page"] ?: ""
                if (!isValidPositiveNumberString(page)) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("Página no válida -$page-")
                    )
                    return@get
                }

                val query = call.queryParameters["query"] ?: ""

                val principal = call.principal<JWTPrincipal>()
                val result = getValidatedUser(principal, userDataSource, companyDataSource)

                result.fold(
                    onSuccess = { user ->
                        val devices = deviceDataSource.getDevicesPaginated(page.toInt(), query, user.companyId)
                        call.respond(HttpStatusCode.OK, devices)
                    },
                    onFailure = { e ->
                        call.respond(
                            HttpStatusCode.Conflict,
                            ErrorResponse(e.message ?: "Error")
                        )
                    }
                )
            }

            get("/screen") {
                val principal = call.principal<JWTPrincipal>()
                val result = getValidatedUser(principal, userDataSource, companyDataSource)

                result.fold(
                    onSuccess = { user ->
                        val devicesScreen = deviceDataSource.getDevicesScreen(user, "Device")
                        devicesScreen?.let {
                            call.respond(HttpStatusCode.OK, devicesScreen)
                        } ?:run {
                            call.respond(
                                HttpStatusCode.Conflict,
                                ErrorResponse("Pantalla no permitida")
                            )
                        }
                    },
                    onFailure = { e ->
                        call.respond(
                            HttpStatusCode.Conflict,
                            ErrorResponse(e.message ?: "Error")
                        )
                    }
                )
            }

            get("{code}") {
                val codeParam = call.parameters["code"]
                if (codeParam == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("ID inválido")
                    )
                    return@get
                }

                val principal = call.principal<JWTPrincipal>()
                val result = getValidatedUser(principal, userDataSource, companyDataSource)

                result.fold(
                    onSuccess = { user ->
                        val device = deviceDataSource.getDeviceByCode(codeParam, user.companyId)
                        device?.let {
                            call.respond(HttpStatusCode.OK, it)
                        } ?: call.respond(HttpStatusCode.NoContent, "{}")
                    },
                    onFailure = { e ->
                        call.respond(
                            HttpStatusCode.Conflict,
                            ErrorResponse(e.message ?: "Error")
                        )
                    }
                )
            }

            post("/create") {
                val principal = call.principal<JWTPrincipal>()

                val user = getCurrentUser(principal, userDataSource)

                user?.let {
                    val multipart = call.receiveMultipart()

                    var fileName: String? = null
                    val companyId = user.companyId
                    var code = ""
                    var sets = ""
                    var location = ""
                    var implements = ""

                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                // Aquí puedes manejar otros campos, por ejemplo:

                                when(part.name) {
                                    "code" -> {
                                        code = part.value
                                    }
                                    "sets" -> {
                                        sets = part.value
                                    }
                                    "location" -> {
                                        location = part.value
                                    }
                                    "implements" -> {
                                        implements = part.value
                                    }

                                }

                            }
                            is PartData.FileItem -> {
                                if (part.name == "file") {
                                    fileName = "device_${code}_${System.currentTimeMillis()}.jpg"
                                    val folder = File(UPLOAD_FOLDER)
                                    if (!folder.exists()) folder.mkdirs()

                                    val file = File(folder, fileName)
                                    val channel = part.provider()

                                    file.outputStream().use { output ->
                                        channel.copyTo(output)
                                    }
                                }
                            }
                            else -> Unit
                        }
                        part.dispose()
                    }

                    val device = deviceDataSource.getDeviceByCode(code, companyId)

                    device?.let {
                        call.respond(
                            HttpStatusCode.Conflict,
                            ErrorResponse("El dispositivo ya existe.")
                        )
                    } ?: run {
                        val serverHost = call.request.local.serverHost
                        val serverPort = call.request.local.serverPort

                        val newDevice = Device(
                            id = 0,
                            code = code,
                            sets = sets,
                            location = location,
                            implements = implements,
                            imageUrl = "$serverHost:$serverPort/$UPLOAD_FOLDER/$fileName",
                            companyId = companyId
                        )
                        val result = deviceDataSource.insertDevice(newDevice)
                        if (result) {
                            call.respond(HttpStatusCode.OK)
                        } else {
                            call.respond(HttpStatusCode.Conflict)
                            return@post
                        }
                    }
                } ?:run {
                    call.respond(
                        HttpStatusCode.Conflict,
                        ErrorResponse("El usuario no existe.")
                    )
                }
            }

            post("/update") {
                val principal = call.principal<JWTPrincipal>()

                val user = getCurrentUser(principal, userDataSource)

                user?.let {
                    val multipart = call.receiveMultipart()

                    var fileName: String? = null
                    val companyId = user.companyId
                    var code = ""
                    var sets = ""
                    var location = ""
                    var implements = ""

                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                // Aquí puedes manejar otros campos, por ejemplo:

                                when(part.name) {
                                    "code" -> {
                                        code = part.value
                                    }
                                    "sets" -> {
                                        sets = part.value
                                    }
                                    "location" -> {
                                        location = part.value
                                    }
                                    "implements" -> {
                                        implements = part.value
                                    }

                                }

                            }
                            is PartData.FileItem -> {
                                if (part.name == "image") {
                                    fileName = "device_${code}_${System.currentTimeMillis()}.jpg"
                                    val folder = File(UPLOAD_FOLDER)
                                    if (!folder.exists()) folder.mkdirs()

                                    val file = File(folder, fileName)
                                    val channel = part.provider()

                                    file.outputStream().use { output ->
                                        channel.copyTo(output)
                                    }
                                }
                            }
                            else -> Unit
                        }
                        part.dispose()
                    }

                    val device = deviceDataSource.getDeviceByCode(code, companyId)

                    device?.let {
                        val updateDevice = Device(
                            id = 0,
                            code = code,
                            sets = sets,
                            location = location,
                            implements = implements,
                            imageUrl = fileName,
                            companyId = companyId
                        )
                        val result = deviceDataSource.updateDevice(updateDevice)
                        if (result) {
                            call.respond(HttpStatusCode.OK)
                        } else {
                            call.respond(HttpStatusCode.Conflict)
                            return@post
                        }
                    } ?: run {
                        call.respond(
                            HttpStatusCode.Conflict,
                            ErrorResponse("El dispositivo no existe.")
                        )
                        return@post
                    }
                } ?:run {
                    call.respond(
                        HttpStatusCode.Conflict,
                        ErrorResponse("El usuario no existe.")
                    )
                }
            }
        }
    }
}

fun isValidPositiveNumberString(input: String): Boolean {
    if (input.isEmpty()) {
        return false // Una cadena vacía no es un número positivo
    }
    // Expresión regular para verificar si la cadena contiene solo dígitos
    val containsOnlyDigits = input.matches(Regex("^\\d+\$"))

    if (!containsOnlyDigits) {
        return false
    }

    return try {
        val number = input.toInt() // O toLong() si esperas números más grandes
        number >= 0 // Verifica que el número sea estrictamente positivo
    } catch (e: NumberFormatException) {
        // Esto podría ocurrir si el número es demasiado grande para Int/Long
        // o si de alguna manera la regex falló (poco probable con "^\\d+$")
        false
    }
}
