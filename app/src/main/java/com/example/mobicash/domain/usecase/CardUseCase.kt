package com.example.mobicash.domain.usecase

import com.example.mobicash.core.utils.CardUtils
import com.example.mobicash.domain.models.CardInfo
import com.example.mobicash.domain.repository.BankAccountRepository
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

        return CardInfo(
            maskedCardNumber = CardUtils.maskCardNumber(realCardNumber),
            last4 = realCardNumber.takeLast(4),
            balance = account.balance,
            accountType = account.accountType,
            status = account.status
        )
    }
}
