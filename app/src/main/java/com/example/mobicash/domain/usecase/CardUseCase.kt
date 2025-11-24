package com.example.mobicash.domain.usecase

import com.example.mobicash.core.utils.CardUtils
import com.example.mobicash.domain.models.CardInfo
import com.example.mobicash.domain.models.UserModel
import com.example.mobicash.domain.repository.BankAccountRepository
import com.example.mobicash.domain.repository.UserRepository
import javax.inject.Inject

class CreateBankAccountForUserUseCase @Inject constructor(
    private val repository: BankAccountRepository
) {
    suspend operator fun invoke(userHashed: String) {
        repository.createBankAccountForUser(userHashed)
    }
}


class GetUserCardInfoUseCase @Inject constructor(
    private val repository: BankAccountRepository
) {
    suspend operator fun invoke(userHashed: String): CardInfo? {
        val account = repository.getAccountByUserId(userHashed) ?: return null

        // Descifrar el número real de tarjeta
        val realCardNumber = CardUtils.decryptAES(
            account.accountNumberEncrypted,
            account.accountNumberIV
        )
        val card = CardInfo(
            maskedCardNumber = CardUtils.formatoTarjeta(realCardNumber),
            last4 = realCardNumber.takeLast(4),
            balance = CardUtils.formatMoneda(account.balance),
            accountType = account.accountType,
            status = account.status
        )

        return card
    }
}

class DeleteUserAndAccountsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val bankAccountRepository: BankAccountRepository
) {
    suspend operator fun invoke(user: UserModel) {
        // 1. Borrar cuentas bancarias relacionadas
        bankAccountRepository.deleteByUserHashed(user.userHashed)
        // 2. Borrar usuario
        userRepository.delete(user)
    }
}

