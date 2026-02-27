package com.jaffetvr.syncbid.features.users.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaffetvr.syncbid.core.di.TokenManager
import com.jaffetvr.syncbid.features.users.domain.entities.Auction
import com.jaffetvr.syncbid.features.users.domain.entities.AuctionStatus
import com.jaffetvr.syncbid.features.users.domain.useCases.GetAuctionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val auctions: List<Auction> = emptyList(),
    val filteredAuctions: List<Auction> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategory: String = "Todas",
    val categories: List<String> = listOf("Todas", "Electrónica", "Arte", "Autos"),
    val liveCount: Int = 0,
    val userName: String = "Usuario"
)

sealed interface DashboardUiEvent {
    data class ShowError(val message: String) : DashboardUiEvent
    data class NavigateToDetail(val auctionId: String) : DashboardUiEvent
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getAuctionsUseCase: GetAuctionsUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DashboardUiState(
            userName = tokenManager.getUsername() ?: "Usuario"
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<DashboardUiEvent>()
    val events = _events.asSharedFlow()

    init {
        observeAuctions()
        refreshAuctions()
    }

    private fun observeAuctions() {
        viewModelScope.launch {
            getAuctionsUseCase().collect { auctions ->
                _uiState.update { state ->
                    val filtered = filterAuctions(auctions, state.selectedCategory)
                    state.copy(
                        auctions = auctions,
                        filteredAuctions = filtered,
                        liveCount = auctions.count { it.status == AuctionStatus.LIVE }
                    )
                }
            }
        }
    }

    private fun refreshAuctions() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            getAuctionsUseCase.refresh().fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, error = null) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                    _events.emit(DashboardUiEvent.ShowError(error.message ?: "Error al cargar"))
                }
            )
        }
    }

    fun onCategorySelected(category: String) {
        _uiState.update { state ->
            val filtered = filterAuctions(state.auctions, category)
            state.copy(selectedCategory = category, filteredAuctions = filtered)
        }
    }

    fun onAuctionClick(auctionId: String) {
        viewModelScope.launch {
            _events.emit(DashboardUiEvent.NavigateToDetail(auctionId))
        }
    }

    fun onRefresh() {
        refreshAuctions()
    }

    private fun filterAuctions(auctions: List<Auction>, category: String): List<Auction> =
        if (category == "Todas") auctions
        else auctions.filter { it.category.equals(category, ignoreCase = true) }
}