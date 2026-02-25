package com.jaffetvr.syncbid.features.users.data.datasource.remote.model

import com.google.gson.annotations.SerializedName

data class AuctionDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("category") val category: String,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("basePrice") val basePrice: Double,
    @SerializedName("currentPrice") val currentPrice: Double,
    @SerializedName("timeRemainingSeconds") val timeRemainingSeconds: Long,
    @SerializedName("bidCount") val bidCount: Int,
    @SerializedName("status") val status: String,
    @SerializedName("leaderId") val leaderId: String?,
    @SerializedName("leaderName") val leaderName: String?,
    @SerializedName("isUserWinning") val isUserWinning: Boolean
)

data class BidRequestDto(
    @SerializedName("amount") val amount: Double
)

data class BidResponseDto(
    @SerializedName("bidId") val bidId: String,
    @SerializedName("auctionId") val auctionId: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("userId") val userId: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("isLeader") val isLeader: Boolean
)

/** WebSocket event DTO for real-time auction updates */
data class AuctionUpdateDto(
    @SerializedName("auctionId") val auctionId: String,
    @SerializedName("currentPrice") val currentPrice: Double,
    @SerializedName("bidCount") val bidCount: Int,
    @SerializedName("timeRemainingSeconds") val timeRemainingSeconds: Long,
    @SerializedName("leaderId") val leaderId: String?,
    @SerializedName("leaderName") val leaderName: String?,
    @SerializedName("status") val status: String
)
