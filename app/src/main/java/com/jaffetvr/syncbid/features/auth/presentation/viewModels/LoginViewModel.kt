package com.jaffetvr.syncbid.features.auth.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaffetvr.syncbid.features.auth.domain.useCases.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoginSuccess: Boolean = false
)

sealed interface LoginUiEvent {
    data class ShowError(val message: String) : LoginUiEvent
    data object NavigateToDashboard : LoginUiEvent
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LoginUiEvent>()
    val events = _events.asSharedFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }

    fun onLoginClick() {
        val state = _uiState.value

        // Validaciones
        val emailError = when {
            state.email.isBlank() -> "El correo es obligatorio"
            !state.email.contains("@") -> "Correo inválido"
            else -> null
        }
        val passwordError = when {
            state.password.isBlank() -> "La contraseña es obligatoria"
            state.password.length < 8 -> "Mínimo 8 caracteres"
            else -> null
        }

        if (emailError != null || passwordError != null) {
            _uiState.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            loginUseCase(state.email, state.password).fold(
                onSuccess = { user ->
                    _uiState.update { it.copy(isLoading = false, isLoginSuccess = true) }
                    _events.emit(LoginUiEvent.NavigateToDashboard)
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(LoginUiEvent.ShowError(error.message ?: "Error desconocido"))
                }
            )
        }
    }
}
