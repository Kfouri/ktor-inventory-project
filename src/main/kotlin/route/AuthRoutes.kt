package com.kfouri.route

import com.kfouri.data.company.CompanyDataSource
import com.kfouri.data.feedback.Button
import com.kfouri.data.feedback.Feedback
import com.kfouri.data.requests.SignInRequest
import com.kfouri.data.requests.SignUpRequest
import com.kfouri.data.responses.AuthResponse
import com.kfouri.data.responses.ErrorResponse
import com.kfouri.data.user.User
import com.kfouri.data.user.UserDataSource
import com.kfouri.security.hashing.HashingService
import com.kfouri.security.hashing.SaltedHash
import com.kfouri.security.token.TokenClaim
import com.kfouri.security.token.TokenConfig
import com.kfouri.security.token.TokenService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.get
import jakarta.mail.Authenticator
import jakarta.mail.Message
import jakarta.mail.PasswordAuthentication
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import java.util.Properties
import java.util.UUID

fun Route.signUp(
    hashingService: HashingService,
    userDataSource: UserDataSource
) {

    post("signup") {
        val request = runCatching { call.receiveNullable<SignUpRequest>() }.getOrNull() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val areFieldsBlank = request.email.isBlank() || request.password.isBlank() || request.name.isBlank()
        val isPwToShort = request.password.length < 6

        if (areFieldsBlank) {
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse("Hay campos vacíos.")
            )
            return@post
        }
        if (isPwToShort) {
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse("Contraseña muy corta.")
            )
            return@post
        }

        val checkUser = userDataSource.getUserByEmail(request.email)
        if (checkUser != null) {
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse("El usuario ya existe.")
            )
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)

        val emailToken = UUID.randomUUID().toString()

        val user = User(
            id = 0,
            email = request.email,
            password = saltedHash.hash,
            name = request.name,
            salt = saltedHash.salt,
            companyId = -1,
            allowed = false,
            emailVerified = false,
            verificationToken = emailToken
        )

        val wasAck = userDataSource.insertUser(user)
        if (wasAck) {
            sendVerificationEmail(request.email, emailToken)
        } else {
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse("Problemas al enviar el email.")
            )
            return@post
        }

        val feedbackResponse = Feedback(
            hasBackButton = false,
            image = "success",
            title = "Usuario creado correctamente",
            description = "Te hemos enviado un mail a ${request.email} con un link para que lo valides.",
            button = Button(title = "Ir a Login", route = "signIn")
        )

        call.respond(HttpStatusCode.OK, feedbackResponse)

    }
}

fun Route.signIn(
    hashingService: HashingService,
    companyDataSource: CompanyDataSource,
    userDataSource: UserDataSource,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("signin") {
        val request = runCatching { call.receiveNullable<SignInRequest>() }.getOrNull() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = userDataSource.getUserByEmail(request.email)
        if (user == null) {
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse("Email o contraseña incorrecta.")
            )
            return@post
        }

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt
            )
        )

        if (!isValidPassword) {
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse("Email o contraseña incorrecta.")
            )
            return@post
        }

        val isCompanyAllowed = companyDataSource.checkCompanyIsAllowed(user.companyId)
        if (!isCompanyAllowed) {
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse("El usuario no tiene una empresa asignada.")
            )
            return@post
        }

        if (!user.allowed) {
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse("El usuario no tiene permisos para acceder a la aplicación.")
            )
            return@post
        }

        if (!user.emailVerified) {
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse("El usuario no validó su correo electrónico.")
            )
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            )
        )

        call.respond(
            status = HttpStatusCode.OK,
            message = AuthResponse(
                token = token
            )
        )
    }
}

fun Route.authenticate() {
    authenticate {
        get("authenticate") {
            println("Authenticate OK")
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getSecretInfo() {
    authenticate {
        get("secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            call.respond(HttpStatusCode.OK, "Your userId is $userId")
        }
    }
}

fun Route.verifyEmail(
    userDataSource: UserDataSource,
) {

    get("/verify-email") {
        val token = call.request.queryParameters["token"]

        if (token == null) {
            call.respond(HttpStatusCode.BadRequest, "Token faltante")
            return@get
        }

        val updated = userDataSource.verifyEmail(token)

        if (updated) {
            call.respond(HttpStatusCode.OK, "Cuenta verificada con éxito")
        } else {
            call.respond(HttpStatusCode.NotFound, "Token inválido o expirado")
        }
    }
}

fun sendVerificationEmail(email: String, token: String) {
    val session = createMailSession()
    val message = MimeMessage(session).apply {
        setFrom(InternetAddress("noreply@tuapp.com"))
        setRecipients(Message.RecipientType.TO, InternetAddress.parse(email))
        subject = "Verifica tu cuenta"
        setText("Hacé clic en este link para verificar tu cuenta:\nhttp://127.0.0.1:8080/verify-email?token=$token")
    }
    Transport.send(message)
}

fun createMailSession(): Session {
    val props = Properties().apply {
        put("mail.smtp.host", "sandbox.smtp.mailtrap.io")
        put("mail.smtp.port", "587")
        put("mail.smtp.auth", "true")
        put("mail.smtp.starttls.enable", "true")
    }

    return Session.getInstance(props, object : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication("a9a915133254a1", "c3e930a5653c4f")
        }
    })
}