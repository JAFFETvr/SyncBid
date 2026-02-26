package com.jaffetvr.syncbid.features.users.presentation.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaffetvr.syncbid.features.users.domain.entities.Auction
import com.jaffetvr.syncbid.features.users.domain.useCases.GetAuctionDetailUseCase
import com.jaffetvr.syncbid.features.users.domain.useCases.PlaceBidUseCase
import com.jaffetvr.syncbid.features.users.presentation.components.formatTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
    val isRolledBack: Boolean = false,
    val displayCurrentPrice: String = "$0",
    val displayBidAmount: String = "$0",
    val displayTimeRemaining: String = "00:00",
    val isTimeCritical: Boolean = false,
    val bidLabelStatus: String = "Precio"
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
                    _uiState.update { it.copy(auction = auction, isLoading = false).mapToDisplay() }
                }
            }
        }
    }

    fun onIncrementSelected(amount: Double) {
        _uiState.update { it.copy(selectedIncrement = amount).mapToDisplay() }
    }

    private fun AuctionDetailUiState.mapToDisplay(): AuctionDetailUiState {
        val curr = auction ?: return this
        val nextBid = curr.currentPrice + selectedIncrement

        return this.copy(
            displayCurrentPrice = "$${String.format("%,.0f", curr.currentPrice)}",
            displayBidAmount = "$${String.format("%,.0f", nextBid)}",
            displayTimeRemaining = formatTime(curr.timeRemainingSeconds),
            isTimeCritical = curr.timeRemainingSeconds < 30,
            bidLabelStatus = when (bidStatus) {
                BidStatus.PROCESSING -> "Enviando…"
                BidStatus.ERROR -> "↩ Revertido"
                else -> "Precio"
            }
        )
    }

    fun onPlaceBid() {
        val currentAuction = _uiState.value.auction ?: return
        val bidAmount = currentAuction.currentPrice + _uiState.value.selectedIncrement
        val previousState = _uiState.value

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
            ).mapToDisplay()
        }

        viewModelScope.launch {
            placeBidUseCase(auctionId, bidAmount).fold(
                onSuccess = {
                    _uiState.update { it.copy(bidStatus = BidStatus.SUCCESS).mapToDisplay() }
                    _events.emit(AuctionDetailUiEvent.ShowSuccess("Puja confirmada"))
                    delay(2000)
                    _uiState.update { it.copy(bidStatus = BidStatus.IDLE).mapToDisplay() }
                },
                onFailure = { error ->
                    _uiState.update {
                        previousState.copy(
                            bidStatus = BidStatus.ERROR,
                            isRolledBack = true,
                            error = error.message
                        ).mapToDisplay()
                    }
                    _events.emit(AuctionDetailUiEvent.ShowError("Puja revertida"))
                }
            )
        }
    }

    fun onRetryBid() {
        _uiState.update { it.copy(bidStatus = BidStatus.IDLE, isRolledBack = false).mapToDisplay() }
        onPlaceBid()
    }
}