package com.example.mobicash.data.local

import android.content.Context
import androidx.room.Room
import com.example.mobicash.data.UserDatabase
import com.example.mobicash.data.local.dao.BankAccountDao
import com.example.mobicash.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): UserDatabase {
        return Room.databaseBuilder(
            context,
            UserDatabase::class.java,
            "MobiCashDatabase"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDao(userDatabase: UserDatabase): UserDao {
        return userDatabase.userDao()
    }

    @Provides
    fun provideBankAccountDao(db: UserDatabase): BankAccountDao {
        return db.bankAccountDao()
    }
}