package com.jaffetvr.syncbid.features.users.domain.entities

data class Bid(
    val bidId: String,
    val auctionId: String,
    val amount: Double,
    val userId: String,
    val timestamp: Long,
    val isLeader: Boolean
)
