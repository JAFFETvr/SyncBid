package com.jaffetvr.syncbid.features.admin.domain.repositories

import com.jaffetvr.syncbid.features.admin.domain.entities.AdminStats
import com.jaffetvr.syncbid.features.admin.domain.entities.CreatedAuction
import com.jaffetvr.syncbid.features.admin.domain.entities.InventoryItem

interface AdminRepository {

    suspend fun createAuction(
        name: String,
        description: String,
        basePrice: Double,
        durationHours: Int,
        category: String
    ): Result<CreatedAuction>

    suspend fun getInventory(): Result<List<InventoryItem>>

    suspend fun getStats(): Result<AdminStats>
}
