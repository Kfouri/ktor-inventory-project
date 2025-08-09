package com.kfouri.database.table

import org.jetbrains.exposed.sql.Table

object HomeTable: Table("home") {
    val id = integer("id").autoIncrement()
    val title = varchar(name = "title", 100)
    val description = varchar(name = "description", 100)
    val icon = varchar(name = "icon", 100)
    val route = varchar(name = "route", 50)
    val order = integer(name = "order")
    val companyId = integer("companyId")

    override val primaryKey by lazy { super.primaryKey ?: PrimaryKey(id) }
}