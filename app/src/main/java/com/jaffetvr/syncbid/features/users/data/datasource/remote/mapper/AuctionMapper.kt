package com.jaffetvr.syncbid.features.users.data.datasource.remote.mapper

import com.jaffetvr.syncbid.features.users.data.datasource.local.model.AuctionEntity
import com.jaffetvr.syncbid.features.users.data.datasource.remote.model.AuctionDto
import com.jaffetvr.syncbid.features.users.data.datasource.remote.model.BidResponseDto
import com.jaffetvr.syncbid.features.users.domain.entities.Auction
import com.jaffetvr.syncbid.features.users.domain.entities.AuctionStatus
import com.jaffetvr.syncbid.features.users.domain.entities.Bid

// ─── DTO → Domain ───

fun AuctionDto.toDomain(): Auction = Auction(
    id = id,
    name = name,
    description = description,
    category = category,
    imageUrl = imageUrl,
    basePrice = basePrice,
    currentPrice = currentPrice,
    timeRemainingSeconds = timeRemainingSeconds,
    bidCount = bidCount,
    status = AuctionStatus.fromString(status),
    leaderId = leaderId,
    leaderName = leaderName,
    isUserWinning = isUserWinning
)

fun BidResponseDto.toDomain(): Bid = Bid(
    bidId = bidId,
    auctionId = auctionId,
    amount = amount,
    userId = userId,
    timestamp = timestamp,
    isLeader = isLeader
)

// ─── DTO → Entity (para Room SSOT) ───

fun AuctionDto.toEntity(): AuctionEntity = AuctionEntity(
    id = id,
    name = name,
    description = description,
    category = category,
    imageUrl = imageUrl,
    basePrice = basePrice,
    currentPrice = currentPrice,
    timeRemainingSeconds = timeRemainingSeconds,
    bidCount = bidCount,
    status = status,
    leaderId = leaderId,
    leaderName = leaderName,
    isUserWinning = isUserWinning
)

// ─── Entity → Domain ───

fun AuctionEntity.toDomain(): Auction = Auction(
    id = id,
    name = name,
    description = description,
    category = category,
    imageUrl = imageUrl,
    basePrice = basePrice,
    currentPrice = currentPrice,
    timeRemainingSeconds = timeRemainingSeconds,
    bidCount = bidCount,
    status = AuctionStatus.fromString(status),
    leaderId = leaderId,
    leaderName = leaderName,
    isUserWinning = isUserWinning
)
