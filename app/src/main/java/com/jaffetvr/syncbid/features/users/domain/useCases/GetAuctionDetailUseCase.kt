package com.jaffetvr.syncbid.features.users.domain.useCases

import com.jaffetvr.syncbid.features.users.domain.entities.Auction
import com.jaffetvr.syncbid.features.users.domain.repositories.AuctionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAuctionDetailUseCase @Inject constructor(
    private val auctionRepository: AuctionRepository
) {
    operator fun invoke(auctionId: String): Flow<Auction?> =
        auctionRepository.observeAuctionById(auctionId)
}
