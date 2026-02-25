package com.jaffetvr.syncbid.features.admin.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaffetvr.syncbid.features.admin.domain.entities.InventoryItem
import com.jaffetvr.syncbid.features.admin.domain.entities.InventoryStatus
import com.jaffetvr.syncbid.features.admin.domain.useCases.GetInventoryUseCase
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

data class InventoryUiState(
    val items: List<InventoryItem> = emptyList(),
    val filteredItems: List<InventoryItem> = emptyList(),
    val selectedStatus: InventoryStatus? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false
)

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val getInventoryUseCase: GetInventoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    private val _errors = MutableSharedFlow<String>()
    val errors: SharedFlow<String> = _errors.asSharedFlow()

    init {
        loadInventory()
    }

    fun loadInventory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getInventoryUseCase().fold(
                onSuccess = { items ->
                    _uiState.update { state ->
                        state.copy(
                            items = items,
                            filteredItems = applyFilter(items, state.selectedStatus),
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
                    _errors.emit(error.message ?: "Error al cargar inventario")
                }
            )
        }
    }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadInventory()
    }

    fun filterByStatus(status: InventoryStatus?) {
        _uiState.update { state ->
            state.copy(
                selectedStatus = status,
                filteredItems = applyFilter(state.items, status)
            )
        }
    }

    private fun applyFilter(items: List<InventoryItem>, status: InventoryStatus?): List<InventoryItem> =
        if (status == null) items else items.filter { it.status == status }
}
