package com.kfouri.route

import com.kfouri.data.company.CompanyDataSource
import com.kfouri.data.user.User
import com.kfouri.data.user.UserDataSource
import io.ktor.server.auth.jwt.JWTPrincipal

suspend fun getValidatedUser(
    principal: JWTPrincipal?,
    userDataSource: UserDataSource,
    companyDataSource: CompanyDataSource
): Result<User> {

    val user = getCurrentUser(
        principal,
        userDataSource
    )

    return user?.let {
        val isCompanyAllowed = companyDataSource.checkCompanyIsAllowed(user.companyId)
        if (!isCompanyAllowed) {
            Result.failure(Exception("La empresa del usuario no tiene acceso permitido."))
        } else {
            Result.success(user)
        }
    } ?: run {
        Result.failure(Exception("El usuario no existe."))
    }

    /*
val userId = principal?.payload?.getClaim("userId")?.asString()?.toIntOrNull()
    ?: return Result.failure(Exception("El usuario no existe."))

val userr = userDataSource.getUserById(userId)
    ?: return Result.failure(Exception("El usuario no existe."))


 */

    /*
    val isCompanyAllowed = companyDataSource.checkCompanyIsAllowed(user.companyId)
    if (!isCompanyAllowed) {
        return Result.failure(Exception("La empresa del usuario no tiene acceso permitido."))
    }

    return Result.success(user)

     */
}

suspend fun getCurrentUser(
    principal: JWTPrincipal?,
    userDataSource: UserDataSource,
): User? {
    val userId = principal?.payload?.getClaim("userId")?.asString()?.toIntOrNull()

    return userId?.let {
        userDataSource.getUserById(userId)
    }
}