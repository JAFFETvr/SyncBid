package com.jaffetvr.syncbid.features.users.domain.useCases

import com.jaffetvr.syncbid.features.users.domain.entities.Bid
import com.jaffetvr.syncbid.features.users.domain.repositories.AuctionRepository
import javax.inject.Inject

class PlaceBidUseCase @Inject constructor(
    private val auctionRepository: AuctionRepository
) {
    suspend operator fun invoke(auctionId: String, amount: Double): Result<Bid> =
        auctionRepository.placeBid(auctionId, amount)
}
