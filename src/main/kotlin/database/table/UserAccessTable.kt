package com.kfouri.database.table

import org.jetbrains.exposed.sql.Table

object UserAccessTable: Table("useraccess") {
    val idUser = integer("iduser")
    val idScreen = integer(name = "idscreen")
    val access = varchar("access", 1)

    override val primaryKey = PrimaryKey(idUser, idScreen, name = "PK_UserAccess")
}