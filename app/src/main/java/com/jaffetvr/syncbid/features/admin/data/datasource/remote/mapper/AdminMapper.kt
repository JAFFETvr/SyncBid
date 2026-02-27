package com.jaffetvr.syncbid.features.admin.data.datasource.remote.mapper

import com.jaffetvr.syncbid.features.admin.data.datasource.remote.model.ActivityEventDto
import com.jaffetvr.syncbid.features.admin.data.datasource.remote.model.AdminStatsDto
import com.jaffetvr.syncbid.features.admin.data.datasource.remote.model.CreateAuctionResponseDto
import com.jaffetvr.syncbid.features.admin.data.datasource.remote.model.InventoryItemDto
import com.jaffetvr.syncbid.features.admin.domain.entities.ActivityEvent
import com.jaffetvr.syncbid.features.admin.domain.entities.ActivityType
import com.jaffetvr.syncbid.features.admin.domain.entities.AdminStats
import com.jaffetvr.syncbid.features.admin.domain.entities.CreatedAuction
import com.jaffetvr.syncbid.features.admin.domain.entities.InventoryItem
import com.jaffetvr.syncbid.features.admin.domain.entities.InventoryStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Función auxiliar para convertir la fecha del servidor en segundos
private fun isoToSecondsRemaining(isoString: String?): Long {
    if (isoString.isNullOrBlank()) return 0L
    return try {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val endTime = LocalDateTime.parse(isoString, formatter)
        val now = LocalDateTime.now()
        val seconds = java.time.Duration.between(now, endTime).seconds
        if (seconds < 0) 0L else seconds
    } catch (e: Exception) {
        0L
    }
}

fun CreateAuctionResponseDto.toDomain(): CreatedAuction = CreatedAuction(
    id = id.toString(),
    name = title,
    status = status,
    createdAt = ""
)

// CORRECCIÓN: Adaptar los campos del nuevo DTO al dominio visual
fun InventoryItemDto.toDomain(): InventoryItem = InventoryItem(
    id = id.toString(),
    name = title, // backend envía title, usamos name en App
    basePrice = currentPrice ?: 0.0, // backend no manda basePrice por separado en esta lista
    currentPrice = currentPrice,
    bidCount = bidCount ?: 0,
    status = InventoryStatus.fromString(status),
    timeRemainingSeconds = isoToSecondsRemaining(endTime),
    scheduledAt = endTime
)

fun AdminStatsDto.toDomain(): AdminStats = AdminStats(
    totalRevenue = totalRevenue,
    activeBids = activeBids,
    liveAuctions = liveAuctions,
    onlineUsers = onlineUsers,
    recentActivity = recentActivity.map { it.toDomain() }
)

fun ActivityEventDto.toDomain(): ActivityEvent = ActivityEvent(
    id = id,
    type = ActivityType.fromString(type),
    title = title,
    timeAgo = timeAgo,
    value = value,
    valueType = valueType
)