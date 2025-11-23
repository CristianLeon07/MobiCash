package com.example.mobicash.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mobicash.data.local.dao.BankAccountDao
import com.example.mobicash.data.local.dao.UserDao
import com.example.mobicash.data.local.entities.BankAccountEntity
import com.example.mobicash.data.local.entities.UserEntity

@Database(
    entities = [UserEntity::class, BankAccountEntity::class],
    version = 1,
    exportSchema = false
)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun bankAccountDao(): BankAccountDao

}