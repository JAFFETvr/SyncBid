package com.jaffetvr.syncbid.features.admin.domain.useCases

import com.jaffetvr.syncbid.features.admin.domain.entities.InventoryItem
import com.jaffetvr.syncbid.features.admin.domain.repositories.AdminRepository
import javax.inject.Inject

class GetInventoryUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    suspend operator fun invoke(): Result<List<InventoryItem>> =
        repository.getInventory()
}
