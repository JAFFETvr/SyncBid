package com.jaffetvr.syncbid.features.admin.domain.useCases

import com.jaffetvr.syncbid.features.admin.domain.entities.AdminStats
import com.jaffetvr.syncbid.features.admin.domain.repositories.AdminRepository
import javax.inject.Inject

class GetAdminStatsUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    suspend operator fun invoke(): Result<AdminStats> =
        repository.getStats()
}
