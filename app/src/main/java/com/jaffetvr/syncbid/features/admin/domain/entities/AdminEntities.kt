package com.jaffetvr.syncbid.features.admin.domain.entities

data class CreatedAuction(
    val id: String,
    val name: String,
    val status: String,
    val createdAt: String
)

data class InventoryItem(
    val id: String,
    val name: String,
    val basePrice: Double,
    val currentPrice: Double?,
    val bidCount: Int,
    val status: InventoryStatus,
    val timeRemainingSeconds: Long?,
    val scheduledAt: String?
)

enum class InventoryStatus {
    ACTIVE, PENDING, ENDED;

    companion object {
        fun fromString(value: String): InventoryStatus = when (value.uppercase()) {
            "ACTIVE", "LIVE" -> ACTIVE
            "PENDING" -> PENDING
            "ENDED", "FINISHED" -> ENDED
            else -> PENDING
        }
    }
}

data class AdminStats(
    val totalRevenue: Double,
    val activeBids: Int,
    val liveAuctions: Int,
    val onlineUsers: Int,
    val recentActivity: List<ActivityEvent>
)

data class ActivityEvent(
    val id: String,
    val type: ActivityType,
    val title: String,
    val timeAgo: String,
    val value: String?,
    val valueType: String?
)

enum class ActivityType {
    BID_WON, AUCTION_CREATED, ERROR, AUCTION_ENDED, USER_REGISTERED;

    companion object {
        fun fromString(value: String): ActivityType = when (value.uppercase()) {
            "BID_WON" -> BID_WON
            "AUCTION_CREATED" -> AUCTION_CREATED
            "ERROR" -> ERROR
            "AUCTION_ENDED" -> AUCTION_ENDED
            "USER_REGISTERED" -> USER_REGISTERED
            else -> AUCTION_CREATED
        }
    }
}
