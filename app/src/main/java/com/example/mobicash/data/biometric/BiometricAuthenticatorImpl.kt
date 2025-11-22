package com.example.mobicash.data.biometric

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class BiometricAuthenticatorImpl(
    private val activity: FragmentActivity
) : BiometricAuthenticator {

    override fun canAuthenticate(): Boolean {
        // 🛑 CORRECCIÓN: Usamos solo BIOMETRIC_STRONG.
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG

        val result = BiometricManager.from(activity).canAuthenticate(authenticators)
        return result == BiometricManager.BIOMETRIC_SUCCESS
    }

    override fun authenticate(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Validamos que el activity no esté destruido
        if (activity.isFinishing || activity.isDestroyed) {
            onError("Activity no disponible para autenticación.")
            return
        }

        val executor = ContextCompat.getMainExecutor(activity)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación biométrica")
            .setSubtitle("Usa tu huella para iniciar sesión")
            .setNegativeButtonText("Cancelar")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        try {
            val biometricPrompt = BiometricPrompt(
                activity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        executor.execute { onSuccess() } // Siempre en main thread
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        executor.execute { onError(errString.toString()) }
                    }

                    override fun onAuthenticationFailed() {
                        executor.execute { onError("Autenticación fallida.") }
                    }
                }
            )

            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            onError("Error al iniciar biometría: ${e.localizedMessage ?: "Desconocido"}")
        }
    }
}