package com.jaffetvr.syncbid.features.users.presentation.viewModels

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

data class StatusFilter(
    val status: InventoryStatus?,
    val label: String
)

data class FavoritesUiState(
    val items: List<InventoryItem> = emptyList(),
    val filteredItems: List<InventoryItem> = emptyList(),
    val selectedStatus: InventoryStatus? = null,
    val isLoading: Boolean = false,
    val statusFilters: List<StatusFilter> = listOf(StatusFilter(null, "Todas")),
    val itemCountLabel: String = "0 ITEMS"
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getInventoryUseCase: GetInventoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    private val _errors = MutableSharedFlow<String>()
    val errors: SharedFlow<String> = _errors.asSharedFlow()

    init {
        loadInventory()
    }

    private fun loadInventory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getInventoryUseCase().fold(
                onSuccess = { items ->
                    _uiState.update { state ->
                        state.copy(
                            items = items,
                            filteredItems = applyFilter(items, state.selectedStatus),
                            isLoading = false
                        ).mapToDisplay()
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    _errors.emit(error.message ?: "Error al cargar inventario")
                }
            )
        }
    }

    fun filterByStatus(status: InventoryStatus?) {
        _uiState.update { state ->
            state.copy(
                selectedStatus = status,
                filteredItems = applyFilter(state.items, status)
            )
        }
    }

    private fun applyFilter(
        items: List<InventoryItem>,
        status: InventoryStatus?
    ): List<InventoryItem> =
        if (status == null) items else items.filter { it.status == status }

    private fun FavoritesUiState.mapToDisplay(): FavoritesUiState {
        val activeCount = items.count { it.status == InventoryStatus.ACTIVE }
        val pendingCount = items.count { it.status == InventoryStatus.PENDING }
        val endedCount = items.count { it.status == InventoryStatus.ENDED }

        val filters = buildList {
            add(StatusFilter(null, "Todas"))
            add(StatusFilter(InventoryStatus.ACTIVE, "Activa ($activeCount)"))
            add(StatusFilter(InventoryStatus.PENDING, "Pendiente ($pendingCount)"))
            if (endedCount > 0) add(StatusFilter(InventoryStatus.ENDED, "Finalizada ($endedCount)"))
        }

        return copy(
            statusFilters = filters,
            itemCountLabel = "${items.size} ITEMS"
        )
    }
}
