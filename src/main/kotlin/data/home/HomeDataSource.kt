package com.kfouri.data.home

interface HomeDataSource {
    suspend fun getHome(companyId: Int): List<HomeItem>
}