package com.example.mobicash.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mobicash.data.local.UserDao
import com.example.mobicash.data.local.UserEntity

@Database(entities = [UserEntity::class], version = 2, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

}