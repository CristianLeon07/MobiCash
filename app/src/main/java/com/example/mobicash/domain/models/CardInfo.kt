package com.example.mobicash.domain.models

import com.google.android.gms.common.internal.AccountType

data class CardInfo(
    val maskedCardNumber: String,
    val last4: String,
    val balance: Double,
    val accountType: String,
    val status : String
)