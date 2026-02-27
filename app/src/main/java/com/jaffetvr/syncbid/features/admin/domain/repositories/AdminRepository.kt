package com.jaffetvr.syncbid.features.admin.domain.repositories

import android.net.Uri
import com.jaffetvr.syncbid.features.admin.domain.entities.AdminStats
import com.jaffetvr.syncbid.features.admin.domain.entities.CreatedAuction
import com.jaffetvr.syncbid.features.admin.domain.entities.InventoryItem

interface AdminRepository {

    suspend fun createAuction(
        name: String,
        description: String,
        basePrice: Double,
        durationHours: Int,
        imageUri: Uri? // Se eliminó 'category' y se agregó la imagen
    ): Result<CreatedAuction>

    suspend fun getInventory(): Result<List<InventoryItem>>

    suspend fun getStats(): Result<AdminStats>
}