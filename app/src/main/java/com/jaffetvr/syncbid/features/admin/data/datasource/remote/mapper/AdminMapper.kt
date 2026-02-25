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

fun CreateAuctionResponseDto.toDomain(): CreatedAuction = CreatedAuction(
    id = id,
    name = name,
    status = status,
    createdAt = createdAt
)

fun InventoryItemDto.toDomain(): InventoryItem = InventoryItem(
    id = id,
    name = name,
    basePrice = basePrice,
    currentPrice = currentPrice,
    bidCount = bidCount,
    status = InventoryStatus.fromString(status),
    timeRemainingSeconds = timeRemainingSeconds,
    scheduledAt = scheduledAt
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
