package com.jaffetvr.syncbid.features.users.presentation.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaffetvr.syncbid.features.users.domain.entities.Auction
import com.jaffetvr.syncbid.features.users.domain.entities.AuctionStatus
import com.jaffetvr.syncbid.features.users.domain.useCases.GetAuctionDetailUseCase
import com.jaffetvr.syncbid.features.users.domain.useCases.PlaceBidUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class BidStatus { IDLE, PROCESSING, SUCCESS, ERROR }

data class AuctionDetailUiState(
    val auction: Auction? = null,
    val isLoading: Boolean = true,
    val bidStatus: BidStatus = BidStatus.IDLE,
    val selectedIncrement: Double = 50.0,
    val error: String? = null,
    val isRolledBack: Boolean = false
)

sealed interface AuctionDetailUiEvent {
    data class ShowError(val message: String) : AuctionDetailUiEvent
    data class ShowSuccess(val message: String) : AuctionDetailUiEvent
}

@HiltViewModel
class AuctionDetailViewModel @Inject constructor(
    private val getAuctionDetailUseCase: GetAuctionDetailUseCase,
    private val placeBidUseCase: PlaceBidUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val auctionId: String = savedStateHandle["auctionId"] ?: ""

    private val _uiState = MutableStateFlow(AuctionDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuctionDetailUiEvent>()
    val events = _events.asSharedFlow()

    init {
        observeAuction()
    }

    private fun observeAuction() {
        viewModelScope.launch {
            getAuctionDetailUseCase(auctionId).collect { auction ->
                if (auction != null) {
                    _uiState.update { it.copy(auction = auction, isLoading = false) }
                }
            }
        }
    }

    fun onIncrementSelected(amount: Double) {
        _uiState.update { it.copy(selectedIncrement = amount) }
    }

    /**
     * Puja con actualización optimista + rollback en caso de error.
     *
     * 1. Guarda estado previo (para rollback)
     * 2. Actualiza UI inmediatamente (optimista)
     * 3. Envía puja al servidor
     * 4. Si falla → revierte al estado previo + muestra error
     */
    fun onPlaceBid() {
        val currentAuction = _uiState.value.auction ?: return
        val bidAmount = currentAuction.currentPrice + _uiState.value.selectedIncrement

        // ─── 1. Guardar estado previo para posible rollback ───
        val previousState = _uiState.value

        // ─── 2. Actualización optimista inmediata ───
        _uiState.update { state ->
            state.copy(
                auction = currentAuction.copy(
                    currentPrice = bidAmount,
                    bidCount = currentAuction.bidCount + 1,
                    isUserWinning = true,
                    leaderName = "Tú"
                ),
                bidStatus = BidStatus.PROCESSING,
                isRolledBack = false,
                error = null
            )
        }

        // ─── 3. Enviar al servidor ───
        viewModelScope.launch {
            placeBidUseCase(auctionId, bidAmount).fold(
                onSuccess = { bid ->
                    // ─── Confirmación exitosa ───
                    _uiState.update { it.copy(bidStatus = BidStatus.SUCCESS) }
                    _events.emit(AuctionDetailUiEvent.ShowSuccess("Puja confirmada: \$${bid.amount}"))

                    // Resetear estado después de 2s
                    kotlinx.coroutines.delay(2000)
                    _uiState.update { it.copy(bidStatus = BidStatus.IDLE) }
                },
                onFailure = { error ->
                    // ─── 4. ROLLBACK: Revertir al estado previo ───
                    _uiState.update {
                        previousState.copy(
                            bidStatus = BidStatus.ERROR,
                            isRolledBack = true,
                            error = "Error de sincronización: ${error.message}"
                        )
                    }
                    _events.emit(
                        AuctionDetailUiEvent.ShowError(
                            "Puja revertida · Conflicto de concurrencia"
                        )
                    )
                }
            )
        }
    }

    fun onRetryBid() {
        _uiState.update { it.copy(bidStatus = BidStatus.IDLE, isRolledBack = false, error = null) }
        onPlaceBid()
    }
}
