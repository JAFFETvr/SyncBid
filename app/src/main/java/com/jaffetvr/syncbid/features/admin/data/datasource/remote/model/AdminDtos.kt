package com.jaffetvr.syncbid.features.admin.data.datasource.remote.model

import com.google.gson.annotations.SerializedName

data class CreateAuctionRequestDto(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("basePrice") val basePrice: Double,
    @SerializedName("durationHours") val durationHours: Int,
    @SerializedName("category") val category: String
)

data class CreateAuctionResponseDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: String,
    @SerializedName("createdAt") val createdAt: String
)

data class InventoryItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("basePrice") val basePrice: Double,
    @SerializedName("currentPrice") val currentPrice: Double?,
    @SerializedName("bidCount") val bidCount: Int,
    @SerializedName("status") val status: String,
    @SerializedName("timeRemainingSeconds") val timeRemainingSeconds: Long?,
    @SerializedName("scheduledAt") val scheduledAt: String?
)

data class AdminStatsDto(
    @SerializedName("totalRevenue") val totalRevenue: Double,
    @SerializedName("activeBids") val activeBids: Int,
    @SerializedName("liveAuctions") val liveAuctions: Int,
    @SerializedName("onlineUsers") val onlineUsers: Int,
    @SerializedName("recentActivity") val recentActivity: List<ActivityEventDto>
)

data class ActivityEventDto(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("title") val title: String,
    @SerializedName("timeAgo") val timeAgo: String,
    @SerializedName("value") val value: String?,
    @SerializedName("valueType") val valueType: String?
)
