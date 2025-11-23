package com.example.mobicash.core.di


import com.example.mobicash.data.local.datasource.BankAccountDataSource
import com.example.mobicash.data.local.datasource.BankAccountDataSourceImpl
import com.example.mobicash.data.repository.BankAccountRepositoryImpl
import com.example.mobicash.domain.repository.UserRepository
import com.example.mobicash.data.repository.UserRepositoryImpl
import com.example.mobicash.domain.repository.BankAccountRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MobiCashModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindBankAccountRepository(
        impl: BankAccountRepositoryImpl
    ): BankAccountRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    abstract fun bindBankAccountDataSource(
        impl: BankAccountDataSourceImpl
    ): BankAccountDataSource
}

