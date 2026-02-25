package com.jaffetvr.syncbid.features.users.data.datasource.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "auctions")
data class AuctionEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val category: String,
    val imageUrl: String?,
    val basePrice: Double,
    val currentPrice: Double,
    val timeRemainingSeconds: Long,
    val bidCount: Int,
    val status: String,
    val leaderId: String?,
    val leaderName: String?,
    val isUserWinning: Boolean
)
