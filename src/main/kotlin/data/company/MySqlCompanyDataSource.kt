package com.kfouri.data.company

import com.kfouri.database.MySqlDatabaseFactory
import com.kfouri.database.rowToCompanyModel
import com.kfouri.database.table.CompanyTable
import org.jetbrains.exposed.sql.selectAll

class MySqlCompanyDataSource: CompanyDataSource {
    override suspend fun getCompanyById(id: Int): Company? {
        return MySqlDatabaseFactory.query {
            CompanyTable.selectAll().where {
                CompanyTable.id eq id
            }.map(::rowToCompanyModel).singleOrNull()
        }
    }

    override suspend fun checkCompanyIsAllowed(id: Int): Boolean {
        val company =  MySqlDatabaseFactory.query {
            CompanyTable.selectAll().where {
                CompanyTable.id eq id
            }.map(::rowToCompanyModel).singleOrNull()
        }

        return company?.let {
            company.allowed && company.status == "A"
        } == true
    }

}