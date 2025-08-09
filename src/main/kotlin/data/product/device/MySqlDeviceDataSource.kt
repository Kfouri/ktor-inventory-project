package com.kfouri.data.product.device

import com.kfouri.database.MySqlDatabaseFactory
import com.kfouri.database.rowToDeviceModel
import com.kfouri.database.table.DeviceTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class MySqlDeviceDataSource : DeviceDataSource {
    override suspend fun getDevices(companyId: Int): List<Device> {
        return MySqlDatabaseFactory.query {
            DeviceTable.selectAll().where {
                DeviceTable.companyId eq companyId
            }.map(::rowToDeviceModel)
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