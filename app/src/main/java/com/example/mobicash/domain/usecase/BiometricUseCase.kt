package com.example.mobicash.domain.usecase

import com.example.mobicash.data.biometric.BiometricAuthenticator
import javax.inject.Inject

class CheckBiometricAvailableUseCase @Inject constructor() {

    operator fun invoke(authenticator: BiometricAuthenticator): Boolean {
        return authenticator.canAuthenticate()
    }
}


class AuthenticateWithBiometricsUseCase @Inject constructor() {

    operator fun invoke(
        authenticator: BiometricAuthenticator,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        authenticator.authenticate(onSuccess, onError)
    }
}
