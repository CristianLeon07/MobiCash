package com.example.mobicash.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobicash.domain.models.CardInfo
import com.example.mobicash.domain.models.UserModel
import com.example.mobicash.domain.usecase.GetUserCardInfoUseCase
import com.example.mobicash.domain.usecase.GetUserUseCase
import com.example.mobicash.ui.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getUserCardInfoUseCase: GetUserCardInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<UserModel?>>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _cardInfo = MutableStateFlow<CardInfo?>(null)
    val cardInfo = _cardInfo.asStateFlow()

    init {
        loadUser()
    }

    // -----------------------------
    // CARGAR USUARIO DESDE ROOM
    // -----------------------------
    private fun loadUser() {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                // Tomamos solo la primera emisión para evitar que collect se ejecute varias veces
                val users = getUserUseCase().first()
                val user = users.firstOrNull()

                if (user != null) {
                    _uiState.value = UiState.Success(user)

                    // Cargar tarjeta asociada
                    loadCardData(user.userHashed)

                    Log.d("HomeViewModel", "Usuario cargado: $user")

                } else {
                    _uiState.value = UiState.Error("No hay usuario registrado en el dispositivo")
                }

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error al cargar usuario: ${e.message}")
                _uiState.value = UiState.Error("Error inesperado al cargar usuario")
            }
        }
    }

    // -----------------------------
    // CARGAR DATOS DE TARJETA
    // -----------------------------
    private fun loadCardData(userHashed: String) {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "Invocando GetUserCardInfoUseCase con: $userHashed")
                val info = getUserCardInfoUseCase(userHashed)
                Log.d("HomeViewModel", "Info de tarjeta recibida: $info")

                if (info != null) {
                    _cardInfo.value = info
                } else {
                    Log.w(
                        "HomeViewModel",
                        "No se encontró información de tarjeta para el userHashed"
                    )
                }

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error cargando tarjeta: ${e.message}")
            }
        }
    }
}
