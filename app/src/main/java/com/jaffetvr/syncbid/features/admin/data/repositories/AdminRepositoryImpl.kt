package com.jaffetvr.syncbid.features.admin.data.repositories

import com.jaffetvr.syncbid.features.admin.data.datasource.remote.api.AdminApi
import com.jaffetvr.syncbid.features.admin.data.datasource.remote.mapper.toDomain
import com.jaffetvr.syncbid.features.admin.data.datasource.remote.model.CreateAuctionRequestDto
import com.jaffetvr.syncbid.features.admin.domain.entities.AdminStats
import com.jaffetvr.syncbid.features.admin.domain.entities.CreatedAuction
import com.jaffetvr.syncbid.features.admin.domain.entities.InventoryItem
import com.jaffetvr.syncbid.features.admin.domain.repositories.AdminRepository
import javax.inject.Inject

class AdminRepositoryImpl @Inject constructor(
    private val adminApi: AdminApi
) : AdminRepository {

    override suspend fun createAuction(
        name: String,
        description: String,
        basePrice: Double,
        durationHours: Int,
        category: String
    ): Result<CreatedAuction> =
        try {
            val response = adminApi.createAuction(
                CreateAuctionRequestDto(name, description, basePrice, durationHours, category)
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Error al crear subasta: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun getInventory(): Result<List<InventoryItem>> =
        try {
            val response = adminApi.getInventory()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toDomain() })
            } else {
                Result.failure(Exception("Error al cargar inventario: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun getStats(): Result<AdminStats> =
        try {
            val response = adminApi.getStats()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Error al cargar estadísticas: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
}
