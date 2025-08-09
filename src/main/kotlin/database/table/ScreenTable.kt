package com.kfouri.database.table

import org.jetbrains.exposed.sql.Table

object ScreenTable: Table("screen") {
    val id = integer("id").autoIncrement()
    val name = varchar(name = "name", 100)
    val title = varchar("title", 100)
    val hasBack = integer("hasBack")

    override val primaryKey by lazy { super.primaryKey ?: PrimaryKey(id) }
}