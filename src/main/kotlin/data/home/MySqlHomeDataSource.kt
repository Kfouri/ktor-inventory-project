package com.kfouri.data.home

import com.kfouri.database.MySqlDatabaseFactory
import com.kfouri.database.rowToHomeModel
import com.kfouri.database.table.HomeTable
import org.jetbrains.exposed.sql.selectAll

class MySqlHomeDataSource : HomeDataSource {
    override suspend fun getHome(companyId: Int): List<HomeItem> {
        return MySqlDatabaseFactory.query {
            HomeTable
                .selectAll()
                .where {
                    HomeTable.companyId eq companyId
                }
                .orderBy(HomeTable.order)
                .map(::rowToHomeModel)
        }
    }
}