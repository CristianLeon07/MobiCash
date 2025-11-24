package com.example.mobicash.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo


@Entity(tableName = "bank_accounts")
data class BankAccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userHashed: String,
    val accountNumberEncrypted: String,
    val accountNumberIV: String,
    val cardNumberHashed: String,
    val balance: Double = 5_000_000.0,
    val accountType: String = "AHORROS",
    val status: String = "ACTIVA",
    val createdAt: Long = System.currentTimeMillis()
)
