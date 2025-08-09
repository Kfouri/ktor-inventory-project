package com.kfouri.data.product.device

interface DeviceDataSource {
    suspend fun getDevices(companyId: Int): List<Device>
    suspend fun getDeviceByCode(code: String, companyId: Int): Device?
    suspend fun insertDevice(device: Device): Boolean
    suspend fun updateDevice(device: Device): Boolean
}