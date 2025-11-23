package com.example.mobicash.data.local.datasource

import com.example.mobicash.data.local.entities.BankAccountEntity

interface BankAccountDataSource {

    suspend fun insertAccount(account: BankAccountEntity): Long

    suspend fun getAccountByUser(userHashed: String): BankAccountEntity?

    suspend fun getAccountById(id: Int): BankAccountEntity?

    suspend fun updateAccount(account: BankAccountEntity)

    suspend fun userHasAccount(userHashed: String): Boolean
}
