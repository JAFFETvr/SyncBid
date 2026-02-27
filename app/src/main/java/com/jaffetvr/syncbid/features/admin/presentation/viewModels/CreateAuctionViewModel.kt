package com.jaffetvr.syncbid.features.admin.presentation.viewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaffetvr.syncbid.features.admin.domain.useCases.CreateAuctionUseCase
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

data class CreateAuctionUiState(
    val name: String = "",
    val description: String = "",
    val basePrice: String = "",
    val durationHours: Int = 24,
    val isLoading: Boolean = false,
    val isNameValid: Boolean = true,
    val isDescValid: Boolean = true,
    val isPriceValid: Boolean = true,
    val imageUri: Uri? = null
)

sealed interface CreateAuctionEvent {
    data object Success : CreateAuctionEvent
    data class Error(val message: String) : CreateAuctionEvent
}

@HiltViewModel
class CreateAuctionViewModel @Inject constructor(
    private val createAuctionUseCase: CreateAuctionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateAuctionUiState())
    val uiState: StateFlow<CreateAuctionUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CreateAuctionEvent>()
    val events: SharedFlow<CreateAuctionEvent> = _events.asSharedFlow()

    val durationOptions = listOf(1, 6, 12, 24, 48, 72)

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value, isNameValid = true) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value, isDescValid = true) }
    }

    fun onBasePriceChange(value: String) {
        _uiState.update { it.copy(basePrice = value, isPriceValid = true) }
    }

    fun onDurationChange(hours: Int) {
        _uiState.update { it.copy(durationHours = hours) }
    }

    fun onImageChange(uri: Uri) {
        _uiState.update { it.copy(imageUri = uri) }
    }

    fun submit() {
        val state = _uiState.value
        val nameValid = state.name.isNotBlank()
        val descValid = state.description.isNotBlank()
        val priceValid = state.basePrice.toDoubleOrNull()?.let { it > 0 } ?: false

        if (!nameValid || !descValid || !priceValid) {
            _uiState.update {
                it.copy(
                    isNameValid = nameValid,
                    isDescValid = descValid,
                    isPriceValid = priceValid
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // CORRECCIÓN: Se eliminó el parámetro 'category' que ya no existe en el UseCase
            createAuctionUseCase(
                name = state.name,
                description = state.description,
                basePrice = state.basePrice.toDouble(),
                durationHours = state.durationHours,
                imageUri = state.imageUri
            ).fold(
                onSuccess = {
                    _uiState.update { CreateAuctionUiState() }
                    _events.emit(CreateAuctionEvent.Success)
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(
                        CreateAuctionEvent.Error(error.message ?: "Error desconocido")
                    )
                }
            )
        }
    }
}