package com.kfouri.database

import com.kfouri.data.company.Company
import com.kfouri.data.product.device.Device
import com.kfouri.database.table.UserTable
import com.kfouri.data.user.User
import com.kfouri.database.table.CompanyTable
import com.kfouri.database.table.DeviceTable
import org.jetbrains.exposed.sql.ResultRow

fun rowToUserModel(
    row: ResultRow
): User {
    return User(
        id = row[UserTable.id],
        email = row[UserTable.email],
        password = row[UserTable.password],
        salt = row[UserTable.salt],
        companyId = row[UserTable.companyId],
        name = row[UserTable.name],
        allowed = row[UserTable.allowed],
        emailVerified = row[UserTable.emailVerified],
        verificationToken = row[UserTable.verificationToken]
    )
}

fun rowToCompanyModel(
    row: ResultRow
): Company {
    return Company(
        id = row[CompanyTable.id],
        name = row[CompanyTable.name],
        phone = row[CompanyTable.phone],
        address = row[CompanyTable.address],
        email = row[CompanyTable.email],
        city = row[CompanyTable.city],
        state = row[CompanyTable.state],
        cp = row[CompanyTable.cp],
        logoUrl = row[CompanyTable.logoUrl],
        status = row[CompanyTable.status],
        allowed = row[CompanyTable.allowed],
        createdAt = row[CompanyTable.createdAt],
        updatedAt = row[CompanyTable.updatedAt]
    )
}

fun rowToDeviceModel(
    row: ResultRow
): Device {
    return Device(
        id = row[DeviceTable.id],
        code = row[DeviceTable.code],
        sets = row[DeviceTable.sets],
        location = row[DeviceTable.location],
        implements = row[DeviceTable.implements],
        imageUrl = row[DeviceTable.imageUrl],
        companyId = row[DeviceTable.companyId]
    )
}