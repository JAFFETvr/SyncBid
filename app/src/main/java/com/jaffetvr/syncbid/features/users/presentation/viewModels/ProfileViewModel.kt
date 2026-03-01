package com.jaffetvr.syncbid.features.users.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaffetvr.syncbid.core.di.TokenManager
import com.jaffetvr.syncbid.features.users.data.datasource.remote.api.UserApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val username: String = "",
    val email: String = "",
    val createdAt: String = "",
    val isLoading: Boolean = false,
    val initials: String = ""
)

sealed interface ProfileUiEvent {
    data object NavigateToLogin : ProfileUiEvent
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val userApi: UserApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProfileUiEvent>()
    val events: SharedFlow<ProfileUiEvent> = _events.asSharedFlow()

    init {
        loadLocalProfile()
        fetchRemoteProfile()
    }

    private fun loadLocalProfile() {
        val username = tokenManager.getUsername() ?: ""
        val email = tokenManager.getEmail() ?: ""
        val createdAt = tokenManager.getCreatedAt() ?: ""
        _uiState.update {
            it.copy(
                username = username,
                email = email,
                createdAt = formatDate(createdAt),
                initials = username.take(2).uppercase()
            )
        }
    }

    private fun fetchRemoteProfile() {
        val userId = tokenManager.getUserId()
        if (userId <= 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = userApi.getUserProfile(userId)
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()!!.data!!
                    tokenManager.saveUsername(data.username)
                    tokenManager.saveEmail(data.email)
                    data.createdAt?.let { tokenManager.saveCreatedAt(it) }

                    _uiState.update {
                        it.copy(
                            username = data.username,
                            email = data.email,
                            createdAt = formatDate(data.createdAt ?: ""),
                            initials = data.username.take(2).uppercase(),
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onLogout() {
        tokenManager.clearAll()
        viewModelScope.launch {
            _events.emit(ProfileUiEvent.NavigateToLogin)
        }
    }

    private fun formatDate(iso: String): String {
        if (iso.isBlank()) return ""
        return try {
            val parts = iso.take(10).split("-")
            if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else iso
        } catch (e: Exception) {
            iso
        }
    }
}
