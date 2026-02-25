package com.jaffetvr.syncbid.features.users.domain.useCases

import com.jaffetvr.syncbid.features.users.domain.entities.Auction
import com.jaffetvr.syncbid.features.users.domain.repositories.AuctionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAuctionsUseCase @Inject constructor(
    private val auctionRepository: AuctionRepository
) {
    /**
     * Observa la lista de subastas desde Room (SSOT).
     * También refresca desde la API si se solicita.
     */
    operator fun invoke(): Flow<List<Auction>> =
        auctionRepository.observeAuctions()

    suspend fun refresh(): Result<Unit> =
        auctionRepository.refreshAuctions()
}
