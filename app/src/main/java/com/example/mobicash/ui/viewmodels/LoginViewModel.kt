package com.example.mobicash.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobicash.core.security.HashUtils
import com.example.mobicash.domain.models.UserModel
import com.example.mobicash.domain.usecase.LoginUserUseCase
import com.example.mobicash.ui.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase
) : ViewModel() {

    var user by mutableStateOf("")
        private set

    var pin by mutableStateOf("")
        private set

    var userError by mutableStateOf<String?>(null)
        private set

    var pinError by mutableStateOf<String?>(null)
        private set

    private val _uiState = MutableStateFlow<UiState<UserModel>>(UiState.Idle)
    val uiState: StateFlow<UiState<UserModel>> = _uiState

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
        var isValid = true

        if (user.isBlank()) {
            userError = "Ingresa tu usuario"
            isValid = false
        }

        if (pin.length != 4) {
            pinError = "El PIN debe tener 4 dígitos"
            isValid = false
        }

        return isValid
    }

    fun login() {
        if (!validateInputs()) return

        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                // Busca por hash del user (loginUserUseCase hace la conversión a hash)
                val userFound = loginUserUseCase(user)

                if (userFound == null) {
                    _uiState.value = UiState.Error("El usuario no existe")
                    return@launch
                }

                // userFound.pin contiene el hash guardado (pinHashed)
                val pinIsValid = HashUtils.verify(pin, userFound.pin)

                if (!pinIsValid) {
                    _uiState.value = UiState.Error("PIN incorrecto")
                    return@launch
                }

                _uiState.value = UiState.Success(userFound)

            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    "Ocurrió un error inesperado: ${e.message ?: "Error desconocido"}"
                )
            }
        }
    }
}
