package com.jaffetvr.syncbid.features.admin.domain.useCases

import com.jaffetvr.syncbid.features.admin.domain.entities.CreatedAuction
import com.jaffetvr.syncbid.features.admin.domain.repositories.AdminRepository
import javax.inject.Inject

class CreateAuctionUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    suspend operator fun invoke(
        name: String,
        description: String,
        basePrice: Double,
        durationHours: Int,
        category: String
    ): Result<CreatedAuction> =
        repository.createAuction(name, description, basePrice, durationHours, category)
}
