package com.example.mobicash.ui.viewmodels

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobicash.core.utils.HashUtils
import com.example.mobicash.domain.models.UserModel
import com.example.mobicash.domain.usecase.AddUserUseCase
import com.example.mobicash.domain.usecase.CreateBankAccountForUserUseCase
import com.example.mobicash.domain.usecase.DeleteUserAndAccountsUseCase
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
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(

    private val createBankAccountForUserUseCase: CreateBankAccountForUserUseCase,
    private val addUserUseCase: AddUserUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val loginUserUseCase: LoginUserUseCase,
    private val deleteUserAndAccountsUseCase: DeleteUserAndAccountsUseCase

) : ViewModel() {

    var user by mutableStateOf("")
        private set

    var userHashed by mutableStateOf("")
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

    // --- NUEVO ---
    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog

    private var pendingNewUser: UserModel? = null

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
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // ---------------------
    // Registro
    // ---------------------
    fun registerUser() {
        if (!validateInputs()) return

        viewModelScope.launch {
            _uiState.value = UiState.Loading

            // Si ya existe un usuario → pedir confirmación (Opción B)
            val existingUsers = getUserUseCase().first()
            if (existingUsers.isNotEmpty()) {

                val userHashed = HashUtils.sha256(user)
                pendingNewUser = UserModel(
                    user = user,
                    userHashed = userHashed,
                    name = name,
                    email = email,
                    pin = pin,
                    photo = null
                )

                _showDeleteDialog.value = true
                return@launch
            }

            // No hay usuarios → registrar normal
            registerNewUser()
        }
    }

    private suspend fun registerNewUser() {
        try {
            val userHashed = HashUtils.sha256(user)

            val newUser = UserModel(
                user = user,
                userHashed = userHashed,
                name = name,
                email = email,
                pin = pin,
                photo = null
            )

            addUserUseCase(newUser)
            createBankAccountForUserUseCase(newUser.userHashed)

            _uiState.value = UiState.Success(Unit)

        } catch (e: Exception) {
            _uiState.value = UiState.Error("Error al registrar: ${e.message}")
        }
    }

    // ---------------------
    // Confirmaciones
    // ---------------------
    fun onConfirmDelete() = viewModelScope.launch {
        val existingUsers = getUserUseCase().first()
        val oldUser = existingUsers.firstOrNull()
        val newUser = pendingNewUser ?: return@launch

        if (oldUser != null) {
            deleteUserAndAccountsUseCase(oldUser)
        }

        addUserUseCase(newUser)
        createBankAccountForUserUseCase(newUser.userHashed)

        _showDeleteDialog.value = false
        _uiState.value = UiState.Success(Unit)
    }

    fun onCancelDelete() {
        _showDeleteDialog.value = false
    }
}

