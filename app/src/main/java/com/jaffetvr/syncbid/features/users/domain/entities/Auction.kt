package com.jaffetvr.syncbid.features.users.domain.entities

data class Auction(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val imageUrl: String?,
    val basePrice: Double,
    val currentPrice: Double,
    val timeRemainingSeconds: Long,
    val bidCount: Int,
    val status: AuctionStatus,
    val leaderId: String?,
    val leaderName: String?,
    val isUserWinning: Boolean
)

enum class AuctionStatus {
    LIVE, PENDING, ENDED;

    companion object {
        fun fromString(value: String): AuctionStatus = when (value.uppercase()) {
            "LIVE", "ACTIVE" -> LIVE
            "PENDING" -> PENDING
            "ENDED", "FINISHED" -> ENDED
            else -> LIVE
        }
    }
}
