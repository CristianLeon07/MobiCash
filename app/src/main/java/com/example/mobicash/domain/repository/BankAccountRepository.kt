package com.example.mobicash.domain.repository

import com.example.mobicash.data.local.entities.BankAccountEntity

interface BankAccountRepository {

    suspend fun createBankAccountForUser(userId: String): BankAccountEntity

    suspend fun getAccountByUserId(userId: String): BankAccountEntity?

    suspend fun updateBalance(accountNumberHashed: String, newBalance: Long)

}