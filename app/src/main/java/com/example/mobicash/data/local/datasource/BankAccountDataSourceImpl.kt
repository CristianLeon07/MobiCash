package com.example.mobicash.data.local.datasource

import com.example.mobicash.data.local.dao.BankAccountDao
import com.example.mobicash.data.local.entities.BankAccountEntity
import javax.inject.Inject

class BankAccountDataSourceImpl @Inject constructor(
    private val bankAccountDao: BankAccountDao
) : BankAccountDataSource {


    override suspend fun insertAccount(account: BankAccountEntity): Long {
        return bankAccountDao.insertAccount(account)
    }

    override suspend fun getAccountByUser(userHashed: String): BankAccountEntity? {
        return bankAccountDao.getAccountByUser(userHashed)
    }

    override suspend fun getAccountById(id: Int): BankAccountEntity? {
        return bankAccountDao.getAccountById(id)
    }

    override suspend fun updateAccount(account: BankAccountEntity) {
        bankAccountDao.updateAccount(account)
    }


    override suspend fun userHasAccount(userHashed: String): Boolean {
        return bankAccountDao.countAccountsByUser(userHashed) > 0
    }
}
