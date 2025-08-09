package com.kfouri.database.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object CompanyTable: Table("company") {

    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val phone = varchar("phone", 100)
    val address = varchar("address", length = 100)
    val email = varchar("email", length = 100)
    val city = varchar("city", length = 100)
    val state = varchar("state", length = 100)
    val cp = varchar("cp", length = 45)
    val logoUrl = varchar("logoUrl", length = 200)
    val status = varchar("status", length = 1)
    val allowed = bool("allowed")
    val createdAt = datetime("createdAt")
    val updatedAt = datetime("updatedAt")

    override val primaryKey by lazy { super.primaryKey ?: PrimaryKey(id) }
}