package com.jaffetvr.syncbid.features.admin.data.datasource.remote.model

import com.google.gson.annotations.SerializedName

// 1. Añadimos esta clase para mapear el "ApiResponse" de tu Spring Boot
data class ApiResponseDto<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T?
)

// 2. Coincide exactamente con AuctionCreateRequest.java del backend
data class CreateAuctionRequestDto(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("startingPrice") val startingPrice: Double,
    @SerializedName("endTime") val endTime: String // Formato ISO-8601 ej: "2026-03-01T15:30:00"
)

// 3. Coincide con AuctionResponse.java del backend (lo esencial)
data class CreateAuctionResponseDto(
    @SerializedName("id") val id: Long, // Usualmente en Spring Boot los IDs son Long
    @SerializedName("title") val title: String,
    @SerializedName("status") val status: String
    // Puedes agregar más campos de AuctionResponse si los necesitas mostrar
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