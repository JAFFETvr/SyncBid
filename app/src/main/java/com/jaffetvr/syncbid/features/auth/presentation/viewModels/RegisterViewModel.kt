package com.jaffetvr.syncbid.features.auth.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaffetvr.syncbid.features.auth.domain.useCases.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val fullNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isFullNameValid: Boolean = false,
    val isEmailValid: Boolean = false,
    val isRegisterSuccess: Boolean = false
)

sealed interface RegisterUiEvent {
    data class ShowError(val message: String) : RegisterUiEvent
    data object NavigateToDashboard : RegisterUiEvent
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<RegisterUiEvent>()
    val events = _events.asSharedFlow()

    fun onFullNameChange(name: String) {
        _uiState.update {
            it.copy(
                fullName = name,
                fullNameError = null,
                isFullNameValid = name.length >= 3
            )
        }
    }

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailError = null,
                isEmailValid = email.contains("@") && email.contains(".")
            )
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }

    fun onRegisterClick() {
        val state = _uiState.value

        val fullNameError = if (state.fullName.length < 3) "Mínimo 3 caracteres" else null
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

        if (fullNameError != null || emailError != null || passwordError != null) {
            _uiState.update {
                it.copy(
                    fullNameError = fullNameError,
                    emailError = emailError,
                    passwordError = passwordError
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            registerUseCase(state.fullName, state.email, state.password).fold(
                onSuccess = { user ->
                    _uiState.update { it.copy(isLoading = false, isRegisterSuccess = true) }
                    _events.emit(RegisterUiEvent.NavigateToDashboard)
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(RegisterUiEvent.ShowError(error.message ?: "Error desconocido"))
                }
            )
        }
    }
}
