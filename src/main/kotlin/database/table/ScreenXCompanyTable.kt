package com.kfouri.database.table

import org.jetbrains.exposed.sql.Table

object ScreenXCompanyTable: Table("screenxcompany") {
    val idScreen = integer("idscreen")
    val idCompany = integer(name = "idcompany")
    val status = varchar("status", 1)

    override val primaryKey = PrimaryKey(idScreen, idCompany, name = "PK_ScreenXCompany")
}