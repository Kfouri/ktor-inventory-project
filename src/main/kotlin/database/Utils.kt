package com.kfouri.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun createTableWithTimestamps(vararg tables: Table) {
    transaction {
        SchemaUtils.create(*tables)

        tables.forEach { table ->
            if ("updatedAt" in table.columns.map { it.name }) {
                val tableName = table.tableName
                exec("""
                    ALTER TABLE `$tableName`
                    MODIFY COLUMN `updatedAt` DATETIME 
                    DEFAULT CURRENT_TIMESTAMP 
                    ON UPDATE CURRENT_TIMESTAMP
                """.trimIndent())
            }
            if ("createdAt" in table.columns.map { it.name }) {
                val tableName = table.tableName
                exec("""
                    ALTER TABLE `$tableName`
                    MODIFY COLUMN `createdAt` DATETIME 
                    DEFAULT CURRENT_TIMESTAMP
                """.trimIndent())
            }
        }
    }
}