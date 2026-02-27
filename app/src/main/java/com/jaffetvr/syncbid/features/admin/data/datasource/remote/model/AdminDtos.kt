package com.jaffetvr.syncbid.features.admin.data.datasource.remote.model

import com.google.gson.annotations.SerializedName

data class ApiResponseDto<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T?
)

data class CreateAuctionRequestDto(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("startingPrice") val startingPrice: Double,
    @SerializedName("endTime") val endTime: String
)

data class CreateAuctionResponseDto(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("status") val status: String
)

// CORRECCIÓN: Ahora coincide con AuctionResponse.java del servidor
data class InventoryItemDto(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("currentPrice") val currentPrice: Double?,
    @SerializedName("endTime") val endTime: String?,
    @SerializedName("status") val status: String,
    @SerializedName("winnerUsername") val winnerUsername: String?
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