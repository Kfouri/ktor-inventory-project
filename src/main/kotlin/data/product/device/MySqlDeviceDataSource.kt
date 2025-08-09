package com.kfouri.data.product.device

import com.kfouri.data.responses.PaginatedInfo
import com.kfouri.data.responses.PaginatedResult
import com.kfouri.data.screen.model.Screen
import com.kfouri.data.user.User
import com.kfouri.database.MySqlDatabaseFactory
import com.kfouri.database.rowToDeviceModel
import com.kfouri.database.rowToScreen
import com.kfouri.database.table.DeviceTable
import com.kfouri.database.table.ScreenTable
import com.kfouri.database.table.ScreenXCompanyTable
import com.kfouri.database.table.UserAccessTable
import com.kfouri.database.table.UserTable
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

const val DEFAULT_PAGE_SIZE = 20

class MySqlDeviceDataSource : DeviceDataSource {

    override suspend fun getDevicesPaginated(
        page: Int,
        query: String,
        companyId: Int
    ): PaginatedResult<Device> {

        val pageSize = DEFAULT_PAGE_SIZE
        val currentPage = if (page < 1) 1 else page
        val currentOffset = ((currentPage - 1) * pageSize).toLong()

        val devicesList = MySqlDatabaseFactory.query {
            val select = DeviceTable.selectAll()
                .where {
                    (DeviceTable.companyId eq companyId)
                }
            if (query.isNotBlank()) {
                select.andWhere {
                    (DeviceTable.code like "%${query.trim()}%") or (
                            (DeviceTable.sets like "%${query.trim()}%") or
                                    (DeviceTable.implements like "%${query.trim()}%") or
                                    (DeviceTable.location like "%${query.trim()}%")
                            )
                }
            }
            select
                .orderBy(DeviceTable.code)
                .limit(pageSize)
                .offset(currentOffset)
                .map(::rowToDeviceModel)
        }

        val totalCount = getTotalDeviceCount(companyId)
        val totalPages = if (totalCount == 0L) 1 else (totalCount + pageSize - 1) / pageSize

        val nextPage = if (currentPage < totalPages) {
            currentPage + 1
        } else {
            null
        }

        return PaginatedResult(
            results = devicesList,
            info = PaginatedInfo(
                currentPage = currentPage,
                pageSize = pageSize,
                totalItems = totalCount,
                totalPages = (totalCount + pageSize - 1) / pageSize,
                nextPage = nextPage
            )
        )
    }

    suspend fun getTotalDeviceCount(companyId: Int): Long {
        return MySqlDatabaseFactory.query {
            DeviceTable
                .select(DeviceTable.id.count())
                .where { DeviceTable.companyId eq companyId }
                .first()[DeviceTable.id.count()]
        }
    }

    override suspend fun getDevicesScreen(user: User, screenName: String): Screen? {
        //Necesito confirmar si la empresa tiene la pantalla habilitada.
        //También necesito saber que tipo de acceso tiene el usuario sobre esa pantalla (Query, Update, Insert, Delete)
        //Obtener los datos del titulo de la pantalla y algun otro que haga falta (color, etc)
        //Devolver un objeto con todos estos datos

        return MySqlDatabaseFactory.query {
            val query = ScreenTable
                .join(
                    ScreenXCompanyTable,
                    JoinType.INNER,
                    onColumn = ScreenTable.id,
                    otherColumn = ScreenXCompanyTable.idScreen
                )
                .join(
                    UserTable,
                    JoinType.INNER,
                    onColumn = ScreenXCompanyTable.idCompany,
                    otherColumn = UserTable.companyId
                )
                .join(
                    UserAccessTable,
                    JoinType.INNER,
                    onColumn = ScreenTable.id,
                    otherColumn = UserAccessTable.idScreen,
                    additionalConstraint = { UserAccessTable.idUser eq UserTable.id } // d.idUser = c.id
                )

            val columnsToSelect = buildList<Expression<*>> {
                add(ScreenTable.title)
                add(ScreenTable.hasBack)
                add(UserAccessTable.access)
            }

            query
                .select(columnsToSelect)
                .where {
                    (UserTable.id eq user.id) and
                            (ScreenTable.name eq screenName) and
                            (ScreenXCompanyTable.status eq "A")
                }
                .map(::rowToScreen)
                .singleOrNull()
        }
    }

    override suspend fun getDeviceByCode(code: String, companyId: Int): Device? {
        return MySqlDatabaseFactory.query {
            DeviceTable.selectAll().where {
                (DeviceTable.code eq code) and
                        (DeviceTable.companyId eq companyId)
            }.map(::rowToDeviceModel).singleOrNull()
        }
    }

    override suspend fun insertDevice(device: Device): Boolean {
        return MySqlDatabaseFactory.query {
            DeviceTable.insert {
                it[code] = device.code
                it[sets] = device.sets ?: ""
                it[location] = device.location ?: ""
                it[implements] = device.implements ?: ""
                it[imageUrl] = device.imageUrl ?: ""
                it[companyId] = device.companyId
            }.insertedCount > 0
        }
    }

    override suspend fun updateDevice(device: Device): Boolean {
        return MySqlDatabaseFactory.query {
            DeviceTable.update {
                it[sets] = device.sets ?: ""
                it[location] = device.location ?: ""
                it[implements] = device.implements ?: ""
                device.imageUrl?.let { image ->
                    it[imageUrl] = image
                }
            } > 0
        }
    }
}