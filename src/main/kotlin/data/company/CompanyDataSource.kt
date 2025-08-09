package com.kfouri.data.company

interface CompanyDataSource {
    suspend fun getCompanyById(id: Int): Company?
    suspend fun checkCompanyIsAllowed(id: Int): Boolean
}