package com.jaffetvr.syncbid.features.admin.domain.useCases

import android.net.Uri
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
        durationMinutes: Int,
        imageUri: Uri?
    ): Result<CreatedAuction> =
        repository.createAuction(
            name = name,
            description = description,
            basePrice = basePrice,
            durationMinutes = durationMinutes,
            imageUri = imageUri
        )
}