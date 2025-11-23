package com.example.mobicash.data.local.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mobicash.data.local.entities.BankAccountEntity

@Dao
interface BankAccountDao {

    //Crear cuenta
    @Insert
    suspend fun insertAccount(account: BankAccountEntity): Long

    // Obtener la cuenta del usuario
    @Query("SELECT * FROM bank_accounts WHERE userHashed = :userHashed LIMIT 1")
    suspend fun getAccountByUser(userHashed: String): BankAccountEntity?

    // Obtener por id
    @Query("SELECT * FROM bank_accounts WHERE id = :id")
    suspend fun getAccountById(id: Int): BankAccountEntity?

    // actualizar datos de la cuenta ej: saldo
    @Update
    suspend fun  updateAccount(account: BankAccountEntity)

    //verificar si el usuario ya tiene cuenta
    @Query("SELECT COUNT(*) FROM bank_accounts WHERE userHashed = :userHashed")
    suspend fun countAccountsByUser(userHashed: String): Int
}