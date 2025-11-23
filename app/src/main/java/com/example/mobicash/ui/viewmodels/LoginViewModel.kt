package com.example.mobicash.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobicash.core.utils.HashUtils
import com.example.mobicash.data.biometric.BiometricAuthenticator
import com.example.mobicash.domain.models.UserModel
import com.example.mobicash.domain.usecase.AuthenticateWithBiometricsUseCase
import com.example.mobicash.domain.usecase.CheckBiometricAvailableUseCase
import com.example.mobicash.domain.usecase.GetUserUseCase
import com.example.mobicash.domain.usecase.LoginUserUseCase
import com.example.mobicash.ui.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val checkBiometricAvailableUseCase: CheckBiometricAvailableUseCase,
    private val authenticateWithBiometricsUseCase: AuthenticateWithBiometricsUseCase
) : ViewModel() {

    // --- ESTADO DE LOGIN TRADICIONAL ---
    var user by mutableStateOf("")
        private set

    var pin by mutableStateOf("")
        private set

    var userError by mutableStateOf<String?>(null)
        private set

    var pinError by mutableStateOf<String?>(null)
        private set


    private val _uiState = MutableStateFlow<UiState<UserModel>>(UiState.Idle)
    val uiState = _uiState

    /** Para saber si la biometría está disponible y el botón debe mostrarse */
    var isBiometricAvailable by mutableStateOf(false)
        private set

    // 1. Estado interno para la disponibilidad del hardware
    private var isHardwareAvailable = false

    // 💡 CAMBIO CLAVE: Observar Room y el Hardware en el bloque INIT
    init {
        getUserUseCase().onEach { userList ->
            val isDataAvailable = userList.isNotEmpty()

            isBiometricAvailable = isHardwareAvailable && isDataAvailable
        }
            .launchIn(viewModelScope)
    }


// LÓGICA DE BIOMETRÍA (Utiliza Use Cases inyectados)

    /**
     * Verifica si el hardware de biometría es compatible.
     * La disponibilidad final depende también de si hay usuarios en Room (ver bloque init).
     */
    fun checkBiometricStatus(authenticator: BiometricAuthenticator) {

        // 1. Solo verificamos la compatibilidad del hardware y actualizamos el estado interno
        isHardwareAvailable = checkBiometricAvailableUseCase(authenticator)

        // 2. Forzamos la re-evaluación inmediata
        viewModelScope.launch {
            // Leemos el estado actual del Flow de Room (solo el primer valor)
            val userList = getUserUseCase().firstOrNull() ?: emptyList()
            val isDataAvailable = userList.isNotEmpty()

            isBiometricAvailable = isHardwareAvailable && isDataAvailable
        }
    }

    /**
     * Inicia el proceso de autenticación biométrica a través del Use Case.
     */
    fun startBiometricAuth(
        authenticator: BiometricAuthenticator,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Ejecuta el Use Case para iniciar el BiometricPrompt
        authenticateWithBiometricsUseCase(authenticator, onSuccess, onError)
    }

    /** Login exitoso por biometría */
    fun biometricLogin() {
        // NOTA: Esta lógica requiere ser mejorada para cargar el usuario REAL,
        // Por ahora, se mantiene el placeholder.
        _uiState.value = UiState.Success(UserModel(user = "biometric", "", "", "",pin = ""))
    }


// LÓGICA DE LOGIN TRADICIONAL

    fun onUserChange(value: String) {
        user = value
        userError = null
    }

    fun onPinChange(value: String) {
        if (value.length <= 4 && value.all { it.isDigit() }) {
            pin = value
            pinError = null
        }
    }

    private fun validateInputs(): Boolean {
        var valid = true

        if (user.isBlank()) {
            userError = "Ingresa tu usuario"
            valid = false
        }

        if (pin.length != 4) {
            pinError = "El PIN debe tener 4 dígitos"
            valid = false
        }

        return valid
    }

    fun login() {
        if (!validateInputs()) return

        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                val userFound = loginUserUseCase(user)

                if (userFound == null) {
                    _uiState.value = UiState.Error("El usuario no existe")
                    return@launch
                }

                // Asumo que HashUtils está implementado
                val pinIsValid = HashUtils.verify(pin, userFound.pin)

                if (!pinIsValid) {
                    _uiState.value = UiState.Error("PIN incorrecto")
                    return@launch
                }

                // 💡 RECUERDA: Si quieres usar biometría, aquí deberías guardar userFound.id en SharedPreferences/DataStore.

                _uiState.value = UiState.Success(userFound)

            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error: ${e.message}")
            }
        }
    }
}