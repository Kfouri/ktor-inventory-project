package com.kfouri.database

import com.kfouri.database.table.DeviceTable
import com.kfouri.database.table.UserTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object MySqlDatabaseFactory {

    private val BD_MYSQL_USER = System.getenv("BD_MYSQL_USER") ?: ""
    private val BD_MYSQL_PASSWORD = System.getenv("BD_MYSQL_PASSWORD") ?: ""
    private val BD_MYSQL_DATABASE = System.getenv("BD_MYSQL_DATABASE") ?: ""

    fun init() {
        Database.connect(
            "jdbc:mysql://localhost:3306/$BD_MYSQL_DATABASE",
            driver = "com.mysql.cj.jdbc.Driver",
            user = BD_MYSQL_USER,
            password = BD_MYSQL_PASSWORD
        )

        transaction {
            SchemaUtils.create(UserTable)
            //SchemaUtils.create(DeviceTable)
            createTableWithTimestamps(DeviceTable)
        }
    }


    suspend fun <T> query (block: () -> T) : T = newSuspendedTransaction(Dispatchers.IO) {
        block()
    }
}