package com.kfouri.data.product.device

import com.kfouri.data.responses.PaginatedResult
import com.kfouri.data.screen.model.Screen
import com.kfouri.data.user.User

interface DeviceDataSource {
    suspend fun getDevicesPaginated(page: Int, query: String, companyId: Int): PaginatedResult<Device>
    suspend fun getDevicesScreen(user: User, screenName: String): Screen?
    suspend fun getDeviceByCode(code: String, companyId: Int): Device?
    suspend fun insertDevice(device: Device): Boolean
    suspend fun updateDevice(device: Device): Boolean
}