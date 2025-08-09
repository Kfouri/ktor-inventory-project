package com.kfouri.database.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object DeviceTable: Table("device") {
    val id = integer("id").autoIncrement()
    val code = varchar(name = "code", 50)
    val sets = varchar("sets", 100).nullable()
    val location = varchar("location", 100).nullable()
    val implements = varchar("implements", 100).nullable()
    val imageUrl = varchar("imageUrl", 100).nullable()
    val companyId = integer("companyId")
    val createdAt = datetime("createdAt")
    val updatedAt = datetime("updatedAt")

    override val primaryKey by lazy { super.primaryKey ?: PrimaryKey(id) }
}