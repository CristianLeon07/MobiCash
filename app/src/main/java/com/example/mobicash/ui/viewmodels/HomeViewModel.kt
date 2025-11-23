package com.example.mobicash.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobicash.domain.models.UserModel
import com.example.mobicash.domain.usecase.GetUserUseCase
import com.example.mobicash.ui.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {


    private val _uiState = MutableStateFlow<UiState<UserModel?>>(UiState.Idle)
    val uiState = _uiState.asStateFlow()


    init {
        loadUser()
    }


    // CARGAR USUARIO
    private fun loadUser() {
        try {
            _uiState.value = UiState.Loading

            viewModelScope.launch {
                getUserUseCase().collect { users ->

                    val user = users.firstOrNull()

                    if (user != null) {
                        _uiState.value = UiState.Success(user)
                    } else {
                        _uiState.value =
                            UiState.Error("No hay usuario registrado en el dispositivo")
                    }
                }
            }

        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error al cargar usuario: ${e.message}")
        }
    }

    fun logout() {
        _uiState.value = UiState.Error("logout")

    }
}



