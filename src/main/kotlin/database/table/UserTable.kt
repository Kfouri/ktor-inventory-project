package com.kfouri.database.table

import org.jetbrains.exposed.sql.Table


object UserTable: Table("user") {

    val id = integer("id").autoIncrement()
    val email = varchar("email", 100)
    val password = varchar("password", 100)
    val name = varchar("name", length = 100)
    val salt = varchar("salt", length = 100)
    val companyId = integer("companyId")
    val allowed = bool("allowed")
    val emailVerified = bool("email_verified").default(false)
    val verificationToken = varchar("verification_token", 100).nullable()

    override val primaryKey by lazy { super.primaryKey ?: PrimaryKey(id) }
}
