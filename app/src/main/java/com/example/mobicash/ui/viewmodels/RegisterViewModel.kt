package com.example.mobicash.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobicash.domain.models.UserModel
import com.example.mobicash.domain.usecase.AddUserUseCase
import com.example.mobicash.domain.usecase.GetUserUseCase
import com.example.mobicash.domain.usecase.LoginUserUseCase
import com.example.mobicash.ui.utils.UiState
import com.example.mobicash.ui.utils.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val addUserUseCase: AddUserUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val loginUserUseCase: LoginUserUseCase
) : ViewModel() {

    var user by mutableStateOf("")
        private set

    var name by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    var pin by mutableStateOf("")
        private set

    var userError by mutableStateOf<String?>(null)
        private set

    var nameError by mutableStateOf<String?>(null)
        private set

    var emailError by mutableStateOf<String?>(null)
        private set

    var pinError by mutableStateOf<String?>(null)
        private set

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    // ---------------------
    // Updates
    // ---------------------
    fun onUserChange(value: String) {
        user = value
        userError = null
    }

    fun onNameChange(value: String) {
        name = value
        nameError = null
    }

    fun onEmailChange(value: String) {
        email = value
        emailError = null
    }

    fun onPinChange(value: String) {
        pin = value
        pinError = null
    }

    // ---------------------
    // Validaciones
    // ---------------------
    private fun validateInputs(): Boolean {
        var valid = true

        if (user.isBlank()) {
            userError = "El usuario es obligatorio"
            valid = false
        } else if (!isValidUser(user)) {
            userError = "Debe tener mayúsculas, números y un carácter especial"
            valid = false
        }

        if (name.isBlank()) {
            nameError = "Ingresa tu nombre"
            valid = false
        }

        if (email.isBlank()) {
            emailError = "Ingresa tu correo"
            valid = false
        } else if (!isValidEmail(email)) {
            emailError = "Correo inválido"
            valid = false
        }

        if (pin.length != 4 || pin.any { !it.isDigit() }) {
            pinError = "El PIN debe tener 4 dígitos numéricos"
            valid = false
        } else if (isSequential(pin) || isRepeated(pin)) {
            pinError = "El PIN no puede ser secuencial o repetido"
            valid = false
        }

        return valid
    }

    private fun isValidUser(user: String): Boolean {
        val hasUpper = user.any { it.isUpperCase() }
        val hasDigit = user.any { it.isDigit() }
        val hasSpecial = user.any { !it.isLetterOrDigit() }
        return user.length >= 6 && hasUpper && hasDigit && hasSpecial
    }

    private fun isSequential(pin: String): Boolean {
        val digits = pin.map { it.digitToInt() }
        val ascending = digits.zipWithNext().all { (a, b) -> b == a + 1 }
        val descending = digits.zipWithNext().all { (a, b) -> b == a - 1 }
        return ascending || descending
    }

    private fun isRepeated(pin: String): Boolean {
        return pin.toSet().size == 1
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // ---------------------
    // Registro
    // ---------------------
    fun registerUser() {
        if (!validateInputs()) return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                // Validar si usuario ya existe
                val existingUser = loginUserUseCase(user)
                if (existingUser != null) {
                    userError = "Este usuario ya existe"
                    _uiState.value = UiState.Error("El usuario ya está registrado")
                    return@launch
                }

                // Validar si email ya existe
                val allUsers = getUserUseCase().first() // Traemos todos los usuarios
                if (allUsers.any { it.email == email }) {
                    emailError = "Este correo ya fue registrado"
                    _uiState.value = UiState.Error("El correo ya está registrado")
                    return@launch
                }

                // Crear usuario
                val newUser = UserModel(
                    user = user,
                    name = name,
                    email = email,
                    pin = pin,
                    photo = null
                )

                addUserUseCase(newUser)

                _uiState.value = UiState.Success(Unit)

            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al registrar: ${e.message}")
            }
        }
    }
}
