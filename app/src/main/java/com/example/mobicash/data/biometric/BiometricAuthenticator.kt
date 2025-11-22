package com.example.mobicash.data.biometric

interface BiometricAuthenticator {
    fun canAuthenticate(): Boolean
    fun authenticate(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
}