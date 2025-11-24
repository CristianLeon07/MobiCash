package com.example.mobicash.data.repository

import com.example.mobicash.core.utils.CardUtils
import com.example.mobicash.data.local.datasource.BankAccountDataSource
import com.example.mobicash.data.local.entities.BankAccountEntity
import com.example.mobicash.domain.repository.BankAccountRepository
import javax.inject.Inject

class BankAccountRepositoryImpl @Inject constructor(
    private val dataSource: BankAccountDataSource
) : BankAccountRepository {

    // 1. CREAR CUENTA PARA EL USUARIO

    override suspend fun createBankAccountForUser(userHashed: String): BankAccountEntity {

        //  Generar número realista de tarjeta
        val cardNumber = CardUtils.generateCardNumber()

        // Encriptar usando AES (número real + IV)
        val (encryptedNumber, iv) = CardUtils.encryptAES(cardNumber)

        //  Hash del número de tarjeta (para búsquedas rápidas)
        val cardNumberHashed = CardUtils.sha256(cardNumber)

        // Construir la entidad EXACTA con tu tabla final
        val account = BankAccountEntity(
            userHashed = userHashed,
            accountNumberEncrypted = encryptedNumber,
            accountNumberIV = iv,
            cardNumberHashed = cardNumberHashed,
            balance = 5_000_000.0,
            accountType = "AHORROS",
            status = "ACTIVA",
            createdAt = System.currentTimeMillis()
        )

        //  Guardar en DB
        val id = dataSource.insertAccount(account)

        //  Retornar la cuenta con el ID real asignado por Room
        return account.copy(id = id.toInt())
    }


    // 2. OBTENER CUENTA POR USER HASHED
    override suspend fun getAccountByUserId(userHashed: String): BankAccountEntity? {
        return dataSource.getAccountByUser(userHashed)
    }


    // ACTUALIZAR SALDO
    override suspend fun updateBalance(accountNumberHashed: String, newBalance: Long) {

        // Obtener cuenta buscando por hash
        val account = dataSource.getAccountByUser(accountNumberHashed)
            ?: return

        // Crear versión modificada
        val updatedAccount = account.copy(
            balance = newBalance.toDouble()
        )

        // Persistir cambios
        dataSource.updateAccount(updatedAccount)
    }

    override suspend fun deleteByUserHashed(userHashed: String) {
        dataSource.deleteByUserHashed(userHashed)
    }

}
