package com.kfouri.data.user

import com.kfouri.database.MySqlDatabaseFactory
import com.kfouri.database.rowToUserModel
import com.kfouri.database.table.UserTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class MySqlUserDataSource() : UserDataSource {
    override suspend fun getUserByEmail(email: String): User? {
        return MySqlDatabaseFactory.query {
            UserTable.selectAll().where {
                UserTable.email eq email
            }.map(::rowToUserModel).singleOrNull()
        }
    }

    override suspend fun getUserById(id: Int): User? {
        return MySqlDatabaseFactory.query {
            UserTable.selectAll().where {
                UserTable.id eq id
            }.map(::rowToUserModel).singleOrNull()
        }
    }

    override suspend fun insertUser(user: User): Boolean {
        return MySqlDatabaseFactory.query {
            UserTable.insert {
                it[id] = user.id
                it[email] = user.email
                it[password] = user.password
                it[salt] = user.salt
                it[companyId] = user.companyId
                it[name] = user.name
                it[allowed] = user.allowed
                it[emailVerified] = user.emailVerified
                it[verificationToken] = user.verificationToken
            }.insertedCount > 0
        }
    }

    override suspend fun verifyEmail(token: String): Boolean {
        val update = MySqlDatabaseFactory.query {
            UserTable.update({ UserTable.verificationToken eq token }) {
                it[emailVerified] = true
                it[verificationToken] = null
            }
        }

        return update > 0
    }
}